package com.finance.client.model;


/**
 * Created by Administrator on 2017/10/13 0013.
 */

public class ModifyAvatarResultDao {


    private String result;
    private String resultNote;
    private UserInfoDao userInfo;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public UserInfoDao getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoDao userInfo) {
        this.userInfo = userInfo;
    }
}
