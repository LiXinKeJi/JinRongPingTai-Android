package com.finance.library.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User : yh
 * Date : 17/8/14
 */

public class UserUtil {
    public static String uid = "";

    public static void saveUid(Context mContext, String _uid){
        uid = _uid;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.finance.library",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("uid",_uid).commit();
    }

    public static String getUid(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.finance.library",Context.MODE_PRIVATE);
        String _uid = sharedPreferences.getString("uid",null);
        uid = _uid;
        return _uid;
        //return sharedPreferences.getString("uid",null);
    }

    public static void logout(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.finance.library",Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("uid").commit();
    }
}
