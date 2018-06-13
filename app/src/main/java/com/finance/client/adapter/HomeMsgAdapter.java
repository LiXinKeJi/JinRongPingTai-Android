package com.finance.client.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.model.MeassageBean;
import com.google.common.base.Strings;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.List;

/**
 * User : yh
 * Date : 17/8/14
 */

public class HomeMsgAdapter extends BaseAdapter {
    private List<MeassageBean.DataList> msgList;
    private Context mContext;

    public HomeMsgAdapter(Context mContext, List<MeassageBean.DataList> list) {
        this.mContext = mContext;
        this.msgList = list;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.msg_item_layout, null);
        MeassageBean.DataList item = msgList.get(position);
        if (item.getTitle().equals("系统消息")) {
            ((TextView) view.findViewById(R.id.Title)).setText("系统消息");
            ((TextView) view.findViewById(R.id.desc)).setText("");
        } else {
            ((TextView) view.findViewById(R.id.Title)).setText(item.getTitle());
            ((TextView) view.findViewById(R.id.desc)).setText(item.getCategory());
        }
        if (Strings.isNullOrEmpty(item.getCategoryId())) {
//            ((TextView)view.findViewById(R.id.Title)).setText("系统消息");
//            ((TextView)view.findViewById(R.id.desc)).setText("");
        } else {
//            ((TextView)view.findViewById(R.id.Title)).setText(item.getTitle());
//            ((TextView)view.findViewById(R.id.desc)).setText(item.getCategory());
        }
        if (!TextUtils.isEmpty(item.getUnreadMessages()) && Integer.parseInt(item.getUnreadMessages()) > 0) {
            view.findViewById(R.id.Count).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.Count)).setText("" + item.getUnreadMessages());
        } else {
            view.findViewById(R.id.Count).setVisibility(View.INVISIBLE);
        }

        ImageLoaderUtil.getInstance().displayImage(item.getLogo(), (ImageView) view.findViewById(R.id.Image));

        return view;
    }
}
