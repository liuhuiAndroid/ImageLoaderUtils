package com.lh.picture.progress;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lh.picture.listener.ProgressListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lh on 2017/8/10.
 */

public class ProgressManager {

    //    private static List<ProgressListener> listeners =new ArrayList<ProgressListener>();
    private static Map<String, ProgressListener> listeners = new HashMap<>();

    private static OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response.newBuilder()
                                    .body(new ProgressResponseBody(request.url().toString(), response.body(), LISTENER))
                                    .build();
                        }
                    })
                    .build();
        }
        return okHttpClient;
    }

    private static final ProgressListener LISTENER = new ProgressListener() {

        @Override
        public void onProgress(final String imageUrl, final int percent, final boolean isDone) {
            if (listeners == null || listeners.size() == 0)
                return;
            for (String key : listeners.keySet()) {
                if (key.equals(imageUrl)) {
                    final ProgressListener progressListener = listeners.get(key);
                    if (progressListener == null) {
                        listeners.remove(key);
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressListener.onProgress(imageUrl, percent, isDone);
                            }
                        });
                    }
                }
            }

        }
    };


    public static void addProgressListener(String imageUrl, ProgressListener progressListener) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl) || progressListener == null)
            return;
        if (!findProgressListener(imageUrl)) {
            listeners.put(imageUrl, progressListener);
        }
    }

    public static void removeProgressListener(String imageUrl) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl))
            return;
        if (findProgressListener(imageUrl)) {
            listeners.remove(imageUrl);
        }
    }

    private static boolean findProgressListener(String imageUrl) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl))
            return false;
        if (listeners == null || listeners.size() == 0)
            return false;

        for (String key : listeners.keySet()) {
            if (key.equals(imageUrl)) {
                return true;
            }
        }
        return false;
    }

}
