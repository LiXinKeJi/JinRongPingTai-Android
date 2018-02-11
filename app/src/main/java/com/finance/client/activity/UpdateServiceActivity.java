package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.model.BaseBean;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/5
 */

public class UpdateServiceActivity extends BaseActivity {
    private TextView txtPay3;
    private EditText edit_crowdfunding;
    private String guCoin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "升级服务";
        setContentView(R.layout.activity_update_service);
        super.onCreate(savedInstanceState);
        guCoin=getIntent().getStringExtra("guCoin");
        ((TextView)findViewById(R.id.txt_guCoin)).setText(guCoin);
        txtPay3=(TextView)findViewById(R.id.txt_pay3);
        txtPay3.setOnClickListener(this);
        edit_crowdfunding=(EditText)findViewById(R.id.edit_crowdfunding);
        if (TextUtils.isEmpty(guCoin))
        {
            ((TextView)findViewById(R.id.txt_guCoin)).setText("您当前累计消费0.0元，当前累计股币0.0股币");
        }
        else {
            ((TextView) findViewById(R.id.txt_guCoin)).setText("您当前累计消费" + guCoin + "元，当前累计积分" + guCoin + "股币");
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.txt_pay3:
                String money = edit_crowdfunding.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    Toast.makeText(this,"请输入众筹金额",Toast.LENGTH_SHORT).show();
                    return;
                }
                getOrderId(money);
                break;
        }

    }


    private void getOrderId(final String money) {
        Map<String,String> params = new HashMap<>();
        String json = "{\"cmd\":\"upgradeService\",\"uid\":\""+ UserUtil.uid+"\",\"money\":\""+money+"\"}";
        params.put("json",json);
        showLoading();
        Log.i("sfdgdsfg", "getOrderId: " + json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(UpdateServiceActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                dismissLoading();
            }
            @Override
            public void onResponse(String response, int id) {
                Log.i("sfdgdsfg", "getOrderId: " + response);
                Gson gson = new Gson();
                dismissLoading();
                BaseBean baseBean = gson.fromJson(response,BaseBean.class);
                if (baseBean.getResult().equals("1")){
                    Toast.makeText(UpdateServiceActivity.this,baseBean.getResultNote(),Toast.LENGTH_SHORT).show();
                    return;
                }
                String orderNo = baseBean.getOrderNo();
                if (!TextUtils.isEmpty(orderNo)){
                    Intent intent=new Intent(UpdateServiceActivity.this,PayActivity.class);
                    intent.putExtra("price",money);
                    intent.putExtra("orderNo",orderNo);
                    intent.putExtra("projectId","3000");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
