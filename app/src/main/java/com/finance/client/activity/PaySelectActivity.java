package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.google.common.collect.Maps;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class PaySelectActivity extends BaseActivity{
    private String merchantID;
    private int selectIndex = 0;
    private JSONArray priceList;
    private String[] projectIds=new String[]{"100","200","300","400","500","600"};
    private TextView tv_publisher_phone;
    private String password,price;
    private TextView PayBtnB,PayBtnA;
    private EditText edit_password;
    private RelativeLayout get_publisher_phone,PayBtnBLayout;
    private LinearLayout PwdLayout;
    private String isOrder = "0";
    private int isSetPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "订购支付";
        setContentView(R.layout.activity_pay_order_select);
        super.onCreate(savedInstanceState);
        merchantID = getIntent().getStringExtra("merchantID");
        isOrder = getIntent().getStringExtra("isOrder");
        this.requestInfo();
        tv_publisher_phone=(TextView)findViewById(R.id.tv_publisher_phone);
        PayBtnB=(TextView)findViewById(R.id.PayBtnB);
        PayBtnB.setOnClickListener(this);
        PayBtnA=(TextView)findViewById(R.id.PayBtnA);
        PayBtnA.setOnClickListener(this);
        get_publisher_phone=(RelativeLayout)findViewById(R.id.get_publisher_phone);
        edit_password=(EditText)findViewById(R.id.password);
        PayBtnBLayout=(RelativeLayout)findViewById(R.id.PayBtnBLayout);
        PwdLayout=(LinearLayout)findViewById(R.id.PwdLayout);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = new Intent(this,PayActivity.class);
        if(v.getId() == R.id.PayBtnA){
            if(selectIndex == -1){
                Toast.makeText(this, "请选择订购类型", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isSetPassword==1){
                if (!edit_password.getText().toString().equals(password)){
                    Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    try {
                        intent.putExtra("price", priceList.getJSONObject(selectIndex).getString("price"));
                        intent.putExtra("id", priceList.getJSONObject(selectIndex).getString("id"));
                        intent.putExtra("projectId", projectIds[selectIndex]);
                        intent.putExtra("merchantID", merchantID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    finish();
                }
            }
        }else if(v.getId() == R.id.PayBtnB){
            if (isSetPassword==1){
                if (!edit_password.getText().toString().equals(password))
                {
                    Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    intent.putExtra("price", price);
                    intent.putExtra("id", "1000");
                    intent.putExtra("projectId", "1000");
                    intent.putExtra("merchantID", merchantID);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    private void requestInfo(){
        Map<String,String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"getPriceList\",\"uid\":\""+UserUtil.uid+"\",\"merchantID\":\""+merchantID+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                Toast.makeText(PaySelectActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response, int id) {
                Log.i("response", "onResponse: " + response);
                dismissLoading();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("1")) {
                        Toast.makeText(PaySelectActivity.this, jsonObject.getString("resultNote"), Toast.LENGTH_SHORT).show();
                    }else {
                        updateView(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void updateView(JSONObject info) throws JSONException {
        priceList = info.getJSONArray("priceList");
        if(priceList.length() == 0){
            Toast.makeText(this, "该商户未设置订购价格", Toast.LENGTH_SHORT).show();
            get_publisher_phone.setVisibility(View.GONE);
            PayBtnA.setVisibility(View.GONE);
            PayBtnBLayout.setVisibility(View.GONE);
            return;
        }
        isSetPassword = info.getInt("isSetPassword");
        if (isSetPassword == 0){
            PwdLayout.setVisibility(View.GONE);
        }else {
            PwdLayout.setVisibility(View.VISIBLE);
        }
        get_publisher_phone.setVisibility(View.VISIBLE);
        PayBtnA.setVisibility(View.VISIBLE);
        PayBtnBLayout.setVisibility(View.VISIBLE);
        password = info.getString("password");
        JSONObject jsonObject=info.getJSONObject("other");
        price=jsonObject.getString("price");
        tv_publisher_phone.setText("￥"+price);
        this.initPriceList();
    }

    private void initPriceList() throws JSONException {
        LinearLayout group = (LinearLayout) findViewById(R.id.ItemsLayout);
        group.removeAllViews();
        for(int i = 0; i < priceList.length(); ++i){
            final int index = i;
            View view = View.inflate(this,R.layout.price_item_layout,null);
            JSONObject obj = priceList.getJSONObject(i);
            if (isOrder.equals("1")){
                if (obj.getString("id").equals("100")){
                    continue;
                }else {
                    ((TextView)view.findViewById(R.id.Title)).setText(obj.getString("project"));
                    ((TextView)view.findViewById(R.id.Price)).setText("¥"+obj.getString("price"));
                }
            }else {
                ((TextView)view.findViewById(R.id.Title)).setText(obj.getString("project"));
                ((TextView)view.findViewById(R.id.Price)).setText("¥"+obj.getString("price"));
            }
            if(selectIndex == i){
                ((ImageView)view.findViewById(R.id.ChooseBtn)).setImageResource(R.drawable.select_icon);
            }else{
                ((ImageView)view.findViewById(R.id.ChooseBtn)).setImageResource(R.drawable.unselect_icon);
            }
            group.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectIndex != index){
                        selectIndex = index;
                        try {
                            initPriceList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
