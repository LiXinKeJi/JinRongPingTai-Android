package com.finance.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.fragment.MasterFragment;
import com.finance.client.fragment.MineFragment;
import com.finance.client.fragment.MsgFragment;
import com.finance.client.receiver.ExampleUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout[] mLinearLayout;
    private TextView[] mTextView;
    private Fragment[] mFragments;
    private Fragment currentFragment = new Fragment();
    private Context context;
    private int current = 0;
    public static boolean isForeground = false;
    private long exitTime = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = this;
        initView();
        initFragment();
        refreshView();
        registerMessageReceiver();
        init();
    }

    private void initView() {
        mLinearLayout = new LinearLayout[3];
        mLinearLayout[0] = (LinearLayout) findViewById(R.id.iv_main_massage);
        mLinearLayout[1] = (LinearLayout) findViewById(R.id.iv_main_master);
        mLinearLayout[2] = (LinearLayout) findViewById(R.id.iv_main_mine);
        mTextView = new TextView[3];
        mTextView[0] = (TextView) findViewById(R.id.text_main_massage);
        mTextView[1] = (TextView) findViewById(R.id.text_main_master);
        mTextView[2] = (TextView) findViewById(R.id.text_main_mine);
    }

    private void initFragment() {
        mFragments = new Fragment[3];
        mFragments[0] = new MsgFragment();
        mFragments[1] = new MasterFragment();
        mFragments[2] = new MineFragment();
        setCurrent(0);
    }

    private void refreshView() {
        for (int i = 0; i < mLinearLayout.length; i++) {
            mLinearLayout[i].setId(i);
            mLinearLayout[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                setCurrent(0);
                break;
            case 1:
                setCurrent(1);
                break;
            case 2:
                setCurrent(2);
                break;
            case 3:
                setCurrent(3);
                break;
            default:
                break;
        }
    }

    private void setCurrent(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!mFragments[position].isAdded()) {
            transaction
                    .hide(currentFragment)
                    .add(R.id.activity_new_main_layout_content, mFragments[position]);
        } else {
            transaction
                    .hide(currentFragment)
                    .show(mFragments[position]);
        }
        currentFragment = mFragments[position];
        transaction.commit();

        mLinearLayout[position].setSelected(true);
        Resources resource = context.getResources();
        ColorStateList csl1 = resource.getColorStateList(R.color.colorBlack);
        ColorStateList csl2 = resource.getColorStateList(R.color.app_main_colour);
        for (int i = 0; i < mLinearLayout.length; i++) {
            if (i != position) {
                mLinearLayout[i].setSelected(false);
                mTextView[i].setTextColor(csl1);
            } else {
                mTextView[i].setTextColor(csl2);
            }
        }
        current = position;
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init() {
        JPushInterface.init(getApplicationContext());
    }


    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
//                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
