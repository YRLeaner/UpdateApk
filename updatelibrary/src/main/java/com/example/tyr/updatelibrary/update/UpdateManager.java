package com.example.tyr.updatelibrary.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by tyr on 2018/5/23.
 * @author tyr
 */

public class UpdateManager {

    private static UpdateManager manager;
    private ThreadPoolExecutor threadPoolExecutor;
    private UpdateDownloadRequest request;
    private Context context;

    private UpdateManager(){
        threadPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    }

    static {
        manager = new UpdateManager();
    }

    public static UpdateManager getInstance(){
        return manager;
    }

    public void startUpdate(Context context,String apkUrl,String filePath){
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra("apkUrl", apkUrl);
        intent.putExtra("apkStorage",filePath);
        context.startService(intent);
    }

    public void startDownloads(String downloadUrl,String localPath,UpdateDownloadListener listener){
        if (request!=null){
            Log.d("tag","manager 失败");
            return;
        }
        Log.d("tag","manager 开始下载");
        checkLoaclFilePath(localPath);
        //开始下载任务
        request = new UpdateDownloadRequest(downloadUrl,localPath,listener);
        Future<?> future = threadPoolExecutor.submit(request);
    }

    /**
     * 用来检查文件路径是否已经存在
     * @param localPath
     */
    private void checkLoaclFilePath(String localPath) {
        File dir = new File(localPath.substring(0,localPath.lastIndexOf("/")+1));
        if (!dir.exists()){
            Log.d("tag","创建文件夹"+dir);
            dir.mkdir();
        }
        Log.d("tag","已存在文件夹"+dir);
        File file = new File(localPath);

        if (!file.exists()){
            try {
                Log.d("tag","创建文件"+file);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("tag","已存在文件"+file);
    }

    /**
     * 检查是否有相应的权限
     * @param context
     * @throws Exception
     */
    public void checkPermission(Context context) throws Exception {
        PackageManager pm = context.getPackageManager();
        String[] premissions = {"android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.INTERNET"};
        for (int i = 0; i < premissions.length; i++) {
            if (!(PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission(premissions[i], context.getPackageName()))) {
                Log.d("tag","1  "+i);
                switch (i) {
                    case 0:
                    case 1:
                        throw new Exception("缺少读写权限");
                    case 2:
                        throw new Exception("缺少网络权限");
                    default:
                        break;
                }
            }
        }
    }
}
