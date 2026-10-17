package com.filmbox.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.filmbox.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DownloadManagerUtil {
    private static final String PREF_NAME = "FilmBoxDownloadsPref";
    private static final String KEY_DOWNLOADS = "downloaded_movies";

    private final Context context;
    private final SharedPreferences pref;
    private final Gson gson;

    public DownloadManagerUtil(Context context) {
        this.context = context.getApplicationContext();
        this.pref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public long startRealDownload(Movie movie) {
        try {
            String videoUrl = movie.getVideoUrl();
            if (videoUrl == null || videoUrl.isEmpty()) {
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
            request.setTitle(movie.getTitle());
            request.setDescription("Downloading movie for offline viewing...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            // Save in app-specific downloads directory (no dangerous permissions required)
            File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir != null && !downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            String fileName = "movie_" + movie.getId() + ".mp4";
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = -1;
            if (dm != null) {
                downloadId = dm.enqueue(request);
                addDownload(movie);
                Toast.makeText(context, "Download started for " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            }
            return downloadId;
        } catch (Exception e) {
            Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    public void addDownload(Movie movie) {
        List<Movie> downloads = getDownloads();
        for (Movie m : downloads) {
            if (m.getId() == movie.getId()) {
                return;
            }
        }
        downloads.add(movie);
        saveDownloads(downloads);
    }

    public void removeDownload(int movieId) {
        List<Movie> downloads = getDownloads();
        List<Movie> updated = new ArrayList<>();
        for (Movie m : downloads) {
            if (m.getId() != movieId) {
                updated.add(m);
            }
        }
        saveDownloads(updated);
        try {
            File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir != null) {
                File file = new File(downloadDir, "movie_" + movieId + ".mp4");
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception ignored) {}
    }

    public List<Movie> getDownloads() {
        String json = pref.getString(KEY_DOWNLOADS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Movie>>() {}.getType();
        List<Movie> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    private void saveDownloads(List<Movie> downloads) {
        String json = gson.toJson(downloads);
        pref.edit().putString(KEY_DOWNLOADS, json).apply();
    }
}
