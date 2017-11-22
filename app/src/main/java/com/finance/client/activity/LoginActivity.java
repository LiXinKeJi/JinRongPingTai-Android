package com.finance.client.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.library.BaseActivity;
import com.finance.library.Content;
import com.finance.library.Util.ToolUtil;
import com.finance.library.Util.UserUtil;
import com.finance.library.common.WechatTokenDO;
import com.finance.library.event.WechatLoginEvent;
import com.finance.library.model.UserInfo;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yhrun.alchemy.Util.FastJsonUtil;
import com.yhrun.alchemy.Util.ImageLoaderUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * User : yh
 * Date : 17/6/20
 */

public class LoginActivity extends BaseActivity {
    private IWXAPI wxApi;
    private String SCOPE = "snsapi_userinfo";
    private UMShareAPI mShareAPI;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        super.onCreate(savedInstanceState);
        findViewById(R.id.LoginBtn).setOnClickListener(this);
        NavLayout.setVisibility(View.GONE);
        findViewById(R.id.PwdCallbackBtn).setOnClickListener(this);
        findViewById(R.id.RegisterBtn).setOnClickListener(this);
        findViewById(R.id.WXBtn).setOnClickListener(this);
        wxApi = WXAPIFactory.createWXAPI(this, Content.WXID);  //wechat regist
        EventBus.getDefault().register(this);
        mShareAPI = UMShareAPI.get(this);
        ImageLoaderUtil.init(getApplicationContext());
        if(!TextUtils.isEmpty(UserUtil.getUid(this))){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.LoginBtn){
            Login();
        }else if(v.getId() == R.id.PwdCallbackBtn){
            Intent intent = new Intent(LoginActivity.this,ForgetPwdActivity.class);
            LoginActivity.this.startActivity(intent);
        }else if(v.getId() == R.id.RegisterBtn){
            Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
            LoginActivity.this.startActivity(intent);
        }else if(v.getId() == R.id.WXBtn){
            if (!isWeixinAvilible()) {
                Toast.makeText(this, "请先安装微信", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "正在打开微信...", Toast.LENGTH_SHORT).show();
            mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN);
            loginOther(SHARE_MEDIA.WEIXIN, "wx");
//            if(wxApi != null){
//                SendAuth.Req req = new SendAuth.Req();
//                req.scope = SCOPE;
//                req.state = "wechat_sdk_demo_test";
//                wxApi.sendReq(req);
//            }else{
//                Toast.makeText(LoginActivity.this,"微信认证失败",Toast.LENGTH_SHORT).show();
//            }
        }
    }



    /**
     * 第三方登陆
     *
     * @param platform
     */
    private void loginOther(final SHARE_MEDIA platform, final String From) {
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                String screen_name = null, profile_image_url = null, openid = null;
                if (SHARE_MEDIA.WEIXIN.equals(share_media)) {
                    screen_name = map.get("screen_name");//昵称
                    profile_image_url = map.get("profile_image_url");//头像
                    openid = map.get("openid");//第三方平台id
                }
                Log.i("jshdfg", "onComplete: " + "昵称"+screen_name);
                thirdLogin(screen_name,profile_image_url,openid);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.i("ieei", "onError: " + throwable.getMessage());
                Toast.makeText(LoginActivity.this,"登录授权失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(LoginActivity.this,"登录授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * 第三方登录
     */
    private void thirdLogin(final String screen_name, final String profile_image_url, String openid) {
        String json = "{\"cmd\":\"weChatLogin\",\"wxId\":\"" + openid  + "\"}";

        OkHttpUtils.post().url(Content.DOMAIN).addParams("json", json).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                Log.e("response",response);
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getString("result").equals("1")){
                        Toast.makeText(LoginActivity.this, ""+obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    if(obj.has("userInfo")) {
                        int completeInfo = obj.getJSONObject("userInfo").getInt("completeInfo");
                        if (completeInfo  == 0) {
                            Intent intent = new Intent(LoginActivity.this, ActivityBaseInfo.class);
                            intent.putExtra("screen_name",screen_name);
                            intent.putExtra("profile_image_url",profile_image_url);
                            intent.putExtra("uid", obj.getJSONObject("userInfo").getString("uid"));
                            startActivity(intent);
                            return;
                        }
                    }
                    String info = obj.getString("userInfo");
                    UserInfo userInfo = (UserInfo) FastJsonUtil.parseObject(info, UserInfo.class);
//                    UserUtil.uid = userInfo.getUid();
                    UserUtil.saveUid(LoginActivity.this,userInfo.getUid());
//                            UserUtil.uid = "2";
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    Toast.makeText(LoginActivity.this,jsonObject.getString("resultNote"),Toast.LENGTH_SHORT).show();
//                    finish();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                try{
//                    JSONObject obj = new JSONObject(response);
//                    String nickName = obj.getString("nickname");
//                    String headImg = obj.getString("headimgurl");
//                    String openId = obj.getString("unionid");
//                    Map<String,String> params = Maps.newHashMap();
//                    params.put("cmd","sysWeChatLogin");
//                    params.put("uid",openId);
//                    params.put("nickName",nickName);
//                    params.put("avatar",headImg);
//                    wechatLogin(params);
                    //ThirdLogin(0,openId,nickName,headImg);
                }catch (Exception e){

                }
            }

        });


    }
    /**
     * 判断 用户是否安装微信客户端
     */
    public boolean isWeixinAvilible() {
        final PackageManager packageManager = this.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Subscribe
    public void OnWechatLoginEvent(WechatLoginEvent event){
        if(event.isSuccess()){
            wechatLogin(event.getAppid());
        }
    }

    private void Login(){
        String phone = ((EditText)findViewById(R.id.PhoneEdit)).getText().toString();
        String password = ((EditText)findViewById(R.id.PwdEdit)).getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","login");
        params.put("phoneNum",phone);
        params.put("password", ToolUtil.md5(password));
        params.put("token", JPushInterface.getRegistrationID(this));

        AsyncClient.Get()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(!success){
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("1")){
                                Toast.makeText(LoginActivity.this, ""+obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            if(obj.has("userInfo")) {
                                int completeInfo = obj.getJSONObject("userInfo").getInt("completeInfo");
                                if (completeInfo  == 0) {
                                    Intent intent = new Intent(LoginActivity.this, ActivityBaseInfo.class);
                                    intent.putExtra("uid", obj.getJSONObject("userInfo").getString("uid"));
                                    startActivity(intent);
                                    return;
                                }
                            }
                            String info = obj.getString("userInfo");
                            UserInfo userInfo = (UserInfo) FastJsonUtil.parseObject(info, UserInfo.class);
//                            UserUtil.uid = "2";
                            UserUtil.saveUid(LoginActivity.this,userInfo.getUid());
                            //userInfo.getUid();
//                            UserUtil.uid = userInfo.getUid();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void wechatLogin(String code){
        final String token_url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&grant_type=authorization_code&code=%s", Content.WXID,"32f799126ed76ccfa08aa6005b4fb816",code);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, token_url, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try{
                    WechatTokenDO token = (WechatTokenDO) FastJsonUtil.parseObject(rawJsonResponse,WechatTokenDO.class);
                    if(token == null){
                        Toast.makeText(LoginActivity.this, "微信授权失败", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        //getWechatUserInfo(token.getOpenid(),token.getAccess_token());
                        getWechatUserInfo(token.getOpenid(),token.getAccess_token());
                    }

                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, "微信授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(LoginActivity.this, "微信授权失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    public void getWechatUserInfo(String openId,String token){
        String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",token, Content.WXID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                //Toast.makeText(activity, ""+rawJsonResponse, Toast.LENGTH_SHORT).show();
                try{
                    JSONObject obj = new JSONObject(rawJsonResponse);
                    String nickName = obj.getString("nickname");
                    String headImg = obj.getString("headimgurl");
                    String openId = obj.getString("unionid");
                    Map<String,String> params = Maps.newHashMap();
                    params.put("cmd","weChatLogin");
                    params.put("uid",openId);
                    params.put("nickName",nickName);
                    params.put("avatar",headImg);
                    wechatLogin(params);
                    //ThirdLogin(0,openId,nickName,headImg);
                }catch (Exception e){

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

    private void wechatLogin(Map<String,String> params){
        showLoading();
        AsyncClient.Post()
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(!success){
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("1")){
                                Toast.makeText(LoginActivity.this, ""+obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            if(obj.has("userInfo")) {
                                int completeInfo = obj.getJSONObject("userInfo").getInt("completeInfo");
                                if (completeInfo  == 0) {
                                    Intent intent = new Intent(LoginActivity.this, ActivityBaseInfo.class);
                                    intent.putExtra("uid", obj.getJSONObject("userInfo").getString("uid"));
                                    startActivity(intent);
                                    return;
                                }
                            }
                            String info = obj.getString("userInfo");
                            UserInfo userInfo = (UserInfo) FastJsonUtil.parseObject(info, UserInfo.class);
                            UserUtil.uid = userInfo.getUid();
//                            UserUtil.uid = "2";
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
