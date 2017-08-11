package com.lh.picture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    private ImageSaveListener mImageSaveListener;

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
        mImageSaveListener = listener;
        DownloadThread downloadThread = new DownloadThread(context, url, savePath, saveFileName);
        downloadThread.start();
    }

    @Override
    public void loadImageWithProgress(String url, ImageView imageView, final ProgressListener listener, String image_url_thumbnail) {
        if (url == null)
            return;
        if (!url.startsWith(HTTP))
            return;

        ProgressManager.addProgressListener(url, listener);
        GlideApp.with(imageView.getContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(Glide.with(imageView.getContext())
                        .load(image_url_thumbnail))
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

    @Override
    public void clearImageAllCache(Context context) {
        clearImageDiskCache(context.getApplicationContext());
        clearImageMemoryCache(context.getApplicationContext());
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageSaveListener != null) {
                if (msg.what == 0) {
                    mImageSaveListener.onSaveFail();
                } else if (msg.what == 1) {
                    mImageSaveListener.onSaveSuccess();
                } else if (msg.what == 2) {
                    mImageSaveListener.onProgress(msg.arg1);
                }
            }
        }
    };

    private class DownloadThread extends Thread {

        private Context mContext;
        private String mUrl;
        private String mSavePath;
        private String mSaveFileName;

        public DownloadThread(Context context, String url, String savePath, String saveFileName) {
            mContext = context;
            mUrl = url;
            mSavePath = savePath;
            mSaveFileName = saveFileName;
        }

        @Override
        public void run() {
            super.run();
            InputStream fromStream = null;
            OutputStream toStream = null;
            try {
                File cacheFile = Glide.with(mContext).load(mUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                if (cacheFile == null || !cacheFile.exists()) {
                    mHandler.sendEmptyMessage(0);
                    return;
                }
                File dir = new File(mSavePath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, mSaveFileName + CommonUtils.getPicType(cacheFile.getAbsolutePath()));

                fromStream = new FileInputStream(cacheFile);
                toStream = new FileOutputStream(file);
                byte length[] = new byte[1024];
                int count;
                long totalSize = cacheFile.length();
                long totalBytesRead = 0;
                while ((count = fromStream.read(length)) > 0) {
                    toStream.write(length, 0, count);
                    totalBytesRead += ((count != -1) ? count : 0);

                    int percent = (int) ((totalBytesRead * 1.0f) / totalSize * 100.0f);
                    Message message = Message.obtain(mHandler, 2);
                    message.arg1 = percent;
                    message.what = 2;
                    mHandler.sendMessage(message);
                }

                //用广播通知相册进行更新相册
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                mContext.sendBroadcast(intent);
                mHandler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(0);
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
    }

}
