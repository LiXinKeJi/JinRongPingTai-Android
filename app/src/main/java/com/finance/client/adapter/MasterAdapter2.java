package com.finance.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.finance.client.MyApplication;
import com.finance.client.R;
import com.finance.client.activity.PaySelectActivity;
import com.finance.client.common.LogOutDialog;
import com.finance.client.http.DelMaster;
import com.finance.client.model.MasterDao;
import com.finance.client.util.Content;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yhrun.alchemy.Util.ImageLoaderUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/2
 */

public class MasterAdapter2 extends BaseAdapter {
    private Context mContext;
    private List<MasterDao> lists;
    private boolean searchAdapter;

    public RefreshCallBack refreshCallBack;

    public interface RefreshCallBack {
        void Refresh();
    }


    public MasterAdapter2(Context mContext, List<MasterDao> lists, boolean searchAdapter, RefreshCallBack refreshCallBack) {
        this.mContext = mContext;
        this.lists = lists;
        this.searchAdapter = searchAdapter;
        this.refreshCallBack=refreshCallBack;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MasterViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.master_item_layout2, null);
            viewHolder = new MasterViewHolder();
            viewHolder.HeadImg = (RoundedImageView) convertView.findViewById(R.id.master_HeadImg);
            viewHolder.Title = (TextView) convertView.findViewById(R.id.master_Title);
            viewHolder.StatusInfo = (TextView) convertView.findViewById(R.id.master_StatusInfo);
            viewHolder.Name = (TextView) convertView.findViewById(R.id.master_Name);
            viewHolder.NickName = (TextView) convertView.findViewById(R.id.master_nickName);
            viewHolder.ID = (TextView) convertView.findViewById(R.id.master_ID);
            viewHolder.Fans = (TextView) convertView.findViewById(R.id.master_Fans);
            viewHolder.Score = (TextView) convertView.findViewById(R.id.master_Score);
            viewHolder.Desc = (TextView) convertView.findViewById(R.id.master_Desc);
            viewHolder.iv_del = (ImageView) convertView.findViewById(R.id.iv_del);
            viewHolder.itemview_swipe = (SwipeLayout) convertView.findViewById(R.id.itemview_swipe);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MasterViewHolder) convertView.getTag();
        }
        final MasterDao info = lists.get(position);
        if (TextUtils.isEmpty(info.getNickName())) {
            viewHolder.NickName.setVisibility(View.GONE);
        } else {
            viewHolder.NickName.setText("(" + info.getNickName() + ")");
        }
        if (TextUtils.isEmpty(info.getLogo())) {
            viewHolder.HeadImg.setImageResource(R.drawable.ic_launcher);
        } else {
            ImageLoaderUtil.getInstance().displayImage(info.getLogo(), viewHolder.HeadImg);
        }
        viewHolder.Name.setText(info.getName());
        viewHolder.Title.setText(info.getCategory());
        if (position > 0) {
            if (lists.get(position).getCategory().equals(lists.get(position - 1).getCategory())) {
                viewHolder.Title.setVisibility(View.GONE);
            } else {
                viewHolder.Title.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.ID.setText("ID 号：" + info.getMerchantId());
        viewHolder.Desc.setText("个性签名：" + info.getSignature());
        viewHolder.Score.setText(info.getScore());
        viewHolder.Fans.setText("" + info.getFansNumber());
        viewHolder.itemview_swipe.close();
        viewHolder.itemview_swipe.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                if (!searchAdapter) {
                    if (info.getStatus().equals("0")) {//已订购
                        layout.close();
                    }
                }
            }
        });
        viewHolder.iv_del.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!searchAdapter) {
                    if (info.getStatus().equals("0")) {//已订购
                        viewHolder.itemview_swipe.close();
                    } else {
                        DelMaster.delMaster(mContext, info.getMerchantId(), new DelMaster.DelMasterCallBack() {
                            @Override
                            public void del(String id) {
                                for (int i = 0; i < lists.size(); i++) {
                                    try {
                                        if (lists.get(position).getMerchantId().equals(id)) {
                                            refreshCallBack.Refresh();
                                            break;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        });
                    }
                }
                return true;
            }
        });


        if (searchAdapter) {
            switch (info.getAttention()) {
                case "0":
                    viewHolder.StatusInfo.setBackgroundResource(R.drawable.black_15);
                    viewHolder.StatusInfo.setText("添加");
                    viewHolder.StatusInfo.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.StatusInfo.setTag(position);
                    viewHolder.StatusInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            follow(position);
                        }
                    });
                    break;
                case "1":
                    viewHolder.StatusInfo.setBackgroundResource(R.drawable.gray_15);
                    viewHolder.StatusInfo.setText("已添加");
                    viewHolder.StatusInfo.setTextColor(Color.parseColor("#777777"));
                    break;
            }
        } else {
            switch (info.getStatus()) {
                case "0":
                    viewHolder.StatusInfo.setBackgroundResource(R.drawable.black_15);
                    viewHolder.StatusInfo.setText("已订购");
                    viewHolder.StatusInfo.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.StatusInfo.setEnabled(true);
                    break;
                case "1":
                    viewHolder.StatusInfo.setBackgroundResource(R.drawable.black_15);
                    viewHolder.StatusInfo.setText("订购");
                    viewHolder.StatusInfo.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.StatusInfo.setEnabled(true);
                    viewHolder.StatusInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, PaySelectActivity.class);
                            intent.putExtra("merchantID", info.getMerchantId());
                            intent.putExtra("isOrder", "0");
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case "2":
                    viewHolder.StatusInfo.setEnabled(false);
                    viewHolder.StatusInfo.setBackgroundResource(R.drawable.gray_15);
                    viewHolder.StatusInfo.setText("订购已满");
                    viewHolder.StatusInfo.setTextColor(Color.parseColor("#777777"));
                    break;
            }
        }
        return convertView;
    }


    class MasterViewHolder {
        RoundedImageView HeadImg;
        TextView Title, StatusInfo, Name, NickName, ID, Fans, Score, Desc;
        ImageView iv_del;
        SwipeLayout itemview_swipe;
    }

    private void follow(int index) {
        final MasterDao info = lists.get(index);
        Map<String, String> params = Maps.newHashMap();
        final String json = "{\"cmd\":\"attention\",\"uid\":\"" + UserUtil.uid + "\",\"merchantID\":\"" + info.getMerchantId() + "\"}";
        params.put("json", json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.makeText(mContext, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("1")) {
                        ToastUtils.makeText(mContext, "" + jsonObject.getString("resultNote"));
                        return;
                    }
                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                    info.setAttention("1");
                    MyApplication.temp = 1;
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void cancel(int index) {
        final MasterDao info = lists.get(index);
        Map<String, String> params = Maps.newHashMap();
        final String json = "{\"cmd\":\"cancleOrder\",\"uid\":\"" + UserUtil.uid + "\",\"merchantID\":\"" + info.getMerchantId() + "\"}";
        params.put("json", json);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.makeText(mContext, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("1")) {
                        ToastUtils.makeText(mContext, "" + jsonObject.getString("resultNote"));
                        return;
                    }
                    Toast.makeText(mContext, "取消订购成功", Toast.LENGTH_SHORT).show();
                    info.setStatus("1");
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
