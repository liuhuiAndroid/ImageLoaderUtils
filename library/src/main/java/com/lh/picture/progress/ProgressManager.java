package com.lh.picture.progress;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lh.picture.listener.ProgressListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lh on 2017/8/10.
 */

public class ProgressManager {

    private static List<ProgressListener> listeners =new ArrayList<ProgressListener>();

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
                                    .body(new ProgressResponseBody(response.body(), LISTENER))
                                    .build();
                        }
                    })
                    .build();
        }
        return okHttpClient;
    }

    private static final ProgressListener LISTENER = new ProgressListener() {

        @Override
        public void onProgress(final int percent, final boolean isDone) {
            if (listeners == null || listeners.size() == 0) return;

            Log.i("TAG", "test ProgressManager listeners.size = "+listeners.size());

            for (int i = 0; i < listeners.size(); i++) {
                final ProgressListener progressListener = listeners.get(i);
                if (progressListener == null) {
                    listeners.remove(i);
                    Log.i("TAG", "test ProgressManager progressListener = null");
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("TAG", "test ProgressManager post:" + percent +",isDone:"+isDone);
                            progressListener.onProgress(percent, isDone);
                        }
                    });
                }
            }
        }
    };


    public static void addProgressListener(ProgressListener progressListener) {
        if (progressListener == null)
            return;
        if (findProgressListener(progressListener) == null) {
            listeners.add(progressListener);
        }
    }

    public static void removeProgressListener(ProgressListener progressListener) {
        if (progressListener == null)
            return;
        ProgressListener listener = findProgressListener(progressListener);
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private static ProgressListener findProgressListener(ProgressListener listener) {
        if (listener == null)
            return null;
        if (listeners == null || listeners.size() == 0)
            return null;

        for (int i = 0; i < listeners.size(); i++) {
            ProgressListener progressListener = listeners.get(i);
            if (progressListener == listener) {
                return progressListener;
            }
        }
        return null;
    }
    
}
