package com.finance.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.CashierInputFilter;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.pingplusplus.android.Pingpp;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

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
    private String channel = "alipay", charge,body = "充值";
    private TextView txtAlipay, txtWx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "充值";
        setContentView(R.layout.recharge_layout);
        super.onCreate(savedInstanceState);
        aliBtn = (TextView) findViewById(R.id.ali);
        wechatBtn = (TextView) findViewById(R.id.Wechat);
        editPrice = (EditText) findViewById(R.id.edit_price);
        InputFilter[] filter = {new CashierInputFilter()};
        editPrice.setFilters(filter);
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
        Map<String, String> params = new HashMap<>();
        final String json = "{\"cmd\":\"depositOrder\",\"uid\":\""+UserUtil.uid+"\",\"money\":\""+editPrice.getText().toString()+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(RechargeActivity.this,e.getMessage());
            }
            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(RechargeActivity.this, obj.getString("resultNote"));
                        return;
                    }
                    orderId = obj.getString("orderNo");
                    if (!TextUtils.isEmpty(orderId)){
                        recharge();
                    }else {
                        ToastUtils.makeText(RechargeActivity.this, "生成订单失败，请重试！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void recharge() {
        double amount = Double.parseDouble(editPrice.getText().toString()) * 100;
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        Map<String, String> params = new HashMap<>();
        final String json = "{\"cmd\":\"getCharge\",\"orderNo\":\""+orderId+"\",\"amount\":\""+decimalFormat.format(amount)+"\"" +
                ",\"channel\":\""+channel+"\",\"body\":\""+body+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(RechargeActivity.this,e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(RechargeActivity.this, obj.getString("resultNote"));
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
