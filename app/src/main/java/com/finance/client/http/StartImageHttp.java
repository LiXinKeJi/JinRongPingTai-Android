package com.finance.client.http;

import android.util.Log;

import com.finance.client.model.StartImageModel;
import com.finance.client.util.Content;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 启动页、引导页图片
 * Created by Slingge on 2018/6/23 0023.
 */

public class StartImageHttp {

    public interface ImageCallBack {
        void image(StartImageModel model);
    }


    public static void getImage(  final ImageCallBack imageCallBack) {
        String json = "{\"cmd\":\"getimage" + "\"}";
        Log.e("获取驱动图",json);
        OkHttpUtils.post().url(Content.DOMAIN).addParams("json", json).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                imageCallBack.image(null);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("启动图.......",response);
                StartImageModel model = new Gson().fromJson(response, StartImageModel.class);
                if (model.result.equals("0")) {
                    imageCallBack.image(model);
                } else {
                    imageCallBack.image(null);
                }
            }
        });
    }


}
