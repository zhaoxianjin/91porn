package com.u91porn.rxjava;

import com.orhanobut.logger.Logger;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Function;

/**
 * https://www.jianshu.com/p/7e28c8216c7d rxjava
 * 超时重试，因为网站的原因，okhttp自带的超时重试貌似没啥效果，只能通过外面重试
 * rxjava2
 *
 * @author flymegoc
 * @date 2018/1/13
 */

public class RetryWhenProcess implements Function<Observable<Throwable>, ObservableSource<?>> {
    private static final String TAG = RetryWhenProcess.class.getSimpleName();
    /**
     * 重试间隔
     */
    private long mInterval;
    private long tryTimes = 1;
    private long maxTryTime = 3;

    public RetryWhenProcess(long interval) {

        mInterval = interval;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {

        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                Logger.t(TAG).d("Error:::" + throwable);
                if (throwable instanceof SocketTimeoutException && ++tryTimes <= maxTryTime) {
                    Logger.t(TAG).d("超时重试第【" + (tryTimes - 1) + "】次");
                    return Observable.timer(mInterval, TimeUnit.SECONDS);
                } else if (throwable instanceof CompositeException) {
                    CompositeException compositeException = (CompositeException) throwable;
                    //结合rxcache会把异常进行包裹才会返回，需要解析提取
                    for (Throwable innerthrowable : compositeException.getExceptions()) {
                        if (innerthrowable instanceof SocketTimeoutException && ++tryTimes <= maxTryTime) {
                            Logger.t(TAG).d("带Rxcache超时重试第【" + (tryTimes - 1) + "】次");
                            return Observable.timer(mInterval, TimeUnit.SECONDS);
                        }
                    }
                }
                return Observable.error(throwable);
            }

        });
    }
}
