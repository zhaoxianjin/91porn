package com.u91porn.utils;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 *
 * @author flymegoc
 * @date 2017/12/1
 */

public abstract class CallBackWrapper<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {
        onBegin(d);
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        ApiException apiException = ApiException.handleException(e);
        onError(apiException.getMessage(), apiException.getCode());
    }

    @Override
    public void onComplete() {

    }

    public abstract void onBegin(Disposable d);

    public abstract void onSuccess(T t);

    public abstract void onError(String msg, int code);
}
