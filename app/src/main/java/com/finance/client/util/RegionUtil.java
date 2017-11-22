package com.finance.client.util;

import android.content.Context;

import org.json.JSONArray;

public class RegionUtil {
    public static JSONArray getRegionData(Context context){

        String str = FileUtils.readFromAssert(context.getAssets(),"all_city.json");
        try{
            return new JSONArray(str);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
