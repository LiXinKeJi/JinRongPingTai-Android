package com.finance.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.finance.client.R;
import com.finance.client.adapter.ReNewAdapter;
import com.finance.client.model.ConcernedPublishersDao;
import com.finance.client.model.RenewResultDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
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
 * Date : 17/9/5
 */

public class ReNewActivity extends BaseActivity{
    private PullToRefreshListView mListView;
    private ReNewAdapter adapter;
    private List<ConcernedPublishersDao> lists = new ArrayList<>();
    private int nowPage = 1;
    private int totalPage = 1;
    private ImageView img_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title="续订";
        setContentView(R.layout.activity_renew);
        super.onCreate(savedInstanceState);
        mListView = (PullToRefreshListView)findViewById(R.id.ListView);
        adapter = new ReNewAdapter(this,lists);
        mListView.setAdapter(adapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        this.requestData();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ConcernedPublishersDao info = lists.get(position-1);
//                Intent intent = new Intent(this, CompanyInfoActivity.class);
//                intent.putExtra("id",info.getMerchantID());
//                intent.putExtra("name",info.getName());
//                startActivity(intent);
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
        final String json = "{\"cmd\":\"getAuthorList\",\"uid\":\""+ UserUtil.uid+"\"" +
                ",\"pageCount\":\""+10+"\",\"nowPage\":\""+nowPage+"\"}";
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(ReNewActivity.this,e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                Gson gson = new Gson();
                RenewResultDao renewResultDao = gson.fromJson(response,RenewResultDao.class);
                mListView.onRefreshComplete();
                if (renewResultDao.getResult().equals("1")){
                    ToastUtils.makeText(ReNewActivity.this,renewResultDao.getResultNote());
                    return;
                }
                if (renewResultDao.getTotalPage()!=null) {
                    totalPage = Integer.parseInt(renewResultDao.getTotalPage());
                    Log.e("totalpage---",renewResultDao.getTotalPage());
                }

                if(renewResultDao.getDataList() != null) {
                    for(int i=0;i<renewResultDao.getDataList().size();i++)
                    {
                        if (!TextUtils.isEmpty(renewResultDao.getDataList().get(i).getStatus())&&renewResultDao.getDataList().get(i).getStatus().equals("0")) {
                            lists.add(renewResultDao.getDataList().get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
