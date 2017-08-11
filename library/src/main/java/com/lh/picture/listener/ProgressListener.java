package com.lh.picture.listener;

/**
 * Created by lh on 2017/8/10.
 * 通知下载进度
 */

public interface ProgressListener {

    void onProgress(int percent, boolean isDone);

}
