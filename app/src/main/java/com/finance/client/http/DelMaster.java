package com.finance.client.http;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.yhrun.alchemy.Common.LoadingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 删除关注的人
 * Created by Slingge on 2018/6/27 0027.
 */

public class DelMaster {

    private static LoadingDialog dialog;

    public interface DelMasterCallBack {
        void del(String id);
    }


    public static void delMaster(final Context context, final String masterId, final DelMasterCallBack delMasterCallBack) {
        showLoading(context);
        String json = "{\"cmd\":\"cancelattention\",\"uid\":\"" + UserUtil.getUid(context) + "\",\"merchantId\":\"" + masterId + "\"}";
        OkHttpUtils.post().url(Content.DOMAIN).addParams("json", json).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showMessageShort(context, "网络错误");
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("0")) {
                        delMasterCallBack.del(masterId);
                    } else {
                        ToastUtils.showMessageShort(context, obj.getString("resultNote"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.e("取消关注", response);
            }
        });

    }


    public static void showLoading(Context context) {
        if (dialog == null) {
            dialog = new LoadingDialog(context);
        }
        dialog.show();
    }

    public static void dismissLoading() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


}
