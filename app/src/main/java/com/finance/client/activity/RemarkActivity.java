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
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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
        params.put("cmd","setAuthorNoet");
        params.put("merchantID",id);
        params.put("nickName",text);
        params.put("uid", UserUtil.uid);
        showLoading();
        AsyncClient.Get()
                .setReturnClass(String.class)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(success){
                            try {
                                JSONObject jsonObject=new JSONObject(result);
                                if (jsonObject.getString("result").equals("1"))
                                {
                                    Toast.makeText(RemarkActivity.this, jsonObject.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(RemarkActivity.this, jsonObject.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toast.makeText(RemarkActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }
}
