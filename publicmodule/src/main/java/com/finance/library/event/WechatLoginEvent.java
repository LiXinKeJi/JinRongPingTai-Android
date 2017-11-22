package com.finance.library.event;

/**
 * User : yh
 * Date : 16/11/28
 */

public class WechatLoginEvent {
    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private String appid;
    private boolean success;
}
