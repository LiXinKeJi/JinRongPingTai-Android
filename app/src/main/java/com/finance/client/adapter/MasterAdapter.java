package com.finance.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.activity.PaySelectActivity;
import com.finance.client.common.LogOutDialog;
import com.finance.client.model.MasterDao;
import com.finance.library.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.List;
import java.util.Map;

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
            viewHolder.ID = (TextView) convertView.findViewById(R.id.master_ID);
            viewHolder.Fans = (TextView) convertView.findViewById(R.id.master_Fans);
            viewHolder.Score = (TextView) convertView.findViewById(R.id.master_Score);
            viewHolder.Desc = (TextView) convertView.findViewById(R.id.master_Desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MasterViewHolder) convertView.getTag();
        }
        final MasterDao info = lists.get(position);
        if (TextUtils.isEmpty(info.getNickName())) {
            viewHolder.Name.setText(info.getName());
        } else {
            viewHolder.Name.setText(info.getNickName());
        }
        if (TextUtils.isEmpty(info.getLogo())) {
            viewHolder.HeadImg.setImageResource(R.drawable.ic_launcher);
        } else {
            ImageLoaderUtil.getInstance().displayImage(info.getLogo(), viewHolder.HeadImg);
        }
        viewHolder.Title.setText(info.getCategory());
        viewHolder.ID.setText("ID 号：" + info.getMerchantID());
        viewHolder.Desc.setText(info.getSignature());
        viewHolder.Score.setText(info.getScore());
        viewHolder.Fans.setText("" + info.getFansNumber());
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
                    viewHolder.StatusInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog1 == null)
                                dialog1 = new LogOutDialog(mContext, R.string.are_you_sure_cancel, new LogOutDialog.OnSureBtnClickListener() {
                                    @Override
                                    public void sure() {
                                        cancel(position);
                                    }
                                });
                            dialog1.show();
                        }
                    });
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
                            intent.putExtra("merchantID", info.getMerchantID());
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
        TextView Title, StatusInfo, Name, ID, Fans, Score, Desc;
    }

    private void follow(int index) {
        final MasterDao info = lists.get(index);
        Map<String, String> params = Maps.newHashMap();
        params.put("cmd", "attention");
        params.put("uid", UserUtil.uid);
        params.put("merchantID", info.getMerchantID());
        AsyncClient.Get().setHost(Content.DOMAIN).setParams(params).execute(new AsyncResponseHandler() {
            @Override
            public void onResult(boolean success, Object result, ResponseError error) {
                if (success) {
                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                    info.setAttention("1");
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void cancel(int index) {
        final MasterDao info = lists.get(index);
        Map<String, String> params = Maps.newHashMap();
        params.put("cmd", "cancleOrder");
        params.put("uid", UserUtil.uid);
        params.put("merchantID", info.getMerchantID());
        AsyncClient.Get().setHost(Content.DOMAIN).setParams(params).execute(new AsyncResponseHandler() {
            @Override
            public void onResult(boolean success, Object result, ResponseError error) {
                if (success) {
                    Toast.makeText(mContext, "取消订购成功", Toast.LENGTH_SHORT).show();
                    info.setStatus("1");
                    notifyDataSetChanged();
                }
            }
        });
    }
}
