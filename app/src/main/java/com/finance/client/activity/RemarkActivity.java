package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.model.BaseResultDO;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class RemarkActivity extends BaseActivity{
    private String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        id = getIntent().getStringExtra("id");
        title = "设置备注";
        setContentView(R.layout.remark_layout);
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
        String text = ((EditText)findViewById(R.id.Edit)).getText().toString();
        if(TextUtils.isEmpty(text)){
            Toast.makeText(this, "请输入备注信息", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"setAuthorNoet\",\"merchantID\":\""+id+"\",\"nickName\":\""+text+"\",\"uid\":\""+ UserUtil.uid+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(RemarkActivity.this,e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                dismissLoading();
                BaseResultDO baseResultDO = gson.fromJson(response,BaseResultDO.class);
                if (baseResultDO.getResult().equals("1")){
                    ToastUtils.makeText(RemarkActivity.this, baseResultDO.getResultNote());
                    return;
                }
                ToastUtils.makeText(RemarkActivity.this, baseResultDO.getResultNote());
                MyApplication.temp = 1;
                finish();
            }
        });
    }
}
