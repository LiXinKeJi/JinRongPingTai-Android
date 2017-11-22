package com.finance.library.Util;

import com.google.common.base.Strings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Random;

/**
 * User : yh
 * Date : 16/12/6
 */

public class SmsUtil {
    public static void SendSms(String phone, final SendSmsCallHandler handler){
        final String code = getCode();
        String tpl = "#code#="+code;
        String url = "http://v.juhe.cn/sms/send";
        RequestParams param = new RequestParams();
        param.put("mobile",phone);
        param.put("tpl_id","42425");
        //param.put("tpl_id","24921");
        param.put("key","d4cd3d885697fcc221c6fa172015a440");
        //param.put("key","190ab8206f6efa2b35fbcbbf5ea1f957");
        param.put("tpl_value",tpl);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url,param ,new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(Strings.isNullOrEmpty(rawJsonResponse)){
                    handler.onResult(false,null);
                    return;
                }
                try{
                    JSONObject obj = new JSONObject(rawJsonResponse);
                    if(obj.has("error_code") && obj.getString("error_code").equals("0")){
                        handler.onResult(true,code);
                    }else{
                        if(obj.has("reason")) {
                            handler.onResult(false, obj.getString("reason"));
                        }else{
                            handler.onResult(false,"发送失败，请稍后重试");
                        }
                    }
                }catch (Exception e){
                    handler.onResult(false,"发送失败，请稍后重试");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                handler.onResult(false,"发送失败，请稍后重试");
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private static String getCode(){
        Random random = new Random();
        String str = "";
        for(int i = 0 ;i < 6 ; ++i){
            str += random.nextInt(9);
        }
        return str;
    }

    public interface SendSmsCallHandler{
        public void onResult(boolean success, String code);
    }
}
