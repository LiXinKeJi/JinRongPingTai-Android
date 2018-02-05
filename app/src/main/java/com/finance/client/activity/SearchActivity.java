package com.finance.client.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.MasterAdapter;
import com.finance.client.common.IndustryChooseDialog;
import com.finance.client.model.MasterDao;
import com.finance.client.model.MasterListDao;
import com.finance.library.BaseActivity;
import com.finance.library.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User : yh
 * Date : 17/9/13
 */

public class SearchActivity extends BaseActivity{
    public MasterAdapter adapter;
    private List<MasterDao> lists = new ArrayList<>();
    private PullToRefreshListView mListView;
    private TextView rightBtn;
    private EditText mComment;
    private JSONArray category;
    private String industryId;
    private int totalPage = 1;
    private int nowPage = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_layout);
        super.onCreate(savedInstanceState);
        mListView = (PullToRefreshListView) findViewById(R.id.ListView);
        adapter = new MasterAdapter(this,lists,true);
        mListView.setAdapter(adapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        rightBtn = (TextView) findViewById(R.id.RightBtnText);
        mComment = (EditText) findViewById(R.id.edit_comment);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(this);
        requestCategory();
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                lists.clear();
                nowPage = 1;
                adapter.notifyDataSetChanged();
                Search();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                nowPage += 1;
                if(nowPage>totalPage){
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    });
                }else{
                    Search();
                }
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCategory();
            }
        });
    }


    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.RightBtnText){
            lists.clear();
            nowPage = 1;
            adapter.notifyDataSetChanged();
            Search();
        }
    }
    private void Search(){
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","searchAuthor");
        params.put("industryId",industryId);
        params.put("pageCount","10");
        params.put("nowPage",""+nowPage);
        params.put("uid", UserUtil.uid);
        showLoading();
        AsyncClient.Get().setParams(params).setReturnClass(MasterListDao.class).setHost(Content.DOMAIN).execute(new AsyncResponseHandler<MasterListDao>() {
                    @Override
                    public void onResult(boolean success, MasterListDao result, ResponseError error) {
                        mListView.onRefreshComplete();
                        dismissLoading();
                        if(!success){
                            Toast.makeText(SearchActivity.this, ""+error.errorMsg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (result.getResult().equals("1"))
                        {
                            Toast.makeText(SearchActivity.this, result.getResultNote(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        totalPage =Integer.parseInt(result.getTotalPage()) ;
                        lists.addAll(result.getDataList());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void chooseCategory(){
        if(category == null){
            Toast.makeText(this, "无法获取分类数据", Toast.LENGTH_SHORT).show();
            return;
        }
        IndustryChooseDialog dialog = new IndustryChooseDialog(this,category);
        dialog.setOnCategoryChangeListener(new IndustryChooseDialog.onCategoryChangeListener() {
            @Override
            public void onChange(String info,String id) {
                mComment.setText(info);
                industryId = id;
            }
        });
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    private void requestCategory(){
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","getIndustryCategory");
        params.put("uid", UserUtil.uid);
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(success){
//                            Toast.makeText(SearchActivity.this, "获取分类数据成功", Toast.LENGTH_SHORT).show();
                            try {
                                category = new JSONObject(result).getJSONArray("flist");
                                chooseCategory();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

}
