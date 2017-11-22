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
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.ReNewAdapter;
import com.finance.client.model.ConcernedPublishersDao;
import com.finance.client.model.RenewResultDao;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
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
        //this.initData();
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
        showLoading();
        params.put("cmd","getAuthorList");
        params.put("uid", UserUtil.uid);
        params.put("pageCount","10");
        params.put("nowPage",""+nowPage);
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setReturnClass(RenewResultDao.class)
                .setContext(this)
                .setParams(params)
                .execute(new AsyncResponseHandler<RenewResultDao>() {
                    @Override
                    public void onResult(boolean success, RenewResultDao result, ResponseError error) {
                        dismissLoading();
                        mListView.onRefreshComplete();
                        if(!success){
                            Toast.makeText(ReNewActivity.this,error.errorMsg,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(result.getResult().equals("1")){
                            Toast.makeText(ReNewActivity.this,result.getResultNote(),Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        Toast.makeText(ReNewActivity.this,result.getResultNote(),Toast.LENGTH_SHORT).show();
//
                        if (result.getTotalPage()!=null) {
                            totalPage = Integer.parseInt(result.getTotalPage());
                            Log.e("totalpage---",result.getTotalPage());
                        }
                        if(result.getDataList() != null) {
                            for(int i=0;i<result.getDataList().size();i++)
                            {
                                if (!TextUtils.isEmpty(result.getDataList().get(i).getStatus())&&result.getDataList().get(i).getStatus().equals("0")) {
                                    lists.add(result.getDataList().get(i));
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
