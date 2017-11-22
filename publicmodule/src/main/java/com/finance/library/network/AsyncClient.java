package com.finance.library.network;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.yhrun.alchemy.Util.FastJsonUtil;

/**
 * User : yh
 * Date : 16/12/6
 */

public class AsyncClient {
    private Class returnClass;
    private Object params;
    private AsyncHttpClient client;
    private Context mContext;
    private String httpMethod;
    private String host;

    public static AsyncClient Get(){
        return new AsyncClient("get");
    }

    private AsyncClient(String method){
        this.httpMethod = method;
    }

    public static AsyncClient Post(){
        return new AsyncClient("post");
    }

    public AsyncClient setContext(Context mContext){
        this.mContext = mContext;
        return this;
    }

    public AsyncClient setReturnClass(Class cls){
        this.returnClass = cls;
        return this;
    }

    public AsyncClient setHost(String host){
        this.host = host;
        return this;
    }

    public AsyncClient setParams(Object pms){
        this.params = pms;
        return this;
    }

    public void execute(AsyncResponseHandler handler){
        if(client == null){
            client = new AsyncHttpClient();
        }
        handler.setReturnClass(returnClass);
        if(httpMethod.equals("get")){
            RequestParams rp = new RequestParams();
            rp.put("json", FastJsonUtil.toJsonString(params));
            client.get(this.host,rp,handler);
        }else if(httpMethod.equals("post")){
            RequestParams rp = new RequestParams();
            rp.put("json", FastJsonUtil.toJsonString(params));
            client.post(this.host,rp,handler);
        }
    }
}
