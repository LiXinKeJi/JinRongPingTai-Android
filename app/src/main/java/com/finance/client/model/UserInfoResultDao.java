package com.finance.client.model;


/**
 * User : yh
 * Date : 17/9/11
 */

public class UserInfoResultDao extends BaseResultDO{
    public UserInfoDao getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(UserInfoDao userInfo) {
        this.userInfo = userInfo;
    }
    private UserInfoDao userInfo;
}
