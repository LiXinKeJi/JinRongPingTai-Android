package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.client.util.TimerUtil;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.ToolUtil;
import com.google.common.base.Strings;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/7/17
 */

public class RegistActivity extends BaseActivity{
    private TextView CaptchaText;
    private String captcha;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "注册";
        setContentView(R.layout.regist_layout);
        super.onCreate(savedInstanceState);
        findViewById(R.id.LoginBtn).setOnClickListener(this);
        findViewById(R.id.SubmitBtn).setOnClickListener(this);
        findViewById(R.id.CaptchaBtn).setOnClickListener(this);
        CaptchaText = (TextView) findViewById(R.id.CaptchaText);
        CaptchaText.setOnClickListener(this);
        mTimerUtil = new TimerUtil(CaptchaText);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.LoginBtn){
            finish();
        }else if(v.getId() == R.id.SubmitBtn){
            Regist();
        }else if(v.getId() == R.id.CaptchaText){
            getCaptcha();
        }
    }

    TimerUtil mTimerUtil;
    public void getCaptcha(){
        String phone = ((EditText)findViewById(R.id.PhoneEdit)).getText().toString();
        if(Strings.isNullOrEmpty(phone)){
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        captcha = mTimerUtil.getNum();
        mTimerUtil.timers();
        sendSMS(phone,captcha);
    }

    /**
     * 获取短信验证码
     * @param phone
     */
    public void sendSMS(String phone, String CODE) {
        OkHttpUtils.post().url("https://v.juhe.cn/sms/send?").addParams("mobile", phone).addParams("tpl_id", "42425").addParams("tpl_value", "%23code%23%3d" + CODE).addParams("key", "d4cd3d885697fcc221c6fa172015a440").build().execute(new StringCallback() {
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error_code").equals("0")) {
                        ToastUtils.makeText(RegistActivity.this,"短信已发送，请注意查收");
                    } else {
                        ToastUtils.makeText(RegistActivity.this,obj.getString("reason"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });

    }

    private void Regist(){
        String phoneNum = ((EditText)findViewById(R.id.PhoneEdit)).getText().toString();
        String code = ((EditText)findViewById(R.id.SMSCode)).getText().toString();
        String password = ((EditText)findViewById(R.id.PwdEdit)).getText().toString();
        String cpassword = ((EditText)findViewById(R.id.CPwdEdit)).getText().toString();
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
        Intent intent = new Intent(RegistActivity.this,ActivityBaseInfo.class);
        startActivity(intent);
        finish();

        getRegister(phoneNum, ToolUtil.md5(password));

    }

    private void getRegister(String phoneNum, String password) {
        Map<String,String> params = new HashMap<>();
        final String json = "{\"cmd\":\"register\",\"phoneNum\":\""+phoneNum+"\",\"password\":\""+password+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(RegistActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissLoading();
            }
            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                Log.i("666", "onResponse: " +response);
                try {
                    JSONObject obj = new JSONObject(response);
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
