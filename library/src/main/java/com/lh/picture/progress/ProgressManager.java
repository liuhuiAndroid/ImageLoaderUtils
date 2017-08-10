package com.lh.picture.progress;

import android.support.annotation.NonNull;

import com.lh.picture.listener.ProgressListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lh on 2017/8/10.
 */

public class ProgressManager {

    private static List<WeakReference<ProgressListener>> listeners =
            Collections.synchronizedList(new ArrayList<WeakReference<ProgressListener>>());

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
        public void onProgress(long bytesRead, long totalBytes, boolean isDone) {
            if (listeners == null || listeners.size() == 0) return;

            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<ProgressListener> listener = listeners.get(i);
                ProgressListener progressListener = listener.get();
                if (progressListener == null) {
                    listeners.remove(i);
                } else {
                    progressListener.onProgress( bytesRead, totalBytes, isDone);
                }
            }
        }
    };


    public static void addProgressListener(ProgressListener progressListener) {
        if (progressListener == null)
            return;
        if (findProgressListener(progressListener) == null) {
            listeners.add(new WeakReference<>(progressListener));
        }
    }

    public static void removeProgressListener(ProgressListener progressListener) {
        if (progressListener == null)
            return;
        WeakReference<ProgressListener> listener = findProgressListener(progressListener);
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private static WeakReference<ProgressListener> findProgressListener(ProgressListener listener) {
        if (listener == null)
            return null;
        if (listeners == null || listeners.size() == 0)
            return null;

        for (int i = 0; i < listeners.size(); i++) {
            WeakReference<ProgressListener> progressListener = listeners.get(i);
            if (progressListener.get() == listener) {
                return progressListener;
            }
        }
        return null;
    }
    
}
