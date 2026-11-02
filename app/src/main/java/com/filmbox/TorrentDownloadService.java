package com.filmbox;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TorrentDownloadService extends Service {
    public static final String CHANNEL_ID = "filmbox_p2p_downloads";
    public static final String ACTION_START_FOREGROUND = "com.filmbox.action.START_DOWNLOAD_SERVICE";
    public static final String ACTION_STOP_FOREGROUND = "com.filmbox.action.STOP_DOWNLOAD_SERVICE";
    public static final String ACTION_UPDATE_PROGRESS = "com.filmbox.action.UPDATE_PROGRESS";

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_PROGRESS = "extra_progress";
    public static final String EXTRA_SPEED_KB = "extra_speed_kb";

    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START_FOREGROUND.equals(action) || ACTION_UPDATE_PROGRESS.equals(action)) {
                String title = intent.getStringExtra(EXTRA_TITLE);
                int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
                int speedKb = intent.getIntExtra(EXTRA_SPEED_KB, 0);

                if (title == null) title = "Film Box Movie";
                startForeground(NOTIFICATION_ID, createNotification(title, progress, speedKb));
            } else if (ACTION_STOP_FOREGROUND.equals(action)) {
                stopForeground(true);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Film Box P2P Downloads",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background status notifications for offline P2P downloads.");
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(String title, int progress, int speedKb) {
        String contentText = progress >= 100
                ? "Download Complete"
                : "Downloading (" + progress + "%) · " + speedKb + " KB/s";

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(100, progress, false)
                .setOngoing(progress < 100)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    public static void startDownloadService(Context context, String title, int progress, int speedKb) {
        try {
            Intent intent = new Intent(context, TorrentDownloadService.class);
            intent.setAction(ACTION_START_FOREGROUND);
            intent.putExtra(EXTRA_TITLE, title);
            intent.putExtra(EXTRA_PROGRESS, progress);
            intent.putExtra(EXTRA_SPEED_KB, speedKb);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception ignored) {}
    }

    public static void stopDownloadService(Context context) {
        try {
            Intent intent = new Intent(context, TorrentDownloadService.class);
            intent.setAction(ACTION_STOP_FOREGROUND);
            context.startService(intent);
        } catch (Exception ignored) {}
    }
}
