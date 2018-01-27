package com.u91porn.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author flymegoc
 * @date 2018/1/14
 */

@GlideModule
public final class MyAppGlideModle extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 250;
        builder.setDiskCache(new DiskLruCacheFactory(AppCacheUtils.getGlideDiskCacheDir(context).getAbsolutePath(), diskCacheSizeBytes));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        ResponseProgressListener listener = new DispatchingProgressListener();
                        return response.newBuilder()
                                .body(new OkHttpProgressResponseBody(request.url(), response.body(), listener))
                                .build();
                    }
                })
                .build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }

    public static void forget(String url) {
        MyAppGlideModle.DispatchingProgressListener.forget(url);
    }

    public static void expect(String url, UiOnProgressListener listener) {
        MyAppGlideModle.DispatchingProgressListener.expect(url, listener);
    }

    private interface ResponseProgressListener {
        /**
         * progress update
         * @param url  image url
         * @param bytesRead  recent
         * @param contentLength total
         */
        void update(HttpUrl url, long bytesRead, long contentLength);
    }

    public interface UiOnProgressListener {
        /**
         * progress
         * @param bytesRead recent
         * @param expectedLength total
         */
        void onProgress(long bytesRead, long expectedLength);

        /**
         * Control how often the listener needs an update. 0% and 100% will always be dispatched.
         *
         * @return in percentage (0.2 = call {@link #onProgress} around every 0.2 percent of progress)
         */
        float getGranualityPercentage();
    }

    private static class DispatchingProgressListener implements MyAppGlideModle.ResponseProgressListener {
        private static final Map<String, UiOnProgressListener> LISTENERS = new HashMap<>();
        private static final Map<String, Long> PROGRESSES = new HashMap<>();

        private final Handler handler;

        DispatchingProgressListener() {
            this.handler = new Handler(Looper.getMainLooper());
        }

        static void forget(String url) {
            LISTENERS.remove(url);
            PROGRESSES.remove(url);
        }

        static void expect(String url, UiOnProgressListener listener) {
            LISTENERS.put(url, listener);
        }

        @Override
        public void update(HttpUrl url, final long bytesRead, final long contentLength) {
            //System.out.printf("%s: %d/%d = %.2f%%%n", url, bytesRead, contentLength, (100f * bytesRead) / contentLength);
            String key = url.toString();
            final UiOnProgressListener listener = LISTENERS.get(key);
            if (listener == null) {
                return;
            }
            if (contentLength <= bytesRead) {
                forget(key);
            }
            if (needsDispatch(key, bytesRead, contentLength, listener.getGranualityPercentage())) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onProgress(bytesRead, contentLength);
                    }
                });
            }
        }

        private boolean needsDispatch(String key, long current, long total, float granularity) {
            if (granularity == 0 || current == 0 || total == current) {
                return true;
            }
            float percent = 100f * current / total;
            long currentProgress = (long) (percent / granularity);
            Long lastProgress = PROGRESSES.get(key);
            if (lastProgress == null || currentProgress != lastProgress) {
                PROGRESSES.put(key, currentProgress);
                return true;
            } else {
                return false;
            }
        }
    }

    private static class OkHttpProgressResponseBody extends ResponseBody {
        private final HttpUrl url;
        private final ResponseBody responseBody;
        private final ResponseProgressListener progressListener;
        private BufferedSource bufferedSource;

        OkHttpProgressResponseBody(HttpUrl url, ResponseBody responseBody,
                                   ResponseProgressListener progressListener) {
            this.url = url;
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    long fullLength = responseBody.contentLength();
                    // this source is exhausted
                    if (bytesRead == -1) {
                        totalBytesRead = fullLength;
                    } else {
                        totalBytesRead += bytesRead;
                    }
                    progressListener.update(url, totalBytesRead, fullLength);
                    return bytesRead;
                }
            };
        }
    }
}
