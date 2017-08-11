package com.lh.picture;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.lh.picture.progress.ProgressManager;

import java.io.InputStream;

/**
 * Created by lh on 2017/8/10.
 * AppGlideModule 是一个抽象方法，全局改变 Glide 行为的一个方式
 */

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
        // Apply options to the builder here.
        //获取系统分配给应用的总内存大小
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //设置图片内存缓存占用八分之一
        int memoryCacheSize = maxMemory / 8;
        //设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(memoryCacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // register ModelLoaders here.
        // Glide 底层默认使用 HttpConnection 进行网络请求,这里替换为 Okhttp 进行 Glide 的加载进度监听
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient()));
    }
}
