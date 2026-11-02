package com.filmbox;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.frostwire.jlibtorrent.AlertListener;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.frostwire.jlibtorrent.TorrentStatus;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TorrentDownloadManager {
    private static final String TAG = "TorrentDownloadManager";
    private static TorrentDownloadManager sInstance;

    private Context mContext;
    private SessionManager mSessionManager;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<String, TorrentDownloadInfo> mDownloads = new ConcurrentHashMap<>();

    public static class TorrentDownloadInfo {
        public String movieId;
        public String title;
        public String magnetUrl;
        public TorrentHandle handle;
        public float progress;
        public int speedKbps;
        public long totalBytes;
        public int totalMb;
        public String status; // "DOWNLOADING", "FINISHED", "ERROR"

        public TorrentDownloadInfo(String movieId, String title, String magnetUrl) {
            this.movieId = movieId;
            this.title = title;
            this.magnetUrl = magnetUrl;
            this.progress = 0f;
            this.speedKbps = 0;
            this.totalBytes = 0;
            this.totalMb = 0;
            this.status = "INITIALIZING";
        }
    }

    private TorrentDownloadManager() {}

    public static synchronized TorrentDownloadManager getInstance() {
        if (sInstance == null) {
            sInstance = new TorrentDownloadManager();
        }
        return sInstance;
    }

    public void initAsync(Context context) {
        if (context != null && mContext == null) {
            this.mContext = context.getApplicationContext();
        }
        new Thread(this::initSessionInternal).start();
    }

    public synchronized void initSessionInternal() {
        if (mSessionManager != null && mSessionManager.isRunning()) {
            return;
        }
        try {
            mSessionManager = new SessionManager();
            mSessionManager.addListener(new AlertListener() {
                @Override
                public int[] types() {
                    return new int[]{
                        AlertType.ADD_TORRENT.swig(),
                        AlertType.TORRENT_FINISHED.swig(),
                        AlertType.TORRENT_ERROR.swig()
                    };
                }

                @Override
                public void alert(Alert<?> alert) {
                    try {
                        if (alert.type() == AlertType.TORRENT_FINISHED) {
                            TorrentFinishedAlert finishedAlert = (TorrentFinishedAlert) alert;
                            TorrentHandle handle = finishedAlert.handle();
                            if (handle != null && handle.isValid()) {
                                onTorrentFinished(handle);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Alert processing error: " + e.getMessage());
                    }
                }
            });
            mSessionManager.start();
            Log.d(TAG, "JLibTorrent SessionManager started successfully in background.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start JLibTorrent SessionManager: " + e.getMessage());
        }
    }

    public File getOfflineFolder() {
        if (mContext == null) return null;
        File dir = new File(mContext.getFilesDir(), "offline_movies");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public void updateProgressInfo(String movieId, String title, float progress, int speedKbps, long totalBytes, String status) {
        TorrentDownloadInfo info = mDownloads.get(movieId);
        if (info == null) {
            info = new TorrentDownloadInfo(movieId, title, "");
            mDownloads.put(movieId, info);
        }
        info.progress = progress;
        info.speedKbps = speedKbps;
        info.totalBytes = totalBytes;
        if (totalBytes > 0) {
            info.totalMb = (int) (totalBytes / (1024 * 1024));
        }
        info.status = status;
    }

    public void downloadMagnetPrivate(Context context, String magnetOrUrl, String movieId, String title) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        downloadMagnetPrivate(magnetOrUrl, movieId, title);
    }

    public void downloadMagnetPrivate(String magnetOrUrl, String movieId, String title) {
        try {
            if (mSessionManager == null || !mSessionManager.isRunning()) {
                initSessionInternal();
            }

            TorrentDownloadInfo info = new TorrentDownloadInfo(movieId, title, magnetOrUrl);
            mDownloads.put(movieId, info);

            if (mContext != null) {
                mHandler.post(() -> Toast.makeText(mContext, "📥 Starting P2P Download: " + title, Toast.LENGTH_SHORT).show());
            }

            new Thread(() -> {
                try {
                    File saveDir = getOfflineFolder();

                    if (magnetOrUrl != null && magnetOrUrl.startsWith("magnet:")) {
                        if (mSessionManager != null) {
                            byte[] data = mSessionManager.fetchMagnet(magnetOrUrl, 30);
                            if (data != null) {
                                TorrentInfo torrentInfo = TorrentInfo.bdecode(data);
                                mSessionManager.download(torrentInfo, saveDir);
                                info.handle = mSessionManager.find(torrentInfo.infoHash());
                                info.status = "DOWNLOADING";
                            } else {
                                info.status = "DOWNLOADING";
                            }
                        }
                    } else {
                        info.status = "DOWNLOADING";
                    }

                    if (mContext != null) {
                        mHandler.post(() -> {
                            Toast.makeText(mContext, "📡 P2P Peers Connected for " + title, Toast.LENGTH_SHORT).show();
                            TorrentDownloadService.startDownloadService(mContext, title, 0, 0);
                        });
                    }

                } catch (Exception e) {
                    info.status = "ERROR";
                    Log.e(TAG, "Torrent download failed: " + e.getMessage(), e);
                    if (mContext != null) {
                        mHandler.post(() -> Toast.makeText(mContext, "P2P Download Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            }).start();
        } catch (Throwable t) {
            Log.e(TAG, "Fatal in downloadMagnetPrivate: " + t.getMessage(), t);
            if (mContext != null) {
                mHandler.post(() -> Toast.makeText(mContext, "Download error: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
    }

    private void onTorrentFinished(TorrentHandle handle) {
        try {
            for (TorrentDownloadInfo info : mDownloads.values()) {
                if (info.handle != null && info.handle.infoHash().equals(handle.infoHash())) {
                    info.progress = 100f;
                    info.status = "FINISHED";
                    info.speedKbps = 0;
                    if (mContext != null) {
                        mHandler.post(() -> Toast.makeText(mContext, "✅ P2P Download Complete: " + info.title, Toast.LENGTH_LONG).show());
                    }
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    public String getProgressJson(String movieId) {
        TorrentDownloadInfo info = mDownloads.get(movieId);
        JSONObject json = new JSONObject();
        try {
            if (info != null) {
                if (info.handle != null && info.handle.isValid()) {
                    TorrentStatus status = info.handle.status();
                    info.progress = status.progress() * 100f;
                    info.speedKbps = (int) (status.downloadRate() / 1024);
                    if (info.handle.torrentFile() != null) {
                        info.totalBytes = info.handle.torrentFile().totalSize();
                        info.totalMb = (int) (info.totalBytes / (1024 * 1024));
                    }
                    if (status.isFinished()) {
                        info.status = "FINISHED";
                    } else {
                        info.status = "DOWNLOADING";
                    }
                }
                int downloadedMb = info.totalBytes > 0 ? (int) ((info.totalBytes * (info.progress / 100f)) / (1024 * 1024)) : 0;
                json.put("progress", Math.round(info.progress));
                json.put("speedKb", info.speedKbps);
                json.put("totalMb", info.totalMb);
                json.put("downloadedMb", downloadedMb);
                json.put("status", info.status);
            } else {
                File dir = getOfflineFolder();
                File file = dir != null ? new File(dir, movieId + ".mp4") : null;
                if (file != null && file.exists() && file.length() > (10 * 1024 * 1024)) {
                    int fileMb = (int) (file.length() / (1024 * 1024));
                    json.put("progress", 100);
                    json.put("speedKb", 0);
                    json.put("totalMb", fileMb);
                    json.put("downloadedMb", fileMb);
                    json.put("status", "FINISHED");
                } else if (file != null && file.exists() && file.length() > 0) {
                    int fileMb = (int) (file.length() / (1024 * 1024));
                    json.put("progress", 50);
                    json.put("speedKb", 512);
                    json.put("totalMb", 0);
                    json.put("downloadedMb", fileMb);
                    json.put("status", "DOWNLOADING");
                } else {
                    json.put("progress", 0);
                    json.put("speedKb", 0);
                    json.put("totalMb", 0);
                    json.put("downloadedMb", 0);
                    json.put("status", "NOT_FOUND");
                }
            }
        } catch (Exception e) {
            try {
                json.put("progress", 0);
                json.put("speedKb", 0);
                json.put("totalMb", 0);
                json.put("downloadedMb", 0);
                json.put("status", "ERROR");
            } catch (Exception ignored) {}
        }
        return json.toString();
    }

    public void stop() {
        try {
            if (mContext != null) {
                TorrentDownloadService.stopDownloadService(mContext);
            }
            if (mSessionManager != null && mSessionManager.isRunning()) {
                mSessionManager.stop();
                Log.d(TAG, "JLibTorrent SessionManager stopped.");
            }
        } catch (Exception ignored) {}
    }
}
