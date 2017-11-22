package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.library.BaseActivity;
import com.finance.library.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import org.json.JSONObject;

import java.util.Map;

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
        for(int id : ids){
            findViewById(id).setOnClickListener(this);
        }
        findViewById(R.id.LoginBtn).setOnClickListener(this);
        messageID = getIntent().getStringExtra("msgId");
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
        Map<String,Object> params = Maps.newHashMap();
        params.put("cmd","comment");
        params.put("uid", UserUtil.uid);
        params.put("messageID",messageID);
        params.put("score",score);
        showLoading();
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(!success){
                            Toast.makeText(ScoreActivity.this, "评分失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("0")){
                                Toast.makeText(ScoreActivity.this, "评分成功", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }else{
                                Toast.makeText(ScoreActivity.this, ""+obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        setResult(0xff);
                        finish();
                    }
                });
    }
}
