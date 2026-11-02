package com.filmbox;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final OkHttpClient mOkHttpClient;

    public WebAppInterface(Context context) {
        this.mContext = context;
        if (context != null) {
            TorrentDownloadManager.getInstance().initAsync(context);
        }
        this.mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    private File getOfflineFolder() {
        File dir = new File(mContext.getFilesDir(), "offline_movies");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @JavascriptInterface
    public void downloadMoviePrivate(String videoUrl, String movieId, String title) {
        try {
            Log.d(TAG, "downloadMoviePrivate called for: " + title + " (" + movieId + ") -> URL: " + videoUrl);
            mHandler.post(() -> Toast.makeText(mContext, "📥 Starting real download for " + title + "...", Toast.LENGTH_SHORT).show());
            TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, 0f, 0, 0, "DOWNLOADING");

            new Thread(() -> {
                File dir = getOfflineFolder();
                File targetFile = new File(dir, movieId + ".mp4");

                try {
                    Request.Builder requestBuilder = new Request.Builder()
                            .url(videoUrl)
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                            .header("Accept", "*/*");

                    try {
                        String cookies = CookieManager.getInstance().getCookie(videoUrl);
                        if (cookies != null && !cookies.isEmpty()) {
                            requestBuilder.header("Cookie", cookies);
                        }
                    } catch (Exception ignored) {}

                    Request request = requestBuilder.build();

                    try (Response response = mOkHttpClient.newCall(request).execute()) {
                        Log.d(TAG, "Download HTTP response: " + response.code() + " for URL: " + videoUrl);

                        if (response.isSuccessful() && response.body() != null) {
                            ResponseBody body = response.body();
                            String contentType = body.contentType() != null ? body.contentType().toString().toLowerCase() : "";

                            if (contentType.contains("text/html")) {
                                Log.w(TAG, "Stream endpoint returned HTML page instead of video binary.");
                                TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, 0f, 0, 0, "ERROR");
                                mHandler.post(() -> Toast.makeText(mContext, "❌ Download failed: Stream returned HTML page.", Toast.LENGTH_LONG).show());
                                return;
                            }

                            long contentLength = body.contentLength();
                            TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, 0f, 0, contentLength, "DOWNLOADING");

                            long downloadedBytes = 0;
                            long startTime = System.currentTimeMillis();

                            try (InputStream input = body.byteStream();
                                 FileOutputStream output = new FileOutputStream(targetFile)) {
                                byte[] buffer = new byte[32768];
                                int count;
                                while ((count = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, count);
                                    downloadedBytes += count;

                                    long currentTime = System.currentTimeMillis();
                                    long timeDiff = (currentTime - startTime) / 1000;
                                    int speedKbps = timeDiff > 0 ? (int) ((downloadedBytes / 1024) / timeDiff) : 1024;
                                    float progressPct = contentLength > 0 ? ((float) downloadedBytes / contentLength) * 100f : 0f;

                                    TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, progressPct, speedKbps, contentLength, "DOWNLOADING");
                                }
                            }

                            TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, 100f, 0, targetFile.length(), "FINISHED");
                            mHandler.post(() -> Toast.makeText(mContext, "✅ Real Offline Download Complete: " + title + " (" + (targetFile.length() / (1024 * 1024)) + " MB)", Toast.LENGTH_LONG).show());
                        } else {
                            Log.e(TAG, "Download HTTP response error: " + response.code());
                            TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, 0f, 0, 0, "ERROR");
                            mHandler.post(() -> Toast.makeText(mContext, "❌ Download failed (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show());
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Network stream exception: " + ex.getMessage(), ex);
                    TorrentDownloadManager.getInstance().updateProgressInfo(movieId, title, 0f, 0, 0, "ERROR");
                    mHandler.post(() -> Toast.makeText(mContext, "❌ Download error: " + ex.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } catch (Throwable t) {
            Log.e(TAG, "Fatal in downloadMoviePrivate: " + t.getMessage(), t);
            mHandler.post(() -> Toast.makeText(mContext, "Download error: " + t.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @JavascriptInterface
    public void downloadTorrentPrivate(String magnetOrUrl, String movieId, String title) {
        try {
            Log.d(TAG, "downloadTorrentPrivate called for: " + title + " (" + movieId + ")");
            TorrentDownloadManager.getInstance().downloadMagnetPrivate(mContext, magnetOrUrl, movieId, title);
        } catch (Throwable t) {
            Log.e(TAG, "Fatal in downloadTorrentPrivate: " + t.getMessage(), t);
            mHandler.post(() -> Toast.makeText(mContext, "Download error: " + t.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @JavascriptInterface
    public String getTorrentProgress(String movieId) {
        try {
            return TorrentDownloadManager.getInstance().getProgressJson(movieId);
        } catch (Throwable t) {
            Log.e(TAG, "Error getting torrent progress: " + t.getMessage(), t);
            return "{\"progress\":0,\"speedKb\":0,\"status\":\"ERROR\"}";
        }
    }

    @JavascriptInterface
    public boolean isMovieDownloaded(String movieId) {
        try {
            File targetFile = new File(getOfflineFolder(), movieId + ".mp4");
            return targetFile.exists() && targetFile.length() > 1024; // Any valid non-empty video file (> 1KB)
        } catch (Throwable t) {
            Log.e(TAG, "Error checking if movie downloaded: " + t.getMessage(), t);
            return false;
        }
    }

    @JavascriptInterface
    public String getOfflineMoviePath(String movieId) {
        try {
            File targetFile = new File(getOfflineFolder(), movieId + ".mp4");
            if (targetFile.exists() && targetFile.length() > 1024) {
                return "file://" + targetFile.getAbsolutePath();
            }
        } catch (Throwable t) {
            Log.e(TAG, "Error getting offline movie path: " + t.getMessage(), t);
        }
        return "";
    }

    @JavascriptInterface
    public void openPlayer(int tmdbId, String mediaType, String title, String videoUrl, int seasonNumber, int episodeNumber) {
        try {
            mHandler.post(() -> {
                try {
                    android.content.Intent intent = new android.content.Intent(mContext, PlayerActivity.class);
                    intent.putExtra("tmdb_id", tmdbId);
                    intent.putExtra("media_type", mediaType != null ? mediaType : "movie");
                    intent.putExtra("title", title != null ? title : "Movie");
                    intent.putExtra("season_number", seasonNumber > 0 ? seasonNumber : 1);
                    intent.putExtra("episode_number", episodeNumber > 0 ? episodeNumber : 1);
                    intent.putExtra("video_url", videoUrl != null ? videoUrl : "");
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(mContext, "Error launching player: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Throwable t) {
            Log.e(TAG, "Fatal in openPlayer: " + t.getMessage(), t);
        }
    }
}
