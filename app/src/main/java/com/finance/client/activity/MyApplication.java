package com.finance.client.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.finance.client.util.CrashHandler;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Slingge on 2017/1/24 0024.
 */

public class MyApplication extends MultiDexApplication {

    public static String uId = "";
    public static boolean isLogin = false;
    public static Context CONTEXT;
    public static int temp = 0;
    private static MyApplication myApplication;
    public static MyApplication getInstance() {
        if (myApplication == null) {
            synchronized (MyApplication.class) {
                if (myApplication == null) myApplication = new MyApplication();
            }
        }
        return myApplication;
    }


  //  String json = "{\"cmd\":\"upPrize\",\"prizeId\":\"" + prizeId  + "\",\"userNme\":\"" + MyApplication.getUserName() + "\"}";


    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();
        myApplication = this;
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wxf33600c85feaf21b", "32f799126ed76ccfa08aa6005b4fb816");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //崩溃错误日志写入本地文档
        CrashHandler catchExcep = new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }


    /**
     * 检查是否已经登录 true 已登录
     *
     * @return
     */
    public static boolean isLogined() {
        return !(uId.equals("")) && isLogin;
    }

    /**
     * 是否登陆提示
     */
    public static boolean isLoginToa() {
        boolean b = !(uId.equals("")) && isLogin;
        if (b) {
            return true;
        } else {

            return false;
        }
    }


    /**
     * 通过类名启动Activity
     *
     * @param targetClass
     */
    public static void openActivity(Context context, Class<?> targetClass) {
        openActivity(context, targetClass, null);
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param targetClass
     * @param extras
     */
    public static void openActivity(Context context, Class<?> targetClass,
                                    Bundle extras) {
        Intent intent = new Intent(context, targetClass);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    public static void openActivityForResult(Activity activity,
                                             Class<?> targetClass, Bundle extras, int requestCode) {
        Intent intent = new Intent(activity, targetClass);
        if (extras != null) {
            intent.putExtras(extras);
        }
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * Fragment中无效
     */
    public static void openActivityForResult(Activity activity,
                                             Class<?> targetClass, int requestCode) {
        openActivityForResult(activity, targetClass, null, requestCode);
    }

    public static Context getContext() {
        return CONTEXT;
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
