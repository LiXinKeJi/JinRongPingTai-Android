package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.model.BaseBean;
import com.finance.client.util.Content;
import com.finance.client.util.UserUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/14
 */

public class ScoreActivity extends BaseActivity{
    private int[] ids = {R.id.Score1,R.id.Score2,R.id.Score3,R.id.Score4,R.id.Score5};
    private int score = 0;
    private String messageID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "我来评分";
        setContentView(R.layout.activity_score);
        super.onCreate(savedInstanceState);
        messageID = getIntent().getStringExtra("msgId");
        for(int id : ids){
            findViewById(id).setOnClickListener(this);
        }
        findViewById(R.id.LoginBtn).setOnClickListener(this);

        Log.i("6666", "submit: " + messageID);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.Score1){
            select(0);
        }else if(v.getId() == R.id.Score2){
            select(1);
        }else if(v.getId() == R.id.Score3){
            select(2);
        }else if(v.getId() == R.id.Score4){
            select(3);
        }else if(v.getId() == R.id.Score5){
            select(4);
        }else if(v.getId() == R.id.LoginBtn){
            submit();
        }
    }

    private void select(int index){
        score = index + 1;
        for(int i = 0 ;i < ids.length; ++i){
            if(i <= index){
                ((ImageView)findViewById(ids[i])).setImageResource(R.drawable.pingfen02);
            }else {
                ((ImageView)findViewById(ids[i])).setImageResource(R.drawable.pingfen01);
            }
        }
    }

    private void submit(){
        Map<String,String> params = new HashMap<>();
        String json = "{\"cmd\":\"comment\",\"uid\":\""+ UserUtil.uid+"\",\"messageID\":\""+messageID+"\"" +
                ",\"score\":\""+score+"\"}";
        params.put("json",json );
        Log.i("6666", "submit: " + json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                Toast.makeText(ScoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                dismissLoading();
                BaseBean baseBean = gson.fromJson(response,BaseBean.class);
                if (baseBean.getResult().equals("1")){
                    Toast.makeText(ScoreActivity.this, baseBean.getResultNote(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ScoreActivity.this, "评分成功", Toast.LENGTH_SHORT).show();
                setResult(0xff);
                finish();
            }
        });
    }
}
