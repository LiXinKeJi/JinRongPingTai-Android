package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.adapter.MsgAdapter;
import com.finance.client.model.MsgInfoDao;
import com.finance.client.model.MsgInfoListDao;
import com.finance.client.util.Content;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.yhrun.alchemy.View.dialog.YHAlertDialog;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshBase;
import com.yhrun.alchemy.View.pulltorefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgListActivity extends BaseActivity{
    private PullToRefreshListView mListView;
    private MsgAdapter mAdapter;
    private List<MsgInfoDao> msgList = new ArrayList<>();
    private String categoryId;
    private ImageView delectIcon;
    private TextView cancelBtn;
    private int nowPage = 1;
    private int totalPage = 1;
    private boolean delete = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = getIntent().getStringExtra("title");
        setContentView(R.layout.activity_msg_list);
        super.onCreate(savedInstanceState);
        delectIcon = (ImageView) findViewById(R.id.RightBtnImg);
        cancelBtn = (TextView) findViewById(R.id.RightBtnText);
        delectIcon.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        cancelBtn.setText("取消");
        delectIcon.setImageResource(R.drawable.delete_icon);
        delectIcon.setVisibility(View.VISIBLE);
        categoryId = getIntent().getStringExtra("categoryId");
        mListView = (PullToRefreshListView) findViewById(R.id.ListView);
        mAdapter = new MsgAdapter(this,msgList);
        mListView.setAdapter(mAdapter);
        //this.initData();
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
//        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
//                msgList.clear();
//                nowPage = 1;
//                mAdapter.notifyDataSetChanged();
//                requestData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
//                nowPage += 1;
//                if(nowPage>totalPage){
//                    new Handler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mListView.onRefreshComplete();
//                        }
//                    });
//                }else{
//                    requestData();
//                }
//            }
//        });
        this.requestData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.RightBtnText){
            delete = false;
            mAdapter.setShowDeleteIcon(delete);
            delectIcon.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
        }else if(v.getId() == R.id.RightBtnImg){
            delete = true;
            mAdapter.setShowDeleteIcon(delete);
            delectIcon.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    private void initData(){
        for(int i = 0 ;i < 10 ; ++i){
            MsgInfoDao info = new MsgInfoDao();
            info.setLogo("https://img1.jiemian.com/101/original/20170904/150451014738829600_a280x210.jpg");
            info.setCategoryID("123");
            info.setMerchantID("123");
            info.setMessageID("123");
            info.setName("金融");
            info.setScore("3.5");
            info.setTime("2017-09-02");
            if(i % 2 == 0) {
                info.setType("1");
                info.setContent("https://img2.jiemian.com/101/original/20170902/150432406612353200_a640x360.jpg");
            }else {
                info.setType("0");
                info.setContent("证监会对公募流动性管理祭新规 余额宝或另有专项规则");
            }
            msgList.add(info);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void requestData(){
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","getMessagesDetail");
        params.put("uid", UserUtil.uid);
        params.put("categoryID",categoryId);
        params.put("pageCount","10");
        params.put("nowPage",""+nowPage);
        AsyncClient.Get()
                .setParams(params)
                .setHost(Content.DOMAIN)
                .setReturnClass(MsgInfoListDao.class)
                .execute(new AsyncResponseHandler<MsgInfoListDao>() {
                    @Override
                    public void onResult(boolean success, MsgInfoListDao result, ResponseError error) {
                        mListView.onRefreshComplete();
                        dismissLoading();
                        if(result.getResult().equals("1")){
                            Toast.makeText(MsgListActivity.this,result.getResultNote(),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        totalPage = Integer.parseInt(result.getTotalPage());
                        msgList.addAll(result.getDataList());
                        if(msgList.size() == 0){
                            Toast.makeText(MsgListActivity.this, "暂无消息", Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        msgList.clear();
//        this.requestData();
//        if(mAdapter == null){
//            mAdapter.notifyDataSetChanged();
//        }
//    }

    public void deleteMsg(final int index){
        YHAlertDialog dialog = new YHAlertDialog.Builder(this)
                .setCancel("取消")
                .setConfirm("删除")
                .setMessage("确定要删除吗？删除之后不可恢复")
                .setTitle("提示")
                .create();
        dialog.setOnDialogClickListener(new YHAlertDialog.OnDialogClickListener() {
            @Override
            public void onConfirm() {
                _deleteMsg(index);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void _deleteMsg(final int index){
        showLoading();
        MsgInfoDao item = msgList.get(index);
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","deleteMessageDetail");
        params.put("uid",UserUtil.uid);
        params.put("messageID",item.getMessageID());
        if (item.getContent().equals("系统消息"))
        {
            params.put("type","0");
        }else
        {
            params.put("type","1");
        }
        AsyncClient.Get()
                .setParams(params)
                .setReturnClass(String.class)
                .setHost(Content.DOMAIN)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(success){
                            try {
                                JSONObject jsonObject=new JSONObject(result);
                                if (jsonObject.getString("result").equals("1")) {
                                    Toast.makeText(MsgListActivity.this, jsonObject.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(MsgListActivity.this, "消息详情已删除", Toast.LENGTH_SHORT).show();
                                msgList.remove(index);
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toast.makeText(MsgListActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
