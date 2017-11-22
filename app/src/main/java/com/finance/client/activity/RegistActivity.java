package com.finance.client.activity;

import android.content.Intent;
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
import com.finance.library.BaseActivity;
import com.finance.library.Content;
import com.finance.library.Util.SmsUtil;
import com.finance.library.Util.ToolUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import org.json.JSONObject;

import java.util.Map;

/**
 * User : yh
 * Date : 17/7/17
 */

public class RegistActivity extends BaseActivity{
    private boolean captchaing = false;
    private int time = 60;
    private String captcha;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "注册";
        setContentView(com.finance.library.R.layout.regist_layout);
        super.onCreate(savedInstanceState);
        findViewById(com.finance.library.R.id.LoginBtn).setOnClickListener(this);
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
        if(v.getId() == com.finance.library.R.id.LoginBtn){
            finish();
        }else if(v.getId() == com.finance.library.R.id.SubmitBtn){
            Regist();
        }else if(v.getId() == com.finance.library.R.id.CaptchaBtn){
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
                    Toast.makeText(RegistActivity.this, ""+code, Toast.LENGTH_SHORT).show();
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

    private void Regist(){
        String phoneNum = ((EditText)findViewById(com.finance.library.R.id.PhoneEdit)).getText().toString();
        String code = ((EditText)findViewById(R.id.SMSCode)).getText().toString();
        String password = ((EditText)findViewById(com.finance.library.R.id.PwdEdit)).getText().toString();
        String cpassword = ((EditText)findViewById(com.finance.library.R.id.CPwdEdit)).getText().toString();
        if(TextUtils.isEmpty(captcha)){
            Toast.makeText(this, "请发送验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!code.equals(captcha)){
            Toast.makeText(this, "验证码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)){
            Toast.makeText(this, "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(cpassword)){
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","register");
        params.put("phoneNum",phoneNum);
        params.put("password", ToolUtil.md5(password));
        showLoading();
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
                            Toast.makeText(RegistActivity.this, ""+error.errorMsg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("1")) {
                                Toast.makeText(RegistActivity.this, "" + obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String uid = obj.getString("uid");
                            Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistActivity.this,ActivityBaseInfo.class);
                            intent.putExtra("uid",uid);
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
    }
}
