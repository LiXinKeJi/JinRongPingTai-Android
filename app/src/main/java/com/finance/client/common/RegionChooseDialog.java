package com.finance.client.common;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
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


public class RegionChooseDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private int first_index =0;
    private int second_index =0;
    private int third_index = 0;
    private WheelView firstView,secondView,thirdView;
    private WheelAdapter firstAdapter,secondAdapter,thirdAdapter;
    private List<String> first = Lists.newArrayList();
    private List<String> second = Lists.newArrayList();
    private List<String> third = Lists.newArrayList();

    private JSONArray cityData;
    private String cityId;
    private JSONArray third_array;
    private onRegionChangeListener listener;
    private String address;

    public RegionChooseDialog(Context context) {
        super(context, R.style.RegionDialogTheme);
        this.context=context;
        Init();
    }

    public void setOnRegionChangeListener(onRegionChangeListener listener){
        this.listener = listener;
    }

    private void Init(){
        setContentView(R.layout.dialog_choose_region_layout);
        firstView = (WheelView) findViewById(R.id.WheelView1);
        secondView = (WheelView) findViewById(R.id.WheelView2);
        thirdView = (WheelView)findViewById(R.id.WheelView3);
        firstView.addChangingListener(listener1);
        secondView.addChangingListener(listener2);
        thirdView.addChangingListener(listener3);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.confirmBtn).setOnClickListener(this);
        setCanceledOnTouchOutside(true);
        cityData = RegionUtil.getRegionData(getContext());
        if(cityData == null){
            Toast.makeText(getContext(),"数据获取失败",Toast.LENGTH_LONG).show();
            dismiss();
        }
        First();
        Second();
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
    }
    private void Second(){
        second.clear();
        try {
            JSONObject data = cityData.getJSONObject(first_index);
            JSONArray array = data.getJSONArray("child");
            for (int index = 0; index < array.length(); ++index) {
                JSONObject obj = array.getJSONObject(index);
                second.add(obj.getString("name"));
            }
            secondView.setViewAdapter(new ListWheelAdapter4(context,second));
            if(array.length() >0 && array.length() > second_index){
                Third(array.getJSONObject(second_index).getJSONArray("child"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void Third(JSONArray array){
        third_array = array;
        third.clear();
        try {
            for (int index = 0; index < array.length(); ++index) {
                JSONObject obj = array.getJSONObject(index);
                third.add(obj.getString("name"));
                if(index == third_index){
                    cityId = obj.getString("id");
                }
            }
            thirdView.setViewAdapter(new ListWheelAdapter4(context,third));
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
            try{
                JSONArray array = cityData.getJSONObject(first_index).getJSONArray("child").getJSONObject(second_index).getJSONArray("child");
                Third(array);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private OnWheelChangedListener listener3 = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheelView, int i, int newValue) {
            third_index = newValue;
//            try{
//                cityId = third_array.getJSONObject(third_index).getString("id");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            try {
                //address = cityData.getJSONObject(first_index).getString("name");
                JSONObject obj_a = cityData.getJSONObject(first_index);
                JSONObject obj_b = obj_a.getJSONArray("child").getJSONObject(second_index);
                JSONObject obj_c = obj_b.getJSONArray("child").getJSONObject(third_index);
                address = obj_a.getString("name")+obj_b.getString("name") + obj_c.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public interface onRegionChangeListener{
        public void onChange(String info, String cityId);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cancelBtn){
            dismiss();
        }else if(v.getId() == R.id.confirmBtn){
            String info = first.get(first_index)+" "+second.get(second_index)+" "+third.get(third_index);
            if(listener != null){
                listener.onChange(info,"");
            }
            dismiss();
        }
    }
}
