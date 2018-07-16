package com.finance.client.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.util.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User : yh
 * Date : 17/8/15
 */

public class SignActivity extends BaseActivity implements TextWatcher {

    private String strSign;
    private TextView tv_num;
    private EditText edit_sign;

    List<String> numList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "个性签名";
        setContentView(R.layout.activity_sign);
        super.onCreate(savedInstanceState);
        String sign = getIntent().getStringExtra("sign");
        findViewById(R.id.RightBtnText).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.RightBtnText)).setText("完成");
        findViewById(R.id.RightBtnText).setOnClickListener(this);
        edit_sign = ((EditText) findViewById(R.id.edit_sign));
        edit_sign.addTextChangedListener(this);
        edit_sign.setText(sign);

        tv_num = (TextView) findViewById(R.id.tv_num);

        for (int i = 0; i < 10; i++) {
            numList.add(i + "");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.RightBtnText) {
            strSign = ((EditText) findViewById(R.id.edit_sign)).getText().toString();
            Intent intent = new Intent();
            intent.putExtra("sign", strSign);
            setResult(1002, intent);
            finish();
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String ss = s.toString();
        if (ss.isEmpty()) {
            return;
        }
        Log.e("位置........", start + "::" + before);
        if (numList.contains(ss.substring(start, start + count))) {
            Message m = new Message();
            m.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("int", ss.replace(ss.substring(start, start + count),""));
            m.setData(bundle);
            handler.sendMessage(m);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (str.isEmpty()) {
            return;
        }

        Message m = new Message();
        m.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("num", str);
        m.setData(bundle);
        handler.sendMessage(m);
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                tv_num.setText(msg.getData().getString("num").length() + "/30");
            } else if (msg.what == 1) {
                edit_sign.setText(msg.getData().getString("int"));
                tv_num.setText(edit_sign.getText().toString().length() + "/30");
                edit_sign.setSelection(edit_sign.length());
            }
        }
    };

}
