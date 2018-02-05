package com.finance.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.ChangeAdapter;
import com.finance.client.model.ChangeInfoDao;
import com.finance.client.model.ChangeListResultDao;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;

import java.util.List;
import java.util.Map;

/**
 * User : yh
 * Date : 17/8/15
 */

public class ChangeListActivity extends BaseActivity {
    private List<ChangeInfoDao> mlists = Lists.newArrayList();
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
        showLoading();
        Map<String, String> params = Maps.newHashMap();
        params.put("cmd", "getTransactions");
        params.put("uid", UserUtil.uid);
        params.put("nowPage", "" + nowPage);
        params.put("pageCount", "10");
        showLoading();
        AsyncClient.Get().setHost(Content.DOMAIN).setParams(params).setReturnClass(ChangeListResultDao.class).execute(new AsyncResponseHandler<ChangeListResultDao>() {

            @Override
            public void onResult(boolean success, ChangeListResultDao result, ResponseError error) {
                mListView.onRefreshComplete();
                dismissLoading();
                if (success && result.getDataList() != null) {
                    Log.i("result", "onResult: " + new Gson().toJson(result.getDataList()));
                    mlists.addAll(result.getDataList());
                    totalPage = result.getTotalPage();
                    if (mlists.size() == 0) {
                        Toast.makeText(ChangeListActivity.this, "暂无消息", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
