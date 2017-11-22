package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.ToolUtil;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import java.util.Map;

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
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changePassword");
        params.put("uid", UserUtil.uid);
        params.put("oldPassword", ToolUtil.md5(oldPwd));
        params.put("newPassword",ToolUtil.md5(newPwd));
        showLoading();
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setParams(params)
                .execute(new AsyncResponseHandler() {
                    @Override
                    public void onResult(boolean success, Object result, ResponseError error) {
                        dismissLoading();
                        if(success){
                            Toast.makeText(ModifyPwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(ModifyPwdActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
