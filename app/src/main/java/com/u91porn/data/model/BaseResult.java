package com.u91porn.data.model;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/18
 * @describe
 */

public class BaseResult {
    private List<UnLimit91PornItem> unLimit91PornItemList;
    private Integer totalPage;

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
}
