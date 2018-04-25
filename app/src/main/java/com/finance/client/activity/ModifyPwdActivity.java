package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.ToolUtil;
import com.finance.client.util.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class ModifyPwdActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "修改密码";
        setContentView(R.layout.activity_modify_pwd);
        super.onCreate(savedInstanceState);
        findViewById(R.id.SubmitBtn).setOnClickListener(this);
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
        }
    }

    private void Submit(){
        String oldPwd = ((EditText)findViewById(R.id.OldPwdEdit)).getText().toString();
        String newPwd = ((EditText)findViewById(R.id.PwdEdit)).getText().toString();

        if(TextUtils.isEmpty(oldPwd)){
            Toast.makeText(this, "请输入旧密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(newPwd)){
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        sumbitChangePassword(ToolUtil.md5(oldPwd),ToolUtil.md5(newPwd));
    }

    private void sumbitChangePassword(String oldPassword, String newPassword) {
        Map<String,String> params = new HashMap<>();
        final String json = "{\"cmd\":\"changePassword\",\"uid\":\""+ UserUtil.uid+"\",\"oldPassword\":\""+oldPassword+"\",\"newPassword\":\""+newPassword+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(ModifyPwdActivity.this,e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("1")){
                        ToastUtils.makeText(ModifyPwdActivity.this, jsonObject.getString("resultNote"));
                        return;
                    }
                    ToastUtils.makeText(ModifyPwdActivity.this, "修改成功");
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
