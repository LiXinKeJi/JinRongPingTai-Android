package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.finance.client.R;

/**
 * User : yh
 * Date : 17/8/15
 */

public class SignActivity extends BaseActivity {

    private String strSign;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "个性签名";
        setContentView(R.layout.activity_sign);
        super.onCreate(savedInstanceState);
        String sign=getIntent().getStringExtra("sign");
        findViewById(R.id.RightBtnText).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.RightBtnText)).setText("完成");
        findViewById(R.id.RightBtnText).setOnClickListener(this);
        ((EditText)findViewById(R.id.edit_sign)).setText(sign);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId()== R.id.RightBtnText)
        {
            strSign=((EditText)findViewById(R.id.edit_sign)).getText().toString();
            Intent intent=new Intent();
            intent.putExtra("sign",strSign);
            setResult(1002,intent);
            finish();
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
