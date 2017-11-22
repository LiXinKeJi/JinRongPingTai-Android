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
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.activity.CompanyInfoActivity;
import com.finance.client.activity.SearchActivity;
import com.finance.client.adapter.MasterAdapter1;
import com.finance.client.model.MasterDao;
import com.finance.client.model.MasterListDao;
import com.finance.client.util.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User : yh
 * Date : 17/9/2
 */

public class MasterFragment extends BaseFragment implements View.OnClickListener{
    private PullToRefreshListView mListView;
    private MasterAdapter1 adapter;
    private List<MasterDao> lists = new ArrayList<>();
    private int nowPage = 1;
    private int totalPage = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_layout,null);
        view.findViewById(R.id.SearchBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lists.clear();
        this.requestData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (PullToRefreshListView) getView().findViewById(R.id.ListView);
        adapter = new MasterAdapter1(this.getContext(),lists);
        mListView.setAdapter(adapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MasterDao info = lists.get(position-1);
                Intent intent = new Intent(MasterFragment.this.getActivity(), CompanyInfoActivity.class);
                intent.putExtra("id",info.getMerchantID());
                if (TextUtils.isEmpty(info.getNickName())) {
                    intent.putExtra("name", info.getName());
                }else
                {
                    intent.putExtra("name", info.getNickName());
                }
                startActivity(intent);
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                lists.clear();
                nowPage = 1;
                adapter.notifyDataSetChanged();
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                nowPage += 1;
                if(nowPage > totalPage){
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    });
                }else{
                    requestData();
                }
            }
        });

    }

    private void requestData(){
        Map<String,String> params = Maps.newHashMap();
        showLoading();
        params.put("cmd","getAuthorList");
        params.put("uid", UserUtil.uid);
        params.put("pageCount","10");
        params.put("nowPage",""+nowPage);
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setReturnClass(MasterListDao.class)
                .setContext(this.getContext())
                .setParams(params)
                .execute(new AsyncResponseHandler<MasterListDao>() {
                    @Override
                    public void onResult(boolean success, MasterListDao result, ResponseError error) {
                        dismissLoading();
                        mListView.onRefreshComplete();
//                        if (!success) {
//                            Toast.makeText(MasterFragment.this.getContext(), error.errorMsg, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        if (success) {
                            if (result.getResult().equals("1")) {
                                Toast.makeText(MasterFragment.this.getContext(), result.getResultNote(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!TextUtils.isEmpty(result.getTotalPage()))
                            {
                                totalPage = Integer.parseInt(result.getTotalPage());
                            }
                            if (result.getDataList() != null) {
                                Log.i("result", "onResult: " + new Gson().toJson(result.getDataList()));
                                lists.addAll(result.getDataList());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.SearchBtn){
            Intent intent = new Intent(this.getActivity(), SearchActivity.class);
            startActivity(intent);
        }
    }
}


