package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.library.BaseActivity;

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
            ((TextView)findViewById(R.id.txt_guCoin)).setText("您当前累计消费0.0元，当前累计积分0.0股币");
        }
        else {
            ((TextView) findViewById(R.id.txt_guCoin)).setText("您当前累计消费" + guCoin + "元，当前累计积分" + guCoin + "股币");
        }


    }



    private void updateView()
    {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId()== R.id.txt_pay3) {
            if (TextUtils.isEmpty(edit_crowdfunding.getText().toString()))
            {
                Toast.makeText(this,"请输入众筹金额",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(UpdateServiceActivity.this,PayActivity.class);
            intent.putExtra("price",edit_crowdfunding.getText().toString());
            intent.putExtra("id","3000");
            startActivity(intent);
            finish();
        }
    }



    @Override
    public void onLeftIconClick() {
        finish();
    }
}
