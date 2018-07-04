package com.finance.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.finance.client.R;
import com.finance.client.activity.ChangeListActivity;
import com.finance.client.model.ChangeListResultDao;

import java.util.List;

/**
 * User : yh
 * Date : 17/9/11
 */

public class ChangeAdapter extends BaseAdapter{
    private Context mContext;
    private List<ChangeListResultDao.ChangeInfoDao> lists;
    public ChangeAdapter(ChangeListActivity mContext, List<ChangeListResultDao.ChangeInfoDao> mlists) {
        this.mContext = mContext;
        this.lists = mlists;
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
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.change_item_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.mMoney = (TextView) convertView.findViewById(R.id.Money);
            viewHolder.mType = (TextView) convertView.findViewById(R.id.Type);
            viewHolder.mDate = (TextView) convertView.findViewById(R.id.Date);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChangeListResultDao.ChangeInfoDao info = lists.get(position);
        viewHolder.mType.setText(info.getType());
        viewHolder.mDate.setText(info.getTime());
        if (info.getType().contains("充值")){
            viewHolder.mMoney.setText(info.getMoney()+"元");
        }else {
            viewHolder.mMoney.setText( info.getMoney()+"元");
        }
        return convertView;
    }
    class ViewHolder{
        TextView mMoney,mType,mDate;
    }
}
