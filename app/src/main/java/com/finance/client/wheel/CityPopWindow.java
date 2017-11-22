package com.finance.client.wheel;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.finance.client.R;
import com.finance.client.wheel.adapter.ListWheelAdapter;
import com.finance.client.wheel.adapter.ListWheelAdapter2;
import com.finance.client.wheel.adapter.ListWheelAdapter3;
import com.finance.client.wheel.wheelview.OnWheelChangedListener;
import com.finance.client.wheel.wheelview.WheelView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * 轮子选择器
 * Created by Administrator on 2016/11/9 0009.
 */

public class CityPopWindow extends PopupWindow implements OnWheelChangedListener {

    private Context context;
    private WheelView wheel1;//省
    private WheelView wheel2;//市
    private WheelView wheel3;//区

    private String province, city, county;

    private List<CityBean> cityList = new ArrayList<>();
    private List<CityBean.citiesBean> citiesList = new ArrayList<>();
    private List<CityBean.countiesBean> counties = new ArrayList<>();

    public CityPopWindow(Context context) {
        // TODO Auto-generated constructor stub
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.wheel_region, null);

        this.context = context;

        this.setContentView(v);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setAnimationStyle(android.R.style.Animation_InputMethod);
        this.setFocusable(true);
//		this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        cityList = new Gson ().fromJson(getJsons(context),
                new TypeToken<List<CityBean>>() {
                }.getType());

        initPop(v, cityList);
    }


    //初始化Pop
    private void initPop(View viewGroup, List<CityBean> cityList) {
        wheel1 = (WheelView) viewGroup.findViewById(R.id.wheel1);
        wheel2 = (WheelView) viewGroup.findViewById(R.id.wheel2);
        wheel3 = (WheelView) viewGroup.findViewById(R.id.wheel3);

        TextView tv_enter = (TextView) viewGroup.findViewById(R.id.tv_enter);
        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcw.saveVycle(province, city, county);
            }
        });
        TextView tv_cancel = (TextView) viewGroup.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        province=cityList.get(0).areaName;
        wheel1.setViewAdapter(new ListWheelAdapter(context, cityList));
        wheel1.setCurrentItem(0);
        wheel1.setCyclic(false);// 可循环滚动
        wheel1.addChangingListener(this);

        citiesList = cityList.get(0).cities;
        city=citiesList.get(0).areaName;
        wheel2.setViewAdapter(new ListWheelAdapter2(context, citiesList));
        wheel2.setCurrentItem(0);
        wheel2.setCyclic(false);// 可循环滚动
        wheel2.addChangingListener(this);

        counties = cityList.get(0).cities.get(0).counties;
        county=counties.get(0).areaName;
        wheel3.setViewAdapter(new ListWheelAdapter3(context, counties));
        wheel3.setCurrentItem(0);
        wheel3.setCyclic(false);// 可循环滚动
        wheel3.addChangingListener(this);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

        if (wheel == wheel1) {
            citiesList = cityList.get(wheel.getCurrentItem()).cities;
            wheel2.setViewAdapter(new ListWheelAdapter2(context, citiesList));
            counties = citiesList.get(0).counties;
            wheel3.setViewAdapter(new ListWheelAdapter3(context, counties));

            wheel2.setCurrentItem(0);
            wheel3.setCurrentItem(0);

            province = cityList.get(wheel.getCurrentItem()).areaName;
            city = citiesList.get(0).areaName;
            county = counties.get(0).areaName;
        } else if (wheel == wheel2) {
            counties = citiesList.get(wheel2.getCurrentItem()).counties;
            wheel3.setViewAdapter(new ListWheelAdapter3(context, counties));

            wheel3.setCurrentItem(0);

            province = cityList.get(wheel.getCurrentItem()).areaName;
            city = citiesList.get(wheel2.getCurrentItem()).areaName;
            county = counties.get(0).areaName;
        } else if (wheel == wheel3) {
            province = cityList.get(wheel.getCurrentItem()).areaName;
            city = citiesList.get(wheel2.getCurrentItem()).areaName;

            county = counties.get(wheel3.getCurrentItem()).areaName;
        }

    }

    private PopInterface pcw;

    public void setOnCycleListener(PopInterface pcw) {
        this.pcw = pcw;
    }

    public interface PopInterface {
        void saveVycle(String province, String city, String county);
    }


    public static String getJsons(Context context) {
        String text = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.city);
            text = readTextFromSDcard(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }


    private static String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }


}
