package com.u91porn.data.model;

/**
 * @author flymegoc
 * @date 2017/12/10
 */

public class User {
    private String userName;
    private boolean isLogin;
    private int userId;
    private String status;
    private String lastLoginTime;
    private String lastLoginIP;

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", isLogin=" + isLogin +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", lastLoginTime='" + lastLoginTime + '\'' +
                ", lastLoginIP='" + lastLoginIP + '\'' +
                '}';
    }

    public void copyProperties(User toUser) {
        toUser.setUserName(getUserName());
        toUser.setUserId(getUserId());
        toUser.setLogin(isLogin());
        toUser.setLastLoginIP(getLastLoginIP());
        toUser.setLastLoginTime(getLastLoginTime());
        toUser.setStatus(getStatus());
    }

    public void cleanProperties() {
        this.setUserName("");
        this.setUserId(0);
        this.setLogin(false);
        this.setLastLoginIP("");
        this.setLastLoginTime("");
        this.setStatus("");
    }
}
