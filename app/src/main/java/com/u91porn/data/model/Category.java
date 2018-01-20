package com.u91porn.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 类别
 *
 * @author flymegoc
 * @date 2018/1/19
 */
@Entity
public class Category implements Serializable {
    public static final String[] CATEGORY_DEFAULT_91PORN_VALUE = {"index","watch","hot", "rp", "long", "md", "tf", "mf", "rf", "top", "top1", "hd"};
    public static final String[] CATEGORY_DEFAULT_91PORN_NAME = {"主页","最近更新","当前最热", "最近得分", "10分钟以上", "本月讨论", "本月收藏", "收藏最多", "最近加精", "本月最热", "上月最热", "高清(会员)"};
    public static final int TYPE_91PORN = 1;
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;

    private int categoryType;

    private String categoryName;

    private String categoryValue;

    private String categoryUrl;

    private Integer sortId;

    private boolean isShow;

    @Generated(hash = 895463876)
    public Category(Long id, int categoryType, String categoryName,
            String categoryValue, String categoryUrl, Integer sortId,
            boolean isShow) {
        this.id = id;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.categoryValue = categoryValue;
        this.categoryUrl = categoryUrl;
        this.sortId = sortId;
        this.isShow = isShow;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCategoryType() {
        return this.categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryValue() {
        return this.categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    public String getCategoryUrl() {
        return this.categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public Integer getSortId() {
        return this.sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public boolean getIsShow() {
        return this.isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }
}
