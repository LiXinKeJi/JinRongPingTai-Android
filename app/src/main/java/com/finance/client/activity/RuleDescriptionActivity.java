package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class RuleDescriptionActivity extends BaseActivity {
    private String ruleDescriptionUrl;
    private WebView myWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "推荐";
        setContentView(R.layout.activity_rule_description);
        super.onCreate(savedInstanceState);

        findViewById(R.id.BackImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myWebView = (WebView) findViewById(R.id.webview);

        if(TextUtils.isEmpty(getIntent().getStringExtra("url"))){
            requestData();
        }else{
            ruleDescriptionUrl = getIntent().getStringExtra("url");//启动页，欢迎页跳转
            myWebView.loadUrl(ruleDescriptionUrl);
        }

        WebSettings settings = myWebView.getSettings();
        // 设置可以支持缩放
        settings.setSupportZoom(true);
        // 设置支持js
        settings.setJavaScriptEnabled(true);
        // 关闭缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        // 设置出现缩放工具
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        // 扩大比例的缩放
        settings.setUseWideViewPort(true);
        // 自适应屏幕
        settings.setLoadWithOverviewMode(true);
//        myWebView.loadUrl(ruleDescriptionUrl);]
        if (!TextUtils.isEmpty(getIntent().getStringExtra("url"))) {
            myWebView.loadUrl(getIntent().getStringExtra("url"));
            title = "推荐";
        } else {
            requestData();
            title = "规则说明";
        }
    }


    private void requestData() {
        showLoading();
        Map<String, String> params =new HashMap<>();
        final String json = "{\"cmd\":\"ruleDescription\",\"nid\":\"" + "6" + "\"}";
        Log.e("获取规则说明...........", json);
        params.put("json", json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                ToastUtils.makeText(RuleDescriptionActivity.this, e.getMessage());
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                Log.e("获取规则说明...........", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("1")) {
                        Toast.makeText(RuleDescriptionActivity.this, jsonObject.getString("resultNote"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ruleDescriptionUrl = jsonObject.getString("detailUrl");
                    myWebView.loadUrl(ruleDescriptionUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLeftIconClick() {

    }
}
