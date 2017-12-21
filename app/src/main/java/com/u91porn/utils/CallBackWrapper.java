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
 * Created by flymegoc on 2017/12/1.
 */

public abstract class CallBackWrapper<T> implements Observer<T> {

    //未知错误
    public static final int UN_KNOWN_ERROR = 1000;
    //解析(服务器)数据错误
    public static final int ANALYTIC_SERVER_DATA_ERROR = 1001;
    //解析(客户端)数据错误
    public static final int ANALYTIC_CLIENT_DATA_ERROR = 1002;
    //网络连接错误
    public static final int CONNECT_ERROR = 1003;
    //网络连接超时
    public static final int TIME_OUT_ERROR = 1004;

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
//        CompositeException compositeException = (CompositeException) e;
//        if (compositeException.size() > 0) {
//            e = compositeException.getExceptions().get(0);
//        }
        //HTTP错误
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ResponseBody responseBody = httpException.response().errorBody();
            onError("网络错误", httpException.code());
            //解析数据错误
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException || e instanceof MalformedJsonException) {
            onError("数据解析错误", ANALYTIC_CLIENT_DATA_ERROR);
            //连接网络错误
        } else if (e instanceof ConnectException) {
            onError("连接失败", CONNECT_ERROR);
            //网络超时
        } else if (e instanceof SocketTimeoutException) {
            onError("网络连接超时", TIME_OUT_ERROR);
            //未知错误
        } else {
            onError("未知错误", UN_KNOWN_ERROR);
        }
    }

    @Override
    public void onComplete() {

    }

    public abstract void onBegin(Disposable d);

    public abstract void onSuccess(T t);

    public abstract void onError(String msg, int code);

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
