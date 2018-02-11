package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.PaySelectAdapter;
import com.finance.client.model.BaseBean;
import com.finance.client.model.PaySelectBean;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class PaySelectActivity extends BaseActivity {
    private String merchantID;
    private List<PaySelectBean.PriceList> priceList;
    private TextView tv_publisher_phone;
    private String password, price,pricePhone;
    private TextView PayBtnB, PayBtnA;
    private EditText edit_password;
    private RelativeLayout get_publisher_phone, PayBtnBLayout;
    private LinearLayout PwdLayout;
    private String isOrder = "0";
    private View headView,footView;
    private String projectId;
    private ListView select_price_list;
    private PaySelectAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "订购支付";
        setContentView(R.layout.activity_pay_order_select);
        super.onCreate(savedInstanceState);
        priceList = new ArrayList<>();
        merchantID = getIntent().getStringExtra("merchantID");
        isOrder = getIntent().getStringExtra("isOrder");
        initView();
        requestInfo();
    }

    private void initView() {
        select_price_list = (ListView) findViewById(R.id.select_price_list);
        headView = LayoutInflater.from(PaySelectActivity.this).inflate(R.layout.head_select_price,null);
        footView = LayoutInflater.from(PaySelectActivity.this).inflate(R.layout.foot_select_price,null);
        if (headView != null) select_price_list.addHeaderView(headView);
        if (footView != null) select_price_list.addFooterView(footView);
        tv_publisher_phone = (TextView) footView.findViewById(R.id.tv_publisher_phone);
        PayBtnB = (TextView) footView.findViewById(R.id.PayBtnB);
        PayBtnB.setOnClickListener(this);
        PayBtnA = (TextView) footView.findViewById(R.id.PayBtnA);
        PayBtnA.setOnClickListener(this);
        get_publisher_phone = (RelativeLayout) footView.findViewById(R.id.get_publisher_phone);
        PayBtnBLayout = (RelativeLayout) footView.findViewById(R.id.PayBtnBLayout);
        edit_password = (EditText) headView.findViewById(R.id.password);
        PwdLayout = (LinearLayout) headView.findViewById(R.id.PwdLayout);
        mAdapter = new PaySelectAdapter(PaySelectActivity.this,priceList);
        select_price_list.setAdapter(mAdapter);
        select_price_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setDefSelect(position-1);
                price = priceList.get(position-1).getPrice();
                projectId = priceList.get(position-1).getId();

            }
        });
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case  R.id.PayBtnA:
                if (!TextUtils.isEmpty(password)){
                    if (!edit_password.getText().toString().equals(password)) {
                        Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                getOrderId(price,projectId);
                break;
            case R.id.PayBtnB:
                if (!TextUtils.isEmpty(password)){
                    if (!edit_password.getText().toString().equals(password)) {
                        Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                getOrderId(pricePhone,projectId);
                break;
        }
    }
    private void getOrderId(final String money, final String projectId) {
        Map<String,String> params = new HashMap<>();
        String json = "{\"cmd\":\"payProject\",\"uid\":\""+ UserUtil.uid+"\",\"price\":\""+money+"\"" +
                ",\"merchantID\":\""+merchantID+"\",\"verifyPsd\":\""+password+"\",\"project\":\""+projectId+"\"}";
        params.put("json",json);
        showLoading();
        Log.i("sfdgdsfg", "getOrderId: " + json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(PaySelectActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                dismissLoading();
            }
            @Override
            public void onResponse(String response, int id) {
                Log.i("sfdgdsfg", "getOrderId: " + response);
                Gson gson = new Gson();
                dismissLoading();
                BaseBean baseBean = gson.fromJson(response,BaseBean.class);
                if (baseBean.getResult().equals("1")){
                    Toast.makeText(PaySelectActivity.this,baseBean.getResultNote(),Toast.LENGTH_SHORT).show();
                    return;
                }
                String orderNo = baseBean.getOrderNo();
                if (!TextUtils.isEmpty(orderNo)){
                    Intent intent=new Intent(PaySelectActivity.this,PayActivity.class);
                    intent.putExtra("price",money);
                    intent.putExtra("projectId", projectId);
                    intent.putExtra("orderNo",orderNo);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    private void requestInfo() {
        final Map<String, String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"getPriceList\",\"uid\":\"" + UserUtil.uid + "\",\"merchantID\":\"" + merchantID + "\"}";
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                Toast.makeText(PaySelectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("response", "onResponse: " + response);
                Gson gson = new Gson();
                dismissLoading();
                PaySelectBean paySelectBean = gson.fromJson(response,PaySelectBean.class);
                if (paySelectBean.getResult().equals("1")){
                    Toast.makeText(PaySelectActivity.this, paySelectBean.getResultNote(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<PaySelectBean.PriceList> priceLists = paySelectBean.getPriceList();
                PaySelectBean.Other other = paySelectBean.getOther();
                password = paySelectBean.getPassword();
                if (TextUtils.isEmpty(password)) {
                    PwdLayout.setVisibility(View.GONE);
                } else {
                    PwdLayout.setVisibility(View.VISIBLE);
                }
                if (priceLists != null && !priceLists.isEmpty() && priceLists.size() > 0){
                    projectId = priceLists.get(0).getId();
                    price = priceLists.get(0).getPrice();
                    priceList.addAll(priceLists);
                    mAdapter.notifyDataSetChanged();
                    get_publisher_phone.setVisibility(View.VISIBLE);
                    PayBtnA.setVisibility(View.VISIBLE);
                    PayBtnBLayout.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(PaySelectActivity.this, "该商户未设置订购价格", Toast.LENGTH_SHORT).show();
                    get_publisher_phone.setVisibility(View.GONE);
                    PayBtnA.setVisibility(View.GONE);
                    PayBtnBLayout.setVisibility(View.GONE);
                }
                pricePhone = other.getPrice();
                tv_publisher_phone.setText("￥" + pricePhone);

            }
        });
    }

}
