package com.filmbox.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.filmbox.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DownloadManagerUtil {
    private static final String PREF_NAME = "FilmBoxDownloadsPref";
    private static final String KEY_DOWNLOADS = "downloaded_movies";

    private final SharedPreferences pref;
    private final Gson gson;

    public DownloadManagerUtil(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
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
