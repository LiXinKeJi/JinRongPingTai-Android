package com.finance.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.fragment.BaseFragment;
import com.finance.client.fragment.MasterFragment;
import com.finance.client.fragment.MineFragment;
import com.finance.client.fragment.MsgFragment;
import com.finance.client.receiver.ExampleUtil;
import cn.jpush.android.api.JPushInterface;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private TextView HomeBtn ,OrderBtn,MineBtn;
    private int index = -1;
    private int[] selectIcons = {R.drawable.dibu02,R.drawable.dibu04,R.drawable.dibu06};
    private int[] icons = {R.drawable.dibu01,R.drawable.dibu03,R.drawable.dibu05};

    public static boolean isForeground = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        HomeBtn = (TextView) findViewById(R.id.msgText);
        OrderBtn = (TextView)findViewById(R.id.orderText);
        MineBtn = (TextView)findViewById(R.id.mineText);
        findViewById(R.id.msgBtn).setOnClickListener(this);
        findViewById(R.id.orderBtn).setOnClickListener(this);
        findViewById(R.id.mineBtn).setOnClickListener(this);
        changeTab(0);
        registerMessageReceiver();
        init();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.msgBtn){
            changeTab(0);
        }else if(v.getId() == R.id.orderBtn){
            changeTab(1);
        }else if(v.getId() == R.id.mineBtn){
            changeTab(2);
        }
    }

    private void changeTab(int _index){
        if(index == _index){
            return;
        }
        index = _index;
        if(index == 0) {
            FragmentManager fm = getSupportFragmentManager();
            BaseFragment fragment = new MsgFragment();
            fm.beginTransaction().replace(R.id.fragment, fragment).commit();
            drawableTop(HomeBtn,R.drawable.dibu02,"#000000");
            drawableTop(OrderBtn,R.drawable.dibu03,"#999999");
            drawableTop(MineBtn,R.drawable.dibu05,"#999999");
        }else if(index == 1){
            FragmentManager fm = getSupportFragmentManager();
            BaseFragment fragment = new MasterFragment();
            fm.beginTransaction().replace(R.id.fragment, fragment).commit();
            drawableTop(HomeBtn,R.drawable.dibu01,"#999999");
            drawableTop(OrderBtn,R.drawable.dibu04,"#000000");
            drawableTop(MineBtn,R.drawable.dibu05,"#999999");
        }else if(index == 2){
            FragmentManager fm = getSupportFragmentManager();
            BaseFragment fragment = new MineFragment();
            fm.beginTransaction().replace(R.id.fragment, fragment).commit();
            drawableTop(HomeBtn,R.drawable.dibu01,"#999999");
            drawableTop(OrderBtn,R.drawable.dibu03,"#999999");
            drawableTop(MineBtn,R.drawable.dibu06,"#000000");
        }
    }

    private void drawableTop(TextView textView,int icon,String color){
        textView.setTextColor(Color.parseColor(color));
        Drawable drawable= getResources().getDrawable(icon);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,drawable,null,null);
    }



    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init(){
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
            } catch (Exception e){
            }
        }


    }
}
