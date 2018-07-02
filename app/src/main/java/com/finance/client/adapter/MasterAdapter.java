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
import com.finance.client.R;
import com.finance.client.MyApplication;
import com.finance.client.activity.PaySelectActivity;
import com.finance.client.common.LogOutDialog;
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

public class MasterAdapter extends BaseAdapter {
    private Context mContext;
    private List<MasterDao> lists;
    private boolean searchAdapter;
    private LogOutDialog dialog1;

    public MasterAdapter(Context mContext, List<MasterDao> lists, boolean searchAdapter) {
        this.mContext = mContext;
        this.lists = lists;
        this.searchAdapter = searchAdapter;
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
        MasterViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.master_item_layout, null);
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
            viewHolder.tv_authen = (TextView) convertView.findViewById(R.id.tv_authen);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MasterViewHolder) convertView.getTag();
        }
        final MasterDao info = lists.get(position);
        if (TextUtils.isEmpty(info.getNickName())) {
            viewHolder.NickName.setVisibility(View.GONE);
        } else {
            viewHolder.NickName.setVisibility(View.VISIBLE);
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

        if(info.getState().equals("1")){
            viewHolder.tv_authen.setText("已认证");
        }else{
            viewHolder.tv_authen.setText("未认证");
        }

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
        TextView Title, StatusInfo, Name, NickName, ID, Fans, Score, Desc,tv_authen;
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
