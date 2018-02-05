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
import com.finance.client.activity.MsgListActivity;
import com.finance.client.adapter.HomeMsgAdapter;
import com.finance.client.model.MeassageBean;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgFragment extends BaseFragment {
    private View view;
    private PullToRefreshListView mListView;
    private HomeMsgAdapter mAdapter;
    private List<MeassageBean.DataList> msgList = new ArrayList<>();
    private int nowPage = 1;
    private int totalPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_msg_list, null);
        initView();
        RequestData();
        return view;
    }

    private void initView() {
        mListView = (PullToRefreshListView) view.findViewById(R.id.msg_listView);
        mAdapter = new HomeMsgAdapter(getContext(), msgList);
        mListView.setAdapter(mAdapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                msgList.clear();
                nowPage = 1;
                mAdapter.notifyDataSetChanged();
                RequestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                nowPage += 1;
                RequestData();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                msgRead(position - 1);
            }
        });
    }

    private void RequestData() {
        if (nowPage > totalPage) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                }
            });
            return;
        }
        Map<String, String> params = new HashMap<>();
        String json = "{\"cmd\":\"getMessageList\",\"uid\":\""+UserUtil.uid+"\"," +
                "\"nowPage\":\""+nowPage+"\",\"pageCount\":\""+10+"\"}";
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(getActivity(),e.getMessage());
                mListView.onRefreshComplete();
            }
            @Override
            public void onResponse(String response, int id) {
                Log.i("response", "onResponse:" +response);
                Gson gson = new Gson();
                dismissLoading();
                MeassageBean meassageBean = gson.fromJson(response,MeassageBean.class);
                if (meassageBean.getResult().equals("1")){
                    ToastUtils.makeText(getActivity(),meassageBean.getResultNote());
                    mListView.onRefreshComplete();
                    return;
                }
                List<MeassageBean.DataList> dataLists = meassageBean.getDataList();
                msgList.addAll(dataLists);
                mAdapter.notifyDataSetChanged();
                totalPage =  Integer.parseInt(meassageBean.getTotalPage());
                mListView.onRefreshComplete();
            }
        });
    }

    private void msgRead(final int index) {
        MeassageBean.DataList item = msgList.get(index);
        if (!TextUtils.isEmpty(item.getUnreadMessages()) && item.getUnreadMessages().equals("0")) {
            RouterMsgList(index);
            return;
        }
        showLoading();
        Map<String, String> params = Maps.newHashMap();
        params.put("cmd", "readMessage");
        params.put("uid", UserUtil.uid);
        params.put("categoryID", item.getCategoryId());
        item.setUnreadMessages("0");
        AsyncClient.Get().setParams(params).setHost(Content.DOMAIN).setReturnClass(String.class).execute(new AsyncResponseHandler<String>() {
            @Override
            public void onResult(boolean success, String result, ResponseError error) {
                dismissLoading();
                RouterMsgList(index);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private void RouterMsgList(int index) {
        MeassageBean.DataList item = msgList.get(index);
        Intent intent = new Intent(getActivity(), MsgListActivity.class);
        intent.putExtra("categoryId", item.getCategoryId());
        intent.putExtra("title", item.getTitle());
        startActivity(intent);
    }
}
