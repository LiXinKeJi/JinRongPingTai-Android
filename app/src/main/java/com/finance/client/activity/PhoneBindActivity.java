package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.AbStrUtil;
import com.finance.client.util.Content;
import com.finance.client.util.TimerUtil;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class PhoneBindActivity extends BaseActivity {
    private TextView CaptchaText;
    private String captcha;

    private TimerUtil mTimerUtil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "绑定手机号";
        setContentView(R.layout.activity_phone_bind);
        super.onCreate(savedInstanceState);
        findViewById(R.id.SubmitBtn).setOnClickListener(this);
        findViewById(R.id.CaptchaBtn).setOnClickListener(this);
        CaptchaText = (TextView) findViewById(R.id.CaptchaText);
        mTimerUtil = new TimerUtil(CaptchaText);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.SubmitBtn) {
            Submit();
        } else if (v.getId() == R.id.CaptchaBtn) {
            getCaptcha();
        }
    }

    public void getCaptcha() {
        if (AbStrUtil.tvTostr(CaptchaText).equals("重新获取") || AbStrUtil.tvTostr(CaptchaText).equals("获取验证码")) {
            String phone = ((EditText) findViewById(R.id.PhoneEdit)).getText().toString();
            if (Strings.isNullOrEmpty(phone)) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            captcha = mTimerUtil.getNum();
        sendSMS(phone, captcha);
            mTimerUtil.timers();
        }
    }

    /**
     * 获取短信验证码
     *
     * @param phone
     */
    public void sendSMS(String phone, String CODE) {
        OkHttpUtils.post().url("https://v.juhe.cn/sms/send?").addParams("mobile", phone).addParams("tpl_id", "42425").addParams("tpl_value", "%23code%23%3d" + CODE).addParams("key", "d4cd3d885697fcc221c6fa172015a440").build().execute(new StringCallback() {
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error_code").equals("0")) {
                        ToastUtils.makeText(PhoneBindActivity.this, "短信已发送，请注意查收");
                    } else {
                        ToastUtils.makeText(PhoneBindActivity.this, obj.getString("reason"));
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

    private void Submit() {
        String phoneNum = ((EditText) findViewById(R.id.PhoneEdit)).getText().toString();
        String code = ((EditText) findViewById(R.id.SMSCode)).getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            Toast.makeText(this, "请发送验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!code.equals(captcha)) {
            Toast.makeText(this, "验证码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String, String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"bindingPhoneNumber\",\"newPhoneNum\":\"" + phoneNum + "\",\"uid\":\"" + UserUtil.uid + "\"}";
        params.put("json", json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(PhoneBindActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                Log.i("666", "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(PhoneBindActivity.this, "" + obj.getString("resultNote"));
                        return;
                    }
                    ToastUtils.makeText(PhoneBindActivity.this, "绑定成功");
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
