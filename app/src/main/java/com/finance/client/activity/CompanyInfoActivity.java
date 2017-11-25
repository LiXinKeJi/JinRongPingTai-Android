package com.finance.client.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.model.MasterDao;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.yhrun.alchemy.Util.FastJsonUtil;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import org.json.JSONObject;

import java.util.Map;

/**
 * User : yh
 * Date : 17/9/5
 */

public class CompanyInfoActivity extends BaseActivity{
    private String id;
    private MasterDao info;
    private TextView rightBtn,statusInfo;
    private RatingBar ratingBar;
    private TextView tv_score;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = getIntent().getStringExtra("name");
        setContentView(R.layout.activity_company_layout);
        super.onCreate(savedInstanceState);
        rightBtn = (TextView) findViewById(R.id.RightBtnText);
        statusInfo = (TextView) findViewById(R.id.StatusInfo);
        rightBtn.setText("设置备注");
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(this);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        tv_score=(TextView)findViewById(R.id.tv_score);
        id = getIntent().getStringExtra("id");

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.requestData();
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.RightBtnText){
            Intent intent = new Intent(this,RemarkActivity.class);
            intent.putExtra("id",info.getMerchantID());
            startActivity(intent);
        }
    }

    private void requestData(){
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","getAuthorDetail");
        params.put("uid", UserUtil.uid);
        params.put("merchantID",id);
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(success){
                            try {
                                JSONObject obj = new JSONObject(result);
//                                Toast.makeText(CompanyInfoActivity.this, "获取发布者详情成功", Toast.LENGTH_SHORT).show();
                                info = (MasterDao) FastJsonUtil.parseObject(obj.getString("dataList"),MasterDao.class);
                                UpdateView();
                            }catch (Exception e){

                            }
                        }else{
                            Toast.makeText(CompanyInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void UpdateView() {
        if (info != null && !TextUtils.isEmpty(info.getLogo())) {
            ImageLoaderUtil.getInstance().displayImage(info.getLogo(), (ImageView) findViewById(R.id.Logo));
        }else
        {
            ((ImageView) findViewById(R.id.Logo)).setImageResource(R.drawable.ic_launcher);
        }
        if (info != null ) {
            if (TextUtils.isEmpty(info.getNickName())) {
                ((TextView) findViewById(R.id.Name)).setText(info.getName());
            }else
            {
                ((TextView) findViewById(R.id.Name)).setText(info.getNickName());
            }
        }
        if (info != null && !TextUtils.isEmpty(info.getMerchantID())) {
            ((TextView) findViewById(R.id.ID)).setText("ID号: " + info.getMerchantID());
        }
        if (info != null && !TextUtils.isEmpty(info.getCategory())) {
            ((TextView) findViewById(R.id.Category)).setText("分类: " + info.getCategory());
        }
        if (info != null && !TextUtils.isEmpty(info.getFansNumber())) {
            ((TextView) findViewById(R.id.Fans)).setText(info.getFansNumber() + "人订阅");
        }
        if (info != null && !TextUtils.isEmpty(info.getIntroduction())) {
            ((TextView) findViewById(R.id.Desc)).setText(info.getIntroduction());
            ((TextView) findViewById(R.id.Info)).setText(info.getIntroduction());
        }
        if (info != null && !TextUtils.isEmpty(info.getAddress())) {
            ((TextView) findViewById(R.id.Address)).setText(info.getAddress());
        }
        if (info != null && !TextUtils.isEmpty(info.getScore())) {
            ratingBar.setRating(Float.valueOf(info.getScore()));
        }
        if (info != null && !TextUtils.isEmpty(info.getScore())) {
            tv_score.setText(info.getScore());
        }
        if (info != null && !TextUtils.isEmpty(info.getStatus())) {
            if (info.getStatus().equals("0")) {
                statusInfo.setBackgroundResource(R.drawable.black_15);
                statusInfo.setText("已订购");
                statusInfo.setTextColor(Color.parseColor("#ffffff"));
            } else if (info.getStatus().equals("1")) {
                statusInfo.setBackgroundResource(R.drawable.black_15);
                statusInfo.setText("订购");
                statusInfo.setTextColor(Color.parseColor("#ffffff"));
            } else if (info.getStatus().equals("2")) {
                statusInfo.setBackgroundResource(R.drawable.gray_15);
                statusInfo.setText("订购已满");
                statusInfo.setTextColor(Color.parseColor("#777777"));
            }
        }
    }
}
