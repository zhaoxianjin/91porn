package com.u91porn.data.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author flymegoc
 * @date 2017/11/18
 * @describe
 */

public class BaseResult<T> {
    public final static int SUCCESS_CODE = 1;
    public final static int ERROR_CODE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SUCCESS_CODE, ERROR_CODE})
    @interface ResultCode {

    }

    private T data;
    private Integer totalPage;
    @ResultCode
    private int code;
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
