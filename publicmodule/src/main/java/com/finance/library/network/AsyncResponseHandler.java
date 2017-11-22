package com.finance.library.network;

import com.alibaba.fastjson.JSON;
import com.finance.library.model.BaseResultDO;
import com.google.common.base.Strings;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yhrun.alchemy.Util.FastJsonUtil;

import org.apache.http.Header;

/**
 * User : yh
 * Date : 16/12/6
 */

public abstract class AsyncResponseHandler<T> extends AsyncHttpResponseHandler{
    private Class returnClass;

    public void setReturnClass(Class cls){
        this.returnClass = cls;
    }

    public abstract void onResult(boolean success, T result, ResponseError error);

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String resultData = new String(responseBody);
        if(statusCode != 200 || Strings.isNullOrEmpty(resultData)){
            onResult(false,null,new ResponseError("request.error","请求错误"));
            return;
        }
        if(returnClass == String.class){
            onResult(true,(T)resultData,null);
            return;
        }
        BaseResultDO brd = null;
        try{
            brd = (BaseResultDO) FastJsonUtil.parseObject(resultData,BaseResultDO.class);
        }catch (Exception e){
            //
        }
        if(brd == null || Strings.isNullOrEmpty(brd.getResult())){
            onResult(false,null,new ResponseError("request.error","请求错误"));
            return;
        }
        if(brd.getResult().equals("1")){
            onResult(false,null,new ResponseError("request.error",brd.getResultNote()));
            return;
        }
        Object obj = resultData;
        if(returnClass != null){
            try{
                //obj = FastJsonUtil.parseObject(resultData,returnClass);
                obj = JSON.parseObject(resultData,returnClass);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        onResult(true,(T)obj,null);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onResult(false,null,new ResponseError("request.error","请求错误"));
    }

    public class ResponseError{
        public String errorMsg;
        public String errorCode;
        public ResponseError(String code,String msg){
            this.errorCode = code;
            this.errorMsg = msg;
        }
    }
}
