package com.finance.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.SmsUtil;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * User : yh
 * Date : 17/9/13
 */

public class PhoneBindActivity extends BaseActivity{
    private boolean captchaing = false;
    private int time = 60;
    private String captcha;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "绑定手机号";
        setContentView(R.layout.activity_phone_bind);
        super.onCreate(savedInstanceState);
        findViewById(com.finance.library.R.id.SubmitBtn).setOnClickListener(this);
        findViewById(com.finance.library.R.id.CaptchaBtn).setOnClickListener(this);

    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.SubmitBtn){
            Submit();
        }else if(v.getId() == R.id.CaptchaBtn){
            getCaptcha();
        }
    }

    public void getCaptcha(){
        if(captchaing){
            return;
        }
        String phone = ((EditText)findViewById(com.finance.library.R.id.PhoneEdit)).getText().toString();
        if(Strings.isNullOrEmpty(phone)){
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        captchaing = true;
        showLoading();
        SmsUtil.SendSms(phone, new SmsUtil.SendSmsCallHandler() {
            @Override
            public void onResult(boolean success, String code) {
                dismissLoading();
                if(success){
                    //Toast.makeText(activity, "获取验证码成功", Toast.LENGTH_SHORT).show();
                    captcha = code;
                    Timer();
                    return;
                }else{
                    Toast.makeText(PhoneBindActivity.this, ""+code, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void Timer(){
        Handler handler = new Handler(this.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(time > 0){
                    ((TextView)findViewById(com.finance.library.R.id.CaptchaText)).setText(time+"s后重试");
                    time--;
                    sendEmptyMessageDelayed(0,1000);
                }else{
                    ResetTimer();
                }
            }
        };
        handler.sendEmptyMessage(0);
    }

    private void ResetTimer(){
        time = 60;
        ((TextView)findViewById(com.finance.library.R.id.CaptchaText)).setText("获取验证码");
        captchaing = false;
    }

    private void Submit(){
        String phoneNum = ((EditText)findViewById(com.finance.library.R.id.PhoneEdit)).getText().toString();
        String code = ((EditText)findViewById(R.id.SMSCode)).getText().toString();
        if(TextUtils.isEmpty(phoneNum)){
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(captcha)){
            Toast.makeText(this, "请发送验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!code.equals(captcha)){
            Toast.makeText(this, "验证码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","bindingPhoneNumber");
        params.put("newPhoneNum",phoneNum);
        params.put("uid", UserUtil.uid);
        showLoading();
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .execute(new AsyncResponseHandler() {
                    @Override
                    public void onResult(boolean success, Object result, ResponseError error) {
                        dismissLoading();
                        if(success){
                            Toast.makeText(PhoneBindActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(PhoneBindActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
