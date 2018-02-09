package com.u91porn.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

/**
 * 存储用户名，设置地址等供AutoCompleteTextView使用
 *
 * @author flymegoc
 * @date 2018/2/7
 */
@Entity
public class AutoCompleteModel {
    @Id
    private Long id;
    @Index(unique = true)
    private String name;
    private int useTime;
    private int type;
    private Date addDate;
    private Date updateDate;

    @Generated(hash = 1600794743)
    public AutoCompleteModel(Long id, String name, int useTime, int type,
            Date addDate, Date updateDate) {
        this.id = id;
        this.name = name;
        this.useTime = useTime;
        this.type = type;
        this.addDate = addDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 181281679)
    public AutoCompleteModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
