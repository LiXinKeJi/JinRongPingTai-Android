package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.fragment.MineFragment;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import org.json.JSONObject;

import java.util.Map;

/**
 * User : yh
 * Date : 17/8/15
 */

public class WalletActivity extends BaseActivity {
    private String guCoin,amount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "我的钱包";
        setContentView(R.layout.acitivyt_wallet_layout);
        super.onCreate(savedInstanceState);
        findViewById(R.id.RightBtnText).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.RightBtnText)).setText("零钱明细");
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
        if(v.getId() == R.id.RightBtnText){
            Intent intent = new Intent(WalletActivity.this,ChangeListActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.ChangeBtn)
        {
            Intent intent = new Intent(WalletActivity.this,RechargeActivity.class);
            startActivity(intent);
        }else if (v.getId()==R.id.UpdateBtn)
        {
            Intent intent = new Intent(WalletActivity.this,UpdateServiceActivity.class);
            intent.putExtra("guCoin",guCoin);
            startActivity(intent);
        }
    }

    private void requestData(){
        Map<String,String> params = Maps.newHashMap();
        params.put("uid", UserUtil.uid);
        params.put("cmd","getUserAmount");
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
                                Toast.makeText(WalletActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            amount=obj.getString("amount");
                            MineFragment.userInfo.setAmount(obj.getString("amount"));
                            guCoin=obj.getString("guCoin");
                            updateView();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void updateView(){
        if (amount.equals("0"))
        {
            ((TextView) findViewById(R.id.Money)).setText("0.00");
        }else {
            ((TextView) findViewById(R.id.Money)).setText("" + amount);
        }

    }
}
