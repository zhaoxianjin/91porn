package com.u91porn.data.model;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/18
 * @describe
 */

public class BaseResult {
    public final static int SUCCESS_CODE=1;
    public final static int ERROR_CODE=2;
    private List<UnLimit91PornItem> unLimit91PornItemList;
    private Integer totalPage;
    private int code;
    private String message;
    public List<UnLimit91PornItem> getUnLimit91PornItemList() {
        return unLimit91PornItemList;
    }

    public void setUnLimit91PornItemList(List<UnLimit91PornItem> unLimit91PornItemList) {
        this.unLimit91PornItemList = unLimit91PornItemList;
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
