package com.lh.picture;

import android.content.Context;
import android.widget.ImageView;

import com.lh.picture.listener.ImageSaveListener;
import com.lh.picture.listener.ProgressListener;

/**
 * Created by lh on 2017/8/10.
 */

public class ImageLoaderUtils {

    private volatile static ImageLoaderUtils instance;

    private BaseImageLoaderStrategy mStrategy;

    protected ImageLoaderUtils() {
        mStrategy = new GlideImageLoaderStrategy();
    }

    /**
     * Returns singleton class instance
     */
    public static ImageLoaderUtils getInstance() {
        if (instance == null) {
            synchronized (ImageLoaderUtils.class) {
                if (instance == null) {
                    instance = new ImageLoaderUtils();
                }
            }
        }
        return instance;
    }

    public void loadImage(String url, ImageView imageView) {
        mStrategy.loadImage(url, imageView);
    }

    public void loadImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadImage(url, placeholder, imageView);
    }

    public void loadCircleImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadCircleImage(url, placeholder, imageView);
    }

    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        mStrategy.saveImage(context, url, savePath, saveFileName, listener);
    }

    public void loadImageWithProgress(String url, ImageView imageView, ProgressListener listener, String image_url_thumbnail) {
        mStrategy.loadImageWithProgress(url, imageView, listener,image_url_thumbnail);
    }

    public void clearImageDiskCache(Context context) {
        mStrategy.clearImageDiskCache(context);
    }

    public void clearImageMemoryCache(Context context) {
        mStrategy.clearImageMemoryCache(context);
    }

}
