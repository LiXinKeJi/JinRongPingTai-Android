package com.finance.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.finance.client.activity.PaySelectActivity;
import com.finance.client.R;
import com.finance.client.model.MasterDao;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.List;

/**
 * User : yh
 * Date : 17/9/2
 */

public class MasterAdapter1 extends BaseAdapter {
    private Context mContext;
    private List<MasterDao> lists;
    private boolean searchAdapter = false;
    private View.OnClickListener followListener;

    public MasterAdapter1(Context mContext, List<MasterDao> lists) {
        this.mContext = mContext;
        this.lists = lists;
    }

    public void setFollowListener(View.OnClickListener listener) {
        this.followListener = listener;
    }

    public void setSearchAdapter(boolean search) {

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
        final View view = View.inflate(mContext, R.layout.master_item_layout, null);
        final MasterDao info = lists.get(position);
        ((TextView) view.findViewById(R.id.Title)).setText(info.getCategory());
        if (TextUtils.isEmpty(info.getNickName())) {
            ((TextView) view.findViewById(R.id.Name)).setText(info.getName());
        } else {
            ((TextView) view.findViewById(R.id.Name)).setText(info.getNickName());
        }
        if (TextUtils.isEmpty(info.getLogo())) {
            ((ImageView) view.findViewById(R.id.HeadImg)).setImageResource(R.drawable.ic_launcher);
        } else {
            ImageLoaderUtil.getInstance().displayImage(info.getLogo(), (ImageView) view.findViewById(R.id.HeadImg));
        }
        ((TextView) view.findViewById(R.id.ID)).setText("ID 号：" + info.getMerchantID());
        ((TextView) view.findViewById(R.id.Desc)).setText(info.getSignature());
        ((TextView) view.findViewById(R.id.Score)).setText(info.getScore());
        ((TextView) view.findViewById(R.id.Fans)).setText("" + info.getFansNumber());
        if (info.getStatus().equals("0")) {
            ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
            ((TextView) view.findViewById(R.id.StatusInfo)).setText("已订购");
            ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            ((TextView) view.findViewById(R.id.StatusInfo)).setEnabled(false);
        } else if (info.getStatus().equals("1")) {
            ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
            ((TextView) view.findViewById(R.id.StatusInfo)).setText("订购");
            ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            ((TextView) view.findViewById(R.id.StatusInfo)).setEnabled(true);
        } else if (info.getStatus().equals("2")) {
            ((TextView) view.findViewById(R.id.StatusInfo)).setEnabled(false);
            ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.gray_15);
            ((TextView) view.findViewById(R.id.StatusInfo)).setText("订购已满");
            ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#777777"));
        }
        if (searchAdapter && "0".equals(info.getAttention())) {
            ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
            ((TextView) view.findViewById(R.id.StatusInfo)).setText("添加");
            ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            ((TextView) view.findViewById(R.id.StatusInfo)).setTag(position);
            ((TextView) view.findViewById(R.id.StatusInfo)).setOnClickListener(followListener);
        } else if (searchAdapter && "1".equals(info.getAttention())) {
            ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.gray_15);
            ((TextView) view.findViewById(R.id.StatusInfo)).setText("已添加");
            ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#777777"));
        } else {
            ((TextView) view.findViewById(R.id.StatusInfo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!searchAdapter && !info.getStatus().equals("2")) {
                        Intent intent = new Intent(mContext, PaySelectActivity.class);
                        intent.putExtra("merchantID", info.getMerchantID());
                        intent.putExtra("isOrder", "0");
                        mContext.startActivity(intent);
                    }
                }
            });
        }
        return view;
    }


}
