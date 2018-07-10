package com.finance.client.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.model.CompanyInfoBean;
import com.finance.client.model.MasterDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yhrun.alchemy.Util.ImageLoaderUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/5
 */

public class CompanyInfoActivity extends BaseActivity {
    private String id;
    private MasterDao info;
    private TextView rightBtn, statusInfo;
    private RatingBar ratingBar;
    private TextView tv_score;
    private TextView tv_num;

    private ImageView image1,image2,image3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = getIntent().getStringExtra("name");
        setContentView(R.layout.activity_company_layout);
        super.onCreate(savedInstanceState);
        rightBtn = (TextView) findViewById(R.id.RightBtnText);
        statusInfo = (TextView) findViewById(R.id.company_statusInfo);
        rightBtn.setText("设置备注");
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_num = (TextView) findViewById(R.id.tv_num);
        id = getIntent().getStringExtra("id");

        image1= (ImageView) findViewById(R.id.image1);
        image2= (ImageView) findViewById(R.id.image2);
        image3= (ImageView) findViewById(R.id.image3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.RightBtnText:
                if (!info.getAttention().equals("1")) {
                    ToastUtils.showMessageShort(this, "请先关注");
                    return;
                }
                Intent intent = new Intent(this, RemarkActivity.class);
                intent.putExtra("Remarks", info.getNickName());
                intent.putExtra("id", info.getMerchantId());
                startActivity(intent);
                break;
        }
    }

    private void requestData() {
        Map<String, String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"getAuthorDetail\",\"uid\":\"" + UserUtil.uid + "\",\"merchantID\":\"" + id + "\"}";
        params.put("json", json);
        Log.e("获取订购人信息", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(CompanyInfoActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("订购人信息", response);
                Gson gson = new Gson();
                dismissLoading();
                CompanyInfoBean companyInfoBean = gson.fromJson(response, CompanyInfoBean.class);
                if (companyInfoBean.getResult().equals("1")) {
                    ToastUtils.makeText(CompanyInfoActivity.this, companyInfoBean.getResultNote());
                    return;
                }
                info = companyInfoBean.getDataList();
                UpdateView();
            }
        });
    }

    private void UpdateView() {
        if (info != null) {
            tv_num.setText(info.getCommentNum() + "人评价");
            if (!TextUtils.isEmpty(info.getLogo()))
                ImageLoaderUtil.getInstance().displayImage(info.getLogo(), (ImageView) findViewById(R.id.Logo));
            else ((ImageView) findViewById(R.id.Logo)).setImageResource(R.drawable.ic_launcher);
            if (TextUtils.isEmpty(info.getNickName())) {
                ((TextView) findViewById(R.id.Name)).setText(info.getName());
            } else {
                ((TextView) findViewById(R.id.Name)).setText(info.getName() + "(" + info.getNickName() + ")");
            }
            if (!TextUtils.isEmpty(info.getMerchantId()))
                ((TextView) findViewById(R.id.ID)).setText("ID号: " + info.getMerchantId());
            if (!TextUtils.isEmpty(info.getCategory()))
                ((TextView) findViewById(R.id.Category)).setText("分类: " + info.getCategory());
            if (!TextUtils.isEmpty(info.getFansNumber()))
                ((TextView) findViewById(R.id.Fans)).setText(info.getFansNumber() + "人订阅");
            if (!TextUtils.isEmpty(info.getIntroduction())) {
                ((TextView) findViewById(R.id.Desc)).setText("个性签名：" + info.getSignature());
                ((TextView) findViewById(R.id.Info)).setText(info.getIntroduction());
            }
            if (!TextUtils.isEmpty(info.getAddress()))
                ((TextView) findViewById(R.id.Address)).setText(info.getAddress());
            if (!TextUtils.isEmpty(info.getScore()))
                ratingBar.setRating(Float.valueOf(info.getScore()));
            if (!TextUtils.isEmpty(info.getScore())) tv_score.setText(info.getScore());

            if(!TextUtils.isEmpty(info.getPublicImage())){
               ImageLoader.getInstance().displayImage(info.getPublicImage(),image1);
            }else{
                image1.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(info.getPublicitImage())){
                ImageLoader.getInstance().displayImage(info.getPublicImage(),image2);
            }else{
                image2.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(info.getPublicityImage())){
                ImageLoader.getInstance().displayImage(info.getPublicImage(),image3);
            }else{
                image3.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(info.getStatus())) {
                switch (info.getStatus()) {
                    case "0":
                        statusInfo.setBackgroundResource(R.drawable.black_15);
                        statusInfo.setText("已订购");
                        statusInfo.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case "1":
                        statusInfo.setBackgroundResource(R.drawable.black_15);
                        statusInfo.setText("订购");
                        statusInfo.setTextColor(Color.parseColor("#ffffff"));
                        statusInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!info.getAttention().equals("1")) {
                                    ToastUtils.showMessageShort(CompanyInfoActivity.this, "请先关注");
                                    return;
                                }
                                Intent intent = new Intent(CompanyInfoActivity.this, PaySelectActivity.class);
                                intent.putExtra("merchantID", info.getMerchantId());
                                intent.putExtra("isOrder", "0");
                                startActivity(intent);
                            }
                        });
                        break;
                    case "2":
//                        statusInfo.setBackgroundResource(R.drawable.gray_15);
//                        statusInfo.setText("订购已满");
//                        statusInfo.setTextColor(Color.parseColor("#777777"));
                        break;
                }

                if (info.getIsman().equals("1")) {
                    if (info.getStatus().equals("0")) {
                        statusInfo.setBackgroundResource(R.drawable.black_15);
                        statusInfo.setText("已订购");
                        statusInfo.setTextColor(Color.parseColor("#ffffff"));
                    } else {
                        statusInfo.setBackgroundResource(R.drawable.gray_15);
                        statusInfo.setText("订购已满");
                        statusInfo.setTextColor(Color.parseColor("#777777"));
                    }
                }
            }
        }
    }

}
