package com.finance.client.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.finance.client.R;
import com.finance.client.activity.CompanyInfoActivity;
import com.finance.client.activity.SearchActivity;
import com.finance.client.adapter.MasterAdapter;
import com.finance.client.adapter.MasterAdapter2;
import com.finance.client.model.MasterDao;
import com.finance.client.model.MasterListDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.finance.client.util.abLog;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/2
 */

public class MasterFragment extends BaseFragment implements View.OnClickListener, MasterAdapter2.RefreshCallBack {
    private View view;
    private PullToRefreshListView mListView;
    private MasterAdapter2 adapter;
    private List<MasterDao> lists = new ArrayList<>();
    private int nowPage = 1;
    private int totalPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_master_layout, null);
        initView();
        return view;
    }

    private void initView() {
        view.findViewById(R.id.SearchBtn).setOnClickListener(this);
        mListView = (PullToRefreshListView) view.findViewById(R.id.ListView);
        adapter = new MasterAdapter2(this.getContext(), lists, false, this);
        mListView.setAdapter(adapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                lists.clear();
                nowPage = 1;
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
    }


    private void requestData() {
        Map<String, String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"getAuthorList\",\"uid\":\"" + UserUtil.uid + "\"" +
                ",\"pageCount\":\"" + 10 + "\",\"nowPage\":\"" + nowPage + "\"}";
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(getActivity(), e.getMessage());
                mListView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response, int id) {
                abLog.e("订阅者消息", "onResponse: " + response);
                Gson gson = new Gson();
                dismissLoading();
                MasterListDao masterListDao = gson.fromJson(response, MasterListDao.class);
                if (masterListDao.getResult().equals("1")) {
                    ToastUtils.makeText(getActivity(), masterListDao.getResultNote());
                    mListView.onRefreshComplete();
                    return;
                }
                if (!TextUtils.isEmpty(masterListDao.getTotalPage())) {
                    totalPage = Integer.parseInt(masterListDao.getTotalPage());
                }
                if (masterListDao.getDataList() != null) {
                    for (int i = 0; i < masterListDao.getDataList().size(); i++) {//排序，把分类相同的放一起
                        if (lists.contains(masterListDao.getDataList().get(i).getCategory())) {
                            lists.add(masterListDao.getDataList().get(i));
                        } else {
                            boolean b=false;
                            for (int j = 0; j < lists.size(); j++) {
                                if (lists.get(j).getCategory().equals(masterListDao.getDataList().get(i).getCategory())) {
                                    lists.add(j + 1, masterListDao.getDataList().get(i));
                                    b=true;
                                    break;
                                }
                            }
                            if(!b){
                                lists.add( masterListDao.getDataList().get(i));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    mListView.onRefreshComplete();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SearchBtn) {
            Intent intent = new Intent(this.getActivity(), SearchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lists.clear();
        nowPage = 1;
        requestData();
    }

    @Override
    public void Refresh() {
        lists.clear();
        nowPage = 1;
        requestData();
    }

}


