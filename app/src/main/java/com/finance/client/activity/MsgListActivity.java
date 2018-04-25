package com.finance.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.adapter.MsgAdapter;
import com.finance.client.model.MsgInfoDao;
import com.finance.client.model.MsgInfoListDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.dialog.YHAlertDialog;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgListActivity extends BaseActivity{
    private PullToRefreshListView mListView;
    private MsgAdapter mAdapter;
    private List<MsgInfoDao> msgList = new ArrayList<>();
    private String categoryId,type;
    private ImageView delectIcon;
    private TextView cancelBtn;
    private int nowPage = 1;
    private int totalPage = 1;
    private boolean delete = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = getIntent().getStringExtra("title");
        setContentView(R.layout.activity_msg_list);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        delectIcon = (ImageView) findViewById(R.id.RightBtnImg);
        cancelBtn = (TextView) findViewById(R.id.RightBtnText);
        delectIcon.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        cancelBtn.setText("取消");
        delectIcon.setImageResource(R.drawable.delete_icon);
        delectIcon.setVisibility(View.VISIBLE);
        categoryId = getIntent().getStringExtra("categoryId");
        type = getIntent().getStringExtra("type");
        mListView = (PullToRefreshListView) findViewById(R.id.ListView);
        mAdapter = new MsgAdapter(this,msgList,type);
        mListView.setAdapter(mAdapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                msgList.clear();
                nowPage = 1;
                mAdapter.notifyDataSetChanged();
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                nowPage += 1;
                requestData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        msgList.clear();
        nowPage = 1;
        mAdapter.notifyDataSetChanged();
        requestData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.RightBtnText){
            delete = false;
            mAdapter.setShowDeleteIcon(delete);
            delectIcon.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
        }else if(v.getId() == R.id.RightBtnImg){
            delete = true;
            mAdapter.setShowDeleteIcon(delete);
            delectIcon.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    private void requestData(){
        if (nowPage > totalPage) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                }
            });
            return;
        }
        showLoading();
        Map<String,String> params = new HashMap<>();
        String json = "{\"cmd\":\"getMessagesDetail\",\"uid\":\""+ UserUtil.uid+"\"" +
                ",\"categoryID\":\""+categoryId+"\",\"pageCount\":\""+10+"\",\"nowPage\":\""+nowPage+"\"" +
                ",\"type\":\""+type+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(MsgListActivity.this,e.getMessage());
                mListView.onRefreshComplete();
            }
            @Override
            public void onResponse(String response, int id) {
                Log.i("response", "onResponse: " + response);
                Gson gson = new Gson();
                dismissLoading();
                MsgInfoListDao msgInfoListDao = gson.fromJson(response,MsgInfoListDao.class);
                if (msgInfoListDao.getResult().equals("1")){
                    ToastUtils.makeText(MsgListActivity.this,msgInfoListDao.getResultNote());
                    mListView.onRefreshComplete();
                    return;
                }
                List<MsgInfoDao> msgInfoDaos = msgInfoListDao.getDataList();
                totalPage = Integer.parseInt(msgInfoListDao.getTotalPage());
                msgList.addAll(msgInfoDaos);
                if(msgList.size() == 0){
                    ToastUtils.makeText(MsgListActivity.this, "暂无消息");
                }
                mListView.onRefreshComplete();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void deleteMsg(final int index){
        YHAlertDialog dialog = new YHAlertDialog.Builder(this)
                .setCancel("取消")
                .setConfirm("删除")
                .setMessage("确定要删除吗？删除之后不可恢复")
                .setTitle("提示")
                .create();
        dialog.setOnDialogClickListener(new YHAlertDialog.OnDialogClickListener() {
            @Override
            public void onConfirm() {
                delete(index);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void delete(final int index){
        showLoading();
        MsgInfoDao item = msgList.get(index);
        Map<String,String> params = Maps.newHashMap();
        final String json = "{\"cmd\":\"deleteMessageDetail\",\"uid\":\""+UserUtil.uid+"\",\"messageID\":\""+item.getMessageId()+"\",\"type\":\""+type+"\"}";
        params.put("json",json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
           @Override
           public void onError(Call call, Exception e, int id) {
               dismissLoading();
               ToastUtils.makeText(MsgListActivity.this, e.getMessage());
           }

           @Override
           public void onResponse(String response, int id) {
               dismissLoading();
               try {
                   JSONObject jsonObject=new JSONObject(response);
                   if (jsonObject.getString("result").equals("1")) {
                       ToastUtils.makeText(MsgListActivity.this, jsonObject.getString("resultNote"));
                       return;
                   }
                   ToastUtils.makeText(MsgListActivity.this, "消息详情已删除");
                   msgList.remove(index);
                   mAdapter.notifyDataSetChanged();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });
    }
}
