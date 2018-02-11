package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import org.json.JSONObject;

import java.util.Map;

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
        params.put("cmd", "feedback");
        params.put("uid", UserUtil.getUid(this));
        params.put("content", ((EditText)findViewById(R.id.edit_sign)).getText().toString());
        showLoading();
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if (success) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (obj.getString("result").equals("1")) {
                                    Toast.makeText(FeedbackActivity.this, obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(FeedbackActivity.this, obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
