package com.finance.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import com.pingplusplus.android.Pingpp;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * User : yh
 * Date : 17/8/15
 */

public class RechargeActivity extends BaseActivity {
    private TextView aliBtn;
    private TextView wechatBtn;
    private Drawable selectIcon;
    private Drawable unSelectIcon;
    private EditText editPrice;
    private String orderId;
    private String channel = "alipay", charge;
    private TextView txtAlipay, txtWx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "充值";
        setContentView(R.layout.recharge_layout);
        super.onCreate(savedInstanceState);
        aliBtn = (TextView) findViewById(R.id.ali);
        wechatBtn = (TextView) findViewById(R.id.Wechat);
        editPrice = (EditText) findViewById(R.id.edit_price);
        txtAlipay = (TextView) findViewById(R.id.ali);
        txtWx = (TextView) findViewById(R.id.Wechat);
        txtAlipay.setOnClickListener(this);
        txtWx.setOnClickListener(this);
        aliBtn.setOnClickListener(this);
        wechatBtn.setOnClickListener(this);
        findViewById(R.id.ChangeBtn).setOnClickListener(this);
        selectIcon = getResources().getDrawable(R.drawable.chongzhi01);
        unSelectIcon = getResources().getDrawable(R.drawable.chongzhi02);
        selectIcon.setBounds(0, 0, selectIcon.getMinimumWidth(), selectIcon.getMinimumHeight());
        unSelectIcon.setBounds(0, 0, unSelectIcon.getMinimumWidth(), unSelectIcon.getMinimumHeight());
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.ChangeBtn:
                if (TextUtils.isEmpty(editPrice.getText().toString())) {
                    Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(UserUtil.uid)){
                    Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestOrderId();
                break;
            case R.id.ali:
                channel = "alipay";
                aliBtn.setCompoundDrawables(selectIcon, null, null, null);
                wechatBtn.setCompoundDrawables(unSelectIcon, null, null, null);
                break;
            case R.id.Wechat:
                channel = "wx";
                aliBtn.setCompoundDrawables(unSelectIcon, null, null, null);
                wechatBtn.setCompoundDrawables(selectIcon, null, null, null);
                break;
        }
    }

    private void requestOrderId() {
        Map<String, String> params = Maps.newHashMap();
        params.put("uid", UserUtil.uid);
        params.put("cmd", "depositOrder");
        params.put("money", editPrice.getText().toString());
        showLoading();
        AsyncClient.Get().setParams(params).setHost(Content.DOMAIN).setReturnClass(String.class).execute(new AsyncResponseHandler<String>() {
            @Override
            public void onResult(boolean success, String result, ResponseError error) {
                Log.e("获取订单号........", result);
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("result").equals("1")) {
                        Toast.makeText(RechargeActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    orderId = obj.getString("orderNo");
                    recharge();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void recharge() {
        double amount = Double.parseDouble(editPrice.getText().toString()) * 100;
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        Map<String, String> params = Maps.newHashMap();
        params.put("cmd", "getCharge");
        params.put("orderNo", orderId);
        params.put("amount", decimalFormat.format(amount));
        params.put("channel", channel);
        params.put("body", "备注");
        showLoading();
        AsyncClient.Get().setParams(params).setHost(Content.DOMAIN).setReturnClass(String.class).execute(new AsyncResponseHandler<String>() {
            @Override
            public void onResult(boolean success, String result, ResponseError error) {
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("result").equals("1")) {
                        Toast.makeText(RechargeActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    charge = obj.getString("charge");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Pingpp.createPayment(RechargeActivity.this, charge);
                        }
                    });
                } catch (Exception e) {
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
                    Intent intent = new Intent(RechargeActivity.this, ChongzhiChenggongActivity.class);
                    intent.putExtra("amount", editPrice.getText().toString());
                    startActivity(intent);
                }
            }
        }
    }
}
