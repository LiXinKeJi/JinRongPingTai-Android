package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.finance.client.MyApplication;
import com.finance.client.R;
import com.finance.client.adapter.MyFragmentPagerAdapter;
import com.finance.client.fragment.WelComeFragment;
import com.finance.client.http.StartImageHttp;
import com.finance.client.model.StartImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Slingge on 2018/6/23 0023.
 */

public class WelComeActivity extends FragmentActivity implements StartImageHttp.ImageCallBack {

    private ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }


    private void init() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        StartImageModel model = (StartImageModel) getIntent().getSerializableExtra("model");
        if (model == null) {
            StartImageHttp.getImage( this);
            return;
        }

        initFramgent(model.imgList);
    }


    private void initFramgent(List<StartImageModel.imgBean> list) {
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            WelComeFragment f1 = new WelComeFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", list.get(i));
            if (i == list.size() - 1) {//是最后一个
                bundle.putInt("flag", 0);
            }
            f1.setArguments(bundle);
            fragmentList.add(f1);
        }

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
    }



    @Override
    public void image(StartImageModel model) {
        if (model == null) {
            MyApplication.openActivity(WelComeActivity.this, LoginActivity.class);
        } else {
            initFramgent(model.imgList);
        }
    }


}
