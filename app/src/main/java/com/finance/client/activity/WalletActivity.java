package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.fragment.MineFragment;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/15
 */

public class WalletActivity extends BaseActivity {
    private String guCoin, amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "我的钱包";
        setContentView(R.layout.acitivyt_wallet_layout);
        super.onCreate(savedInstanceState);
        findViewById(R.id.RightBtnText).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.RightBtnText)).setText("零钱明细");
        findViewById(R.id.RightBtnText).setOnClickListener(this);
        findViewById(R.id.ChangeBtn).setOnClickListener(this);
        findViewById(R.id.UpdateBtn).setOnClickListener(this);

    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.RightBtnText) {
            Intent intent = new Intent(this, ChangeListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.ChangeBtn) {
            Intent intent = new Intent(WalletActivity.this, RechargeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.UpdateBtn) {
            Intent intent = new Intent(WalletActivity.this, UpdateServiceActivity.class);
            intent.putExtra("guCoin", guCoin);
            startActivity(intent);
        }
    }

    private void requestData() {
        Map<String, String> params = new HashMap<>();
        final String json = "{\"cmd\":\"getUserAmount\",\"uid\":\"" + UserUtil.uid + "\"}";
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(WalletActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(WalletActivity.this, obj.getString("resultNote"));
                        return;
                    }
                    amount = obj.getString("amount");
                    MineFragment.userInfo.setAmount(obj.getString("amount"));
                    guCoin = obj.getString("guCoin");
                    updateView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateView() {
        if (amount.equals("0")) {
            ((TextView) findViewById(R.id.Money)).setText("0.00");
        } else {
            ((TextView) findViewById(R.id.Money)).setText("" + amount);
        }
    }
}
