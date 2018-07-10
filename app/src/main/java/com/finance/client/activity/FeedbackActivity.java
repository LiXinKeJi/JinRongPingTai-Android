package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/15
 */

public class FeedbackActivity extends BaseActivity {
    private String strSign;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "意见反馈";
        setContentView(R.layout.activity_feedback);
        super.onCreate(savedInstanceState);
        findViewById(R.id.RightBtnText).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.RightBtnText)).setText("完成");
        findViewById(R.id.RightBtnText).setOnClickListener(this);

    }

    private void postFeedback() {
        Map<String,String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"feedback\",\"uid\":\""+ UserUtil.getUid(this)+"\",\"content\":\""+ ((EditText)findViewById(R.id.edit_sign)).getText().toString()+
                "\",\"type\":\""+ "0"+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.makeText(FeedbackActivity.this, e.getMessage());
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                Log.i("666", "onResponse: " +response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(FeedbackActivity.this, obj.getString("resultNote"));
                        return;
                    }
                    ToastUtils.makeText(FeedbackActivity.this, obj.getString("resultNote"));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId()== R.id.RightBtnText)
        {
            postFeedback();
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
