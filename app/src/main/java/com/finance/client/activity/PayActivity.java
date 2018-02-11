package com.finance.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.pingplusplus.android.Pingpp;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * User : yh
 * Date : 17/9/3
 */

public class PayActivity extends BaseActivity{
    private String price;
    private ImageView img_weixin,img_alipay,img_wallet;
    private RelativeLayout rl_wx,rl_apliay;
    private Drawable selectIcon;
    private Drawable unSelectIcon;
    private String channel="wx";
    private String orderId;
    private TextView OrderId;
    private String charge;
    private TextView txtPay;
    private String projectId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "支付";
        setContentView(R.layout.activity_pay);
        super.onCreate(savedInstanceState);
        price = getIntent().getStringExtra("price");
        orderId=getIntent().getStringExtra("orderNo");
        projectId=getIntent().getStringExtra("projectId");
        ((TextView)findViewById(R.id.txt_price)).setText("￥"+price);

        img_weixin= (ImageView) findViewById(R.id.img_weixin);
        img_alipay= (ImageView) findViewById(R.id.img_alipay);
        img_wallet= (ImageView) findViewById(R.id.img_wallet);
        rl_wx = (RelativeLayout) findViewById(R.id.rl_wx);
        rl_apliay = (RelativeLayout) findViewById(R.id.rl_apliay);
        OrderId=(TextView)findViewById(R.id.OrderId);
        OrderId.setText("订单号：" + orderId);
        img_weixin.setOnClickListener(this);
        img_alipay.setOnClickListener(this);
        img_wallet.setOnClickListener(this);
        txtPay=(TextView)findViewById(R.id.txt_pay);
        txtPay.setText("确认支付"+price+"元");
        findViewById(R.id.SubmitBtn).setOnClickListener(this);
        selectIcon=getResources().getDrawable(R.drawable.choose_select);
        unSelectIcon=getResources().getDrawable(R.drawable.choose);

    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.img_weixin:
                channel="wx";
                img_weixin.setImageDrawable(selectIcon);
                img_alipay.setImageDrawable(unSelectIcon);
                img_wallet.setImageDrawable(unSelectIcon);
                break;
            case R.id.img_alipay:
                channel="alipay";
                img_weixin.setImageDrawable(unSelectIcon);
                img_alipay.setImageDrawable(selectIcon);
                img_wallet.setImageDrawable(unSelectIcon);
                break;
            case R.id.img_wallet:
                channel="balancePay";
                img_weixin.setImageDrawable(unSelectIcon);
                img_alipay.setImageDrawable(unSelectIcon);
                img_wallet.setImageDrawable(selectIcon);
                break;
            case R.id.SubmitBtn:
                recharge();
                break;
        }
    }

    private void recharge(){
        double amount = Double.parseDouble(price);
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        Map<String,String> params = Maps.newHashMap();
        if (channel.equals("balancePay")) {
            params.put("cmd", "balancePay");
            params.put("amount",decimalFormat.format(amount));
        }else{
            params.put("cmd", "getCharge");
            params.put("amount",decimalFormat.format(amount*100));
        }
        params.put("orderNo",orderId);
        params.put("channel",channel);
        params.put("body","备注");
        showLoading();
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        try{
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("1")){
                                Toast.makeText(PayActivity.this, obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (channel.equals("balancePay"))
                            {

                                Toast.makeText(PayActivity.this, obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(PayActivity.this,UpdateServiceSuccessActivity.class);
                                intent.putExtra("amount", price);
                                intent.putExtra("projectId", projectId);
                                startActivity(intent);
                                finish();
                            }else {
                                charge = obj.getString("charge");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Pingpp.createPayment(PayActivity.this, charge);
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
                    Intent intent = new Intent(PayActivity.this, UpdateServiceSuccessActivity.class);
                    intent.putExtra("amount", price);
                    intent.putExtra("projectId", projectId);
                    MyApplication.temp = 1;
                    startActivity(intent);
                    finish();
                }
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//                showMsg(result, errorMsg, extraMsg);
            }
        }
    }
}
