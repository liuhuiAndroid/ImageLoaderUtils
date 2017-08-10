package com.lh.picture.listener;

/**
 * Created by lh on 2017/8/10.
 * 通知下载进度
 */

public interface ImageSaveListener{

    void onSaveSuccess();

    void onSaveFail();

    void onProgress(int progress);

}
