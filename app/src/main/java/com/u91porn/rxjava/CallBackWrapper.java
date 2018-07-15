package com.u91porn.rxjava;

import com.u91porn.exception.ApiException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2017/12/1
 */

public abstract class CallBackWrapper<T> implements Observer<T> {

    private static final String TAG = CallBackWrapper.class.getSimpleName();
    private boolean isCancel = true;

    @Override
    public void onSubscribe(Disposable d) {
        onBegin(d);
    }

    @Override
    public void onNext(T t) {
        isCancel = false;
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        ApiException apiException = ApiException.handleException(e);
        onError(apiException.getMessage(), apiException.getCode());
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (isCancel) {
            onCancel(isCancel);
        }
    }

    public void onBegin(Disposable d) {

    }

    public void onCancel(boolean isCancel) {

    }

    public abstract void onSuccess(T t);

    public abstract void onError(String msg, int code);
}
