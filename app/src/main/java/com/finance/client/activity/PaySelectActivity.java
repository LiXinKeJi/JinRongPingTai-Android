package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.model.PaySelectBean;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class PaySelectActivity extends BaseActivity {
    private String merchantID;
    private int selectIndex = 0;
    private List<PaySelectBean.PriceList> priceList;
    private String[] projectIds = new String[]{"100", "200", "300", "400", "500", "600"};
    private TextView tv_publisher_phone;
    private String password, price;
    private TextView PayBtnB, PayBtnA;
    private EditText edit_password;
    private RelativeLayout get_publisher_phone, PayBtnBLayout;
    private LinearLayout PwdLayout;
    private String isOrder = "0";
    private int isSetPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "订购支付";
        setContentView(R.layout.activity_pay_order_select);
        super.onCreate(savedInstanceState);
        priceList = new ArrayList<>();
        merchantID = getIntent().getStringExtra("merchantID");
        isOrder = getIntent().getStringExtra("isOrder");
        tv_publisher_phone = (TextView) findViewById(R.id.tv_publisher_phone);
        PayBtnB = (TextView) findViewById(R.id.PayBtnB);
        PayBtnB.setOnClickListener(this);
        PayBtnA = (TextView) findViewById(R.id.PayBtnA);
        PayBtnA.setOnClickListener(this);
        get_publisher_phone = (RelativeLayout) findViewById(R.id.get_publisher_phone);
        edit_password = (EditText) findViewById(R.id.password);
        PayBtnBLayout = (RelativeLayout) findViewById(R.id.PayBtnBLayout);
        PwdLayout = (LinearLayout) findViewById(R.id.PwdLayout);
        requestInfo();
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = new Intent(this, PayActivity.class);
        if (v.getId() == R.id.PayBtnA) {
            if (selectIndex == -1) {
                Toast.makeText(this, "请选择订购类型", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edit_password.getText().toString().equals(password)) {
                Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
                return;
            } else {
                    intent.putExtra("price", priceList.get(selectIndex).getPrice());
                    intent.putExtra("id", priceList.get(selectIndex).getId());
                    intent.putExtra("projectId", projectIds[selectIndex]);
                    intent.putExtra("merchantID", merchantID);
                    intent.putExtra("selectIndex", selectIndex +"");
                    startActivity(intent);
            }

        } else if (v.getId() == R.id.PayBtnB) {

            if (!edit_password.getText().toString().equals(password)) {
                Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
                return;
            } else {
                intent.putExtra("price", price);
                intent.putExtra("id", "1000");
                intent.putExtra("projectId", "1000");
                intent.putExtra("merchantID", merchantID);
                startActivity(intent);
                finish();
            }
        }
    }

    private void requestInfo() {
        Map<String, String> params = Maps.newHashMap();
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
                    priceList.addAll(priceLists);
                    get_publisher_phone.setVisibility(View.VISIBLE);
                    PayBtnA.setVisibility(View.VISIBLE);
                    PayBtnBLayout.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(PaySelectActivity.this, "该商户未设置订购价格", Toast.LENGTH_SHORT).show();
                    get_publisher_phone.setVisibility(View.GONE);
                    PayBtnA.setVisibility(View.GONE);
                    PayBtnBLayout.setVisibility(View.GONE);
                }
                price = other.getPrice();
                tv_publisher_phone.setText("￥" + price);
                try {
                    initPriceList(priceLists);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initPriceList(final List<PaySelectBean.PriceList> priceList) throws JSONException {
        LinearLayout group = (LinearLayout) findViewById(R.id.ItemsLayout);
        group.removeAllViews();
        for (int i = 0; i < priceList.size(); ++i) {
            final int index = i;
            View view = View.inflate(this, R.layout.price_item_layout, null);
            String project = priceList.get(i).getProject();
            String id = priceList.get(i).getId();
            String price = priceList.get(i).getPrice();
            if (isOrder.equals("1")) {
                if (id.equals("100")) {
                    continue;
                } else {
                    ((TextView) view.findViewById(R.id.Title)).setText(project);
                    ((TextView) view.findViewById(R.id.Price)).setText("¥" + price);
                }
            } else {
                ((TextView) view.findViewById(R.id.Title)).setText(project);
                ((TextView) view.findViewById(R.id.Price)).setText("¥" + price);
            }
            if (selectIndex == i) {
                ((ImageView) view.findViewById(R.id.ChooseBtn)).setImageResource(R.drawable.select_icon);
            } else {
                ((ImageView) view.findViewById(R.id.ChooseBtn)).setImageResource(R.drawable.unselect_icon);
            }
            group.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectIndex != index) {
                        selectIndex = index;
                        try {
                           initPriceList(priceList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
