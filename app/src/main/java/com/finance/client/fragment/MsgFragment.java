package com.finance.client.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.finance.client.activity.MsgListActivity;
import com.finance.client.R;
import com.finance.client.adapter.HomeMsgAdapter;
import com.finance.client.model.MsgItem;
import com.finance.client.model.MsgListDao;
import com.finance.client.util.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgFragment extends BaseFragment{
    private PullToRefreshListView mListView;
    private HomeMsgAdapter mAdapter;
    private List<MsgItem> msgList = new ArrayList<>();
    private int nowPage = 1;
    private int totalPage = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg_list,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (PullToRefreshListView) getView().findViewById(R.id.ListView);
        mAdapter = new HomeMsgAdapter(getContext(),msgList);
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
        mListView.setOnItemClickListener(onItemClickListener);
        RequestData();
    }

    private ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            msgRead(position-1);
        }
    };

    private void RequestData(){
        if(nowPage > totalPage){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                }
            });
            return;
        }
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","getMessageList");
        params.put("uid", UserUtil.uid);
        params.put("nowPage",""+nowPage);
        params.put("pageCount","10");
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(MsgListDao.class)
                .execute(new AsyncResponseHandler<MsgListDao>() {
                    @Override
                    public void onResult(boolean success, MsgListDao result, ResponseError error) {
                        mListView.onRefreshComplete();
                        if(success) {
                            msgList.addAll(result.getDataList());
                            mAdapter.notifyDataSetChanged();
                            totalPage = Integer.parseInt(result.getTotalPage());
                        }
                    }
                });
//        for(int i = 0; i< 10 ;++i){;
//            item.setCategoryID("123");
//            MsgItem item = new MsgItem();
//            item.setCategory("金融")
//            item.setLogo("https://img1.jiemian.com/101/original/20170904/150451014738829600_a280x210.jpg");
//            item.setTitle("金融");
//            item.setUnreadMessage(i);
//            msgList.add(item);
//        }
    }




    private void msgRead(final int index){
        MsgItem item = msgList.get(index);
        if(!TextUtils.isEmpty(item.getUnreadMessages())&&item.getUnreadMessages().equals("0") ){
            RouterMsgList(index);
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","readMessage");
        params.put("uid",UserUtil.uid);
        params.put("categoryID",item.getCategoryID());
        item.setUnreadMessages("0");
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        RouterMsgList(index);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    private void RouterMsgList(int index){
        MsgItem item = msgList.get(index);
        Intent intent = new Intent(getActivity(), MsgListActivity.class);
        intent.putExtra("categoryId",item.getCategoryID());
        intent.putExtra("title",item.getTitle());
        startActivity(intent);
}
}
