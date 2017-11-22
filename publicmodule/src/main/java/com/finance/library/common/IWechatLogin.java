package com.finance.library.common;

/**
 * User : yh
 * Date : 17/6/26
 */

public interface IWechatLogin {
    public void onSuccess(String code);
    public void onFailed(String error);
}
