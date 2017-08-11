package com.lh.picture;

import android.content.Context;
import android.widget.ImageView;

import com.lh.picture.listener.ImageSaveListener;
import com.lh.picture.listener.ProgressListener;

/**
 * Created by lh on 2017/8/10.
 */

public interface BaseImageLoaderStrategy {

    //无占位图
    void loadImage(String url, ImageView imageView);

    //有占位图
    void loadImage(String url, int placeholder, ImageView imageView);

    //加载圆形图
    void loadCircleImage(String url, int placeholder, ImageView imageView);

    //保存图片
    void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener);

    //加载图片并监听进度
    void loadImageWithProgress(String url, ImageView imageView, ProgressListener listener, String image_url_thumbnail);

    //清除硬盘缓存
    void clearImageDiskCache(final Context context);

    //清除内存缓存
    void clearImageMemoryCache(Context context);

    //清除所有缓存
    void clearImageAllCache(Context context);

}
