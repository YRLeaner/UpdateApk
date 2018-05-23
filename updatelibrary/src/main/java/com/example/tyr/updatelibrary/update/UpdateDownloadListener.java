package com.example.tyr.updatelibrary.update;

/**
 * Created by tyr on 2018/5/23.
 */

public interface UpdateDownloadListener {

    /**
     * 下载开始时的回调
     */
    void onStarted();

    /**
     * 下载过程中的回调
     * @param progress
     * @param downloadUrl
     */
    void onProgressChanged(int progress, String downloadUrl);

    /**
     * 下载结束时的回调
     * @param completeSize
     * @param downloadUrl
     */
    void onFinished(float completeSize, String downloadUrl);

    /**
     * 下载失败时的回调
     */
    void onFailure();

}
