package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.finance.client.R;

/**
 * User : yh
 * Date : 17/9/5
 */

public class ChongzhiChenggongActivity extends BaseActivity {
    private String amount;
    private TextView txtAmount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        title = "充值";
        setContentView(R.layout.activity_chongzhi_chenggong);
        super.onCreate(savedInstanceState);
//        ((TextView)findViewById(R.id.Content)).setText(content);
        amount=getIntent().getStringExtra("amount");
        txtAmount=(TextView)findViewById(R.id.txtamount);
        txtAmount.setText("￥"+amount);
        findViewById(R.id.FinishLayout).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId()== R.id.FinishLayout)
        {
            finish();
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
