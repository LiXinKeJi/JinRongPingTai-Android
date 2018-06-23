package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.MasterAdapter;
import com.finance.client.common.IndustryChooseDialog;
import com.finance.client.fragment.MasterFragment;
import com.finance.client.model.MasterDao;
import com.finance.client.model.MasterListDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MasterDao info = lists.get(position - 1);
                Intent intent = new Intent(SearchActivity.this, CompanyInfoActivity.class);
                intent.putExtra("id", info.getMerchantId());
                intent.putExtra("name", info.getName());
                startActivity(intent);
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
        Map<String,String> params = new HashMap<>();
        String json = "{\"cmd\":\"searchAuthor\",\"uid\":\""+ UserUtil.uid+"\",\"industryId\":\""+industryId+"\"" +
                ",\"pageCount\":\""+10+"\",\"nowPage\":\""+nowPage+"\",\"uid\":\""+UserUtil.uid+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.makeText(SearchActivity.this, e.getMessage());
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("666", "onResponse: " +response);
                dismissLoading();
                Gson gson = new Gson();
                MasterListDao masterListDao = gson.fromJson(response,MasterListDao.class);
                mListView.onRefreshComplete();
                if (masterListDao.getResult().equals("1")){
                    ToastUtils.makeText(SearchActivity.this, ""+masterListDao.getResultNote());
                    return;
                }
                totalPage =Integer.parseInt(masterListDao.getTotalPage()) ;
                List<MasterDao> masterDaos = masterListDao.getDataList();
                if (masterDaos != null && !masterDaos.isEmpty() && masterDaos.size() > 0){
                    lists.addAll(masterDaos);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void chooseCategory(){
        if(category == null){
            Toast.makeText(this, "无法获取分类数据", Toast.LENGTH_SHORT).show();
            return;
        }
        IndustryChooseDialog dialog = new IndustryChooseDialog(this,category,0);
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
        String json = "{\"cmd\":\"getIndustryCategory\",\"uid\":\""+UserUtil.uid+"\"}";
        params.put("json",json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(SearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissLoading();
            }
            @Override
            public void onResponse(String response, int id) {
                Log.i("666", "onResponse: " +response);
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(SearchActivity.this,"" + obj.getString("resultNote"));
                        return;
                    }
                    category = obj.getJSONArray("flist");
                    chooseCategory();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
