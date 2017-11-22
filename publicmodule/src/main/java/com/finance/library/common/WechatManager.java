package com.finance.library.common;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yhrun.alchemy.Util.FastJsonUtil;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * User : yh
 * Date : 17/6/26
 */

public class WechatManager {
    private IWXAPI api;
    private final String SCOPE = "snsapi_userinfo";
    private String appId,appSecret;
    private IWechatLogin callback;
    private static WechatManager manager;
    public static WechatManager getInstance(){
        if(manager  == null){
            manager = new WechatManager();
        }
        return manager;
    }

    public void init(Context mContext,String key,String appSecret){
        this.appId = key;
        this.appSecret = appSecret;
        api = WXAPIFactory.createWXAPI(mContext,key,true);
        api.registerApp(key);
    }

    public void Login(IWechatLogin callback){
        if(api == null || !api.isWXAppInstalled()){
            callback.onFailed("微信登录初始化失败，请检查配置");
            callback = null;
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE;
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
        this.callback = callback;
    }

    public void onResult(BaseResp baseResp){
        if(baseResp.errCode != 0){
            callback.onFailed(baseResp.errStr);
            callback = null;
            return;
        }
        SendAuth.Resp resp = (SendAuth.Resp) baseResp;
        String code = resp.code;
        getAccessToken(code);
    }

    private void getAccessToken(String code){
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", appId, appSecret, code);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get( url, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {

                try{
                    WechatTokenDO token = (WechatTokenDO) FastJsonUtil.parseObject(rawJsonResponse,WechatTokenDO.class);
                    if(token == null){
                        callback.onFailed("微信授权失败");
                        callback = null;
                        return;
                    }else{
                        getWechatUserInfo(token.getOpenid(),token.getAccess_token());
                    }

                }catch (Exception e){
                    callback.onFailed("微信授权失败");
                    callback = null;
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
            }
            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void getWechatUserInfo(String openId,String token){
        String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",token,appId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get( url, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                callback.onSuccess(rawJsonResponse);
                callback = null;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }
}
