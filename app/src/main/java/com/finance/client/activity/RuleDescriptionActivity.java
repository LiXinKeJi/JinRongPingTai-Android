package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.library.BaseActivity;
import com.finance.client.util.Content;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class RuleDescriptionActivity extends BaseActivity {
    private String ruleDescriptionUrl;
    private WebView myWebView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title="规则说明";
        setContentView(R.layout.activity_rule_description);
        super.onCreate(savedInstanceState);
        requestData();
        myWebView = (WebView) findViewById(R.id.webview);
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
//        myWebView.loadUrl(ruleDescriptionUrl);
    }


    private void requestData(){
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","ruleDescription");
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            if (jsonObject.getString("result").equals("1")) {
                                Toast.makeText(RuleDescriptionActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(RuleDescriptionActivity.this,jsonObject.getString("resultNote") , Toast.LENGTH_SHORT).show();
                            ruleDescriptionUrl=jsonObject.getString("ruleDescriptionUrl");
                            myWebView.loadUrl(ruleDescriptionUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                            updateView();
                    }
                });

    }
    @Override
    public void onLeftIconClick() {

    }
}
