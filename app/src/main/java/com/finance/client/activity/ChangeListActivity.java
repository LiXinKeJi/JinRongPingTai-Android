package com.finance.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.ChangeAdapter;
import com.finance.client.model.ChangeListResultDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/15
 */

public class ChangeListActivity extends BaseActivity {
    private List<ChangeListResultDao.ChangeInfoDao> mlists = Lists.newArrayList();
    private PullToRefreshListView mListView;
    private ChangeAdapter mAdapter;
    private int nowPage = 1;
    private int totalPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "零钱明细";
        setContentView(R.layout.change_activity);
        super.onCreate(savedInstanceState);
        mListView = (PullToRefreshListView) findViewById(R.id.ListView);
        mAdapter = new ChangeAdapter(this, mlists);
        mListView.setAdapter(mAdapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                mlists.clear();
                nowPage = 1;
                mAdapter.notifyDataSetChanged();
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                nowPage += 1;
                if (nowPage > totalPage) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    });
                } else {
                    requestData();
                }
            }
        });
        this.requestData();
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    private void requestData() {
        Map<String, String> params = new HashMap<>();
        final String json = "{\"cmd\":\"getTransactions\",\"uid\":\""+ UserUtil.uid+"\",\"nowPage\":\""+nowPage+"\",\"pageCount\":\""+10+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(ChangeListActivity.this,e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                dismissLoading();
                ChangeListResultDao changeListResultDao = gson.fromJson(response,ChangeListResultDao.class);
                if (changeListResultDao.getResult().equals("1")){
                    ToastUtils.makeText(ChangeListActivity.this, changeListResultDao.getResultNote());
                    return;
                }
                totalPage = changeListResultDao.getTotalPage();
                List<ChangeListResultDao.ChangeInfoDao> changeInfoDaos = changeListResultDao.getDataList();
                if (changeInfoDaos != null && !changeInfoDaos.isEmpty() && changeInfoDaos.size()>0){
                    mlists.addAll(changeInfoDaos);
                    mAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(ChangeListActivity.this, "暂无消息", Toast.LENGTH_SHORT).show();
                }
                mListView.onRefreshComplete();
            }
        });
    }
}
