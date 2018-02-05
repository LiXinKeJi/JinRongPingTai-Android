package com.finance.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
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
    private String merchantID;
    private String selectIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "支付";
        setContentView(R.layout.activity_pay);
        super.onCreate(savedInstanceState);
        price = getIntent().getStringExtra("price");
        projectId=getIntent().getStringExtra("projectId");
        merchantID=getIntent().getStringExtra("merchantID");
        selectIndex=getIntent().getStringExtra("selectIndex");
        Log.e("projectId---",projectId);
        ((TextView)findViewById(R.id.txt_price)).setText("￥"+price);
        img_weixin= (ImageView) findViewById(R.id.img_weixin);
        img_alipay= (ImageView) findViewById(R.id.img_alipay);
        img_wallet= (ImageView) findViewById(R.id.img_wallet);
        rl_wx = (RelativeLayout) findViewById(R.id.rl_wx);
        rl_apliay = (RelativeLayout) findViewById(R.id.rl_apliay);
        if (selectIndex.equals("0")){
            rl_apliay.setVisibility(View.GONE);
            rl_wx.setVisibility(View.GONE);
        }else {
            rl_apliay.setVisibility(View.VISIBLE);
            rl_wx.setVisibility(View.VISIBLE);
        }
        OrderId=(TextView)findViewById(R.id.OrderId);
        img_weixin.setOnClickListener(this);
        img_alipay.setOnClickListener(this);
        img_wallet.setOnClickListener(this);
        txtPay=(TextView)findViewById(R.id.txt_pay);
        txtPay.setText("确认支付"+price+"元");
        findViewById(R.id.SubmitBtn).setOnClickListener(this);
        selectIcon=getResources().getDrawable(R.drawable.choose_select);
        unSelectIcon=getResources().getDrawable(R.drawable.choose);
        requestOrderId();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId()==R.id.img_weixin)
        {
            channel="wx";
            img_weixin.setImageDrawable(selectIcon);
            img_alipay.setImageDrawable(unSelectIcon);
            img_wallet.setImageDrawable(unSelectIcon);
        }else if (v.getId()==R.id.img_alipay)
        {
            channel="alipay";
            img_weixin.setImageDrawable(unSelectIcon);
            img_alipay.setImageDrawable(selectIcon);
            img_wallet.setImageDrawable(unSelectIcon);
        }else if (v.getId()==R.id.img_wallet)
        {
            channel="balancePay";
            img_weixin.setImageDrawable(unSelectIcon);
            img_alipay.setImageDrawable(unSelectIcon);
            img_wallet.setImageDrawable(selectIcon);
        }else if(v.getId()==R.id.SubmitBtn)
        {
            recharge();
        }
    }



    private void requestOrderId() {
        Map<String, String> params = Maps.newHashMap();
        params.put("merchantID", merchantID);
        params.put("uid", UserUtil.uid);
        params.put("cmd", "createOrder");
        params.put("money",String.valueOf(Double.parseDouble(price)*100));
        showLoading();
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
//                        Log.e("获取订单号........",result);
                        dismissLoading();
                        try{
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("1")){
                                Toast.makeText(PayActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            orderId=obj.getString("orderNo");
                            OrderId.setText("订单号："+orderId);
//                            recharge();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }





    private void recharge(){
        double amount=Double.parseDouble(price);
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
//                        Log.e("channel........",result);
//                        Toast.makeText(PayActivity.this,orderId,Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject obj = new JSONObject(result);
                            if(obj.getString("result").equals("1")){
                                Toast.makeText(PayActivity.this, obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                return;
                            }
//                            Toast.makeText(RechargeActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
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
//                                    Toast.makeText(RechargeActivity.this, "hah", Toast.LENGTH_SHORT).show();

                                        Pingpp.createPayment(PayActivity.this, charge);

//                                    Pingpp.createPayment(RechargeActivity.this, charge, "qwalletXXXXXXX");
                                    }
                                });
                            }

//                            startActivity(new Intent(RechargeActivity.this,ChongzhiChenggongActivity.class));
//                            finish();
//                            updateView();
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
                if (result.equals("success"))
                {
                    Intent intent = new Intent(PayActivity.this, UpdateServiceSuccessActivity.class);
                    intent.putExtra("amount", price);
                    startActivity(intent);
                    finish();
                }
            /* 处理返回值
             * "success" - 支付成功
             * "fail"    - 支付失败
             * "cancel"  - 取消支付
             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
             * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
             */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }



}
