package com.lh.picture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lh.picture.listener.ImageSaveListener;
import com.lh.picture.listener.ProgressListener;
import com.lh.picture.progress.ProgressManager;
import com.lh.picture.util.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lh on 2017/8/10.
 */

public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy {

    private static final String HTTP = "http";

    private ProgressListener internalProgressListener;

    @Override
    public void loadImage(String url, ImageView imageView) {
        GlideApp.with(imageView.getContext()).load(url)
                .placeholder(imageView.getDrawable())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void loadImage(String url, int placeholder, ImageView imageView) {
        GlideApp.with(imageView.getContext()).load(url)
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void loadCircleImage(String url, int placeholder, ImageView imageView) {
        GlideApp.with(imageView.getContext()).load(url)
                .placeholder(placeholder)
                // 移除设置的动画
                .dontAnimate()
                // CircleCrop类是一个实现了圆形图片的类
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        if (!CommonUtils.isSDCardExsit() || TextUtils.isEmpty(url)) {
            listener.onSaveFail();
            return;
        }
        InputStream fromStream = null;
        OutputStream toStream = null;
        try {
            File cacheFile = GlideApp.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            if (cacheFile == null || !cacheFile.exists()) {
                listener.onSaveFail();
                return;
            }
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, saveFileName + CommonUtils.getPicType(cacheFile.getAbsolutePath()));
            fromStream = new FileInputStream(cacheFile);
            toStream = new FileOutputStream(file);
            byte length[] = new byte[1024];
            int len;
            long totalSize = cacheFile.length();
            long sum = 0;
            while ((len = fromStream.read(length)) > 0) {
                toStream.write(length, 0, len);
                sum += len;
                int progress = (int) ((sum * 1.0f / totalSize) * 100);
                listener.onProgress(progress);
            }
            //用广播通知相册进行更新相册
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            listener.onSaveSuccess();

        } catch (Exception e) {
            e.printStackTrace();
            listener.onSaveFail();
        } finally {
            if (fromStream != null) {
                try {
                    fromStream.close();
                    toStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fromStream = null;
                    toStream = null;
                }
            }
        }
    }

    @Override
    public void loadImageWithProgress(String url, ImageView imageView, final ProgressListener listener, String image_url_thumbnail) {
        if (url == null)
            return;
        if (!url.startsWith(HTTP))
            return;

        ProgressManager.addProgressListener(listener);
        GlideApp.with(imageView.getContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .thumbnail(Glide.with(imageView.getContext())
//                        .load(image_url_thumbnail))
                .into(imageView);
    }

    @Override
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
