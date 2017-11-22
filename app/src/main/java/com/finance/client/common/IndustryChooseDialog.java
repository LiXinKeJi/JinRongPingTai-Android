package com.finance.client.common;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.ArrayWheelAdapter;
import com.finance.client.util.RegionUtil;
import com.finance.client.wheel.adapter.ListWheelAdapter4;
import com.finance.client.wheel.wheelview.WheelView;
import com.google.common.collect.Lists;
import com.finance.client.wheel.wheelview.OnWheelChangedListener;
import com.yhrun.alchemy.View.wheel.WheelAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * User : yh
 * Date : 17/9/5
 */

public class IndustryChooseDialog extends Dialog implements View.OnClickListener{
    private Context context;

    private int first_index =0;
    private int second_index =0;
    private int third_index = 0;
    private int fourth_index = 0;
    private WheelView firstView,secondView,thirdView,fourthView;
    private WheelAdapter firstAdapter,secondAdapter,thirdAdapter;
    private List<String> first = Lists.newArrayList();
    private List<String> second = Lists.newArrayList();
    private List<String> third = Lists.newArrayList();
    private List<String> fourth = Lists.newArrayList();

    private JSONArray cityData;
    private String cityId;
    private JSONArray third_array;

    private onCategoryChangeListener listener;
    private String info = "";
    private String id = "";

    public IndustryChooseDialog(@NonNull Context context,JSONArray data) {
        super(context, R.style.RegionDialogTheme);
        this.cityData = data;
        this.context=context;
        Init();
    }

    public  void setData(JSONArray data){
        this.cityData = data;
    }

    public void setOnCategoryChangeListener(onCategoryChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cancelBtn){
            dismiss();
        }else if(v.getId() == R.id.confirmBtn){
            //String info = first.get(first_index)+" "+second.get(second_index)+" "+third.get(third_index);
            try {
                info = "";
                JSONObject obj_a = cityData.getJSONObject(first_index);
                JSONObject obj_b = obj_a.getJSONArray("slist").getJSONObject(second_index);
                JSONObject obj_c = obj_b.getJSONArray("tlist").getJSONObject(third_index);
                JSONObject obj_d = obj_c.getJSONArray("fflist").getJSONObject(fourth_index);
                //info = obj_a.getString("name")+"-"+obj_b.getString("name")+"-"+obj_c.getString("name")+"-"+obj_d.getString("name");
                info = obj_d.getString("name");
                id = obj_d.getString("industryId");
            }catch (Exception e){

            }
            if(listener != null){
                listener.onChange(info,id);
            }
            dismiss();
        }
    }

    public interface onCategoryChangeListener{
        public void onChange(String info,String id);
    }

    private void Init(){
        setContentView(R.layout.industry_choose_layout);
        firstView = (WheelView) findViewById(R.id.WheelView1);
        secondView = (WheelView) findViewById(R.id.WheelView2);
        thirdView = (WheelView)findViewById(R.id.WheelView3);
        fourthView = (WheelView)findViewById(R.id.WheelView4);
        firstView.addChangingListener(listener1);
        secondView.addChangingListener(listener2);
        thirdView.addChangingListener(listener3);
        fourthView.addChangingListener(listener4);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.confirmBtn).setOnClickListener(this);
        setCanceledOnTouchOutside(true);
        First();
    }

    private void First(){
        first.clear();
        try {
            for (int index = 0; index < cityData.length(); ++index) {
                JSONObject obj = cityData.getJSONObject(index);
                first.add(obj.getString("name"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        firstView.setViewAdapter(new ListWheelAdapter4(context,first));
        Second();
    }
    private void Second(){
        second.clear();
        try {
            JSONObject data = cityData.getJSONObject(first_index);
            JSONArray array = data.getJSONArray("slist");
            for (int index = 0; index < array.length(); ++index) {
                JSONObject obj = array.getJSONObject(index);
                second.add(obj.getString("name"));
            }
            secondView.setViewAdapter(new ListWheelAdapter4(context,second));
            Third();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void Third(){
        third.clear();
        try {
            JSONArray array = cityData.getJSONObject(first_index).getJSONArray("slist").getJSONObject(second_index).getJSONArray("tlist");
            for (int index = 0; index < array.length(); ++index) {
                JSONObject obj = array.getJSONObject(index);
                third.add(obj.getString("name"));
            }
            thirdView.setViewAdapter(new ListWheelAdapter4(context,third));
            Fourth();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Fourth(){
        fourth.clear();
        try {
            JSONArray array = cityData.getJSONObject(first_index).getJSONArray("slist").getJSONObject(second_index).getJSONArray("tlist").getJSONObject(third_index).getJSONArray("fflist");
            for (int index = 0; index < array.length(); ++index) {
                JSONObject obj = array.getJSONObject(index);
                fourth.add(obj.getString("name"));
            }
            fourthView.setViewAdapter(new ListWheelAdapter4(context,fourth));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        super.show();
    }

    private OnWheelChangedListener listener1 = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheelView, int i, int newValue) {
            first_index = newValue;
            second_index = 0;
            third_index = 0;
            Second();
        }
    };

    private OnWheelChangedListener listener2 = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheelView, int i, int newValue) {
            second_index = newValue;
            third_index = 0;
            Third();
        }
    };

    private OnWheelChangedListener listener3 = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheelView, int i, int newValue) {
            third_index = newValue;
            fourth_index = 0;
            Fourth();
        }
    };

    private OnWheelChangedListener listener4 = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheelView, int i, int newValue) {
            fourth_index = newValue;
            try {
                info = "";
                JSONObject obj_a = cityData.getJSONObject(first_index);
                JSONObject obj_b = obj_a.getJSONArray("slist").getJSONObject(second_index);
                JSONObject obj_c = obj_b.getJSONArray("tlist").getJSONObject(third_index);
                JSONObject obj_d = obj_c.getJSONArray("ttlist").getJSONObject(fourth_index);
                //info = obj_a.getString("name")+"-"+obj_b.getString("name")+"-"+obj_c.getString("name")+"-"+obj_d.getString("name");
                info = obj_d.getString("name");
                id = obj_d.getString("industryId");
            }catch (Exception e){

            }
        }
    };

}
