package com.finance.client.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.finance.client.R;
import com.finance.client.model.ChangeInfoDao;
import java.util.List;

/**
 * User : yh
 * Date : 17/9/11
 */

public class ChangeAdapter extends BaseAdapter{
    private Context mContext;
    private List<ChangeInfoDao> lists;
    public ChangeAdapter(Context mContext,List<ChangeInfoDao> lists){
        this.mContext = mContext;
        this.lists = lists;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.change_item_layout,null);
        ChangeInfoDao info = lists.get(position);
        ((TextView)view.findViewById(R.id.Money)).setText(info.getMoney());
        ((TextView)view.findViewById(R.id.Type)).setText(info.getType());
        ((TextView)view.findViewById(R.id.Date)).setText(info.getTime());
        return view;
    }
}
