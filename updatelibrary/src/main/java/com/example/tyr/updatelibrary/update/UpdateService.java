package com.example.tyr.updatelibrary.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.example.tyr.updatelibrary.R;

import java.io.File;

/**
 * Created by tyr on 2018/5/23.
 */

public class UpdateService extends Service {

    private String apkURL;
    private String filePath;
    private NotificationManager notificationManager;
    private Notification notification;
    private PendingIntent contentIntent;

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            notifyUser("下载失败","失败",0);
            stopSelf();
        }

        apkURL = intent.getStringExtra("apkUrl");
        filePath = Environment.getExternalStorageDirectory()+intent.getStringExtra("apkStorage");
        Log.d("tag","下载开始"+apkURL);
        notifyUser("下载开始","下载",0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startDownload() {
        try {
            UpdateManager.getInstance().checkPermission(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UpdateManager.getInstance().startDownloads(apkURL, filePath, new UpdateDownloadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                notifyUser("正在下载","正在",progress);
            }

            @Override
            public void onFinished(float completeSize, String downloadUrl) {
                notifyUser("下载完成","完成",100);
                stopSelf();
            }

            @Override
            public void onFailure() {
                notifyUser("下载失败","失败",0);
                stopSelf();
            }
        });
    }

    private void notifyUser(String result, String reason, int progress) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle(getString(R.string.app_name));
        if (progress > 0 && progress < 100) {
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }

        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(result);
        builder.setContentIntent(progress >= 100 ? getContentIntent() : PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT));
        notification = builder.build();
        notificationManager.notify(0, notification);
    }

    public PendingIntent getContentIntent() {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://"+apkFile.getAbsolutePath()),"application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
