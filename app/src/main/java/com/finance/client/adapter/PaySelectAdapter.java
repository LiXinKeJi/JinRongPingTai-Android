package com.finance.client.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.model.PaySelectBean;

import java.util.List;

/**
 * Created by 小火
 * Create time on  2018/2/5
 * My mailbox is 1403241630@qq.com
 */

public class PaySelectAdapter extends BaseAdapter{
    private Context context;
    private List<PaySelectBean.PriceList> mList;
    private int defItem;//声明默认选中的项
    public PaySelectAdapter(Context context, List<PaySelectBean.PriceList> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PaySelectViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.price_item_layout,null);
            viewHolder = new PaySelectViewHolder();
            viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_choose);
            viewHolder.title = (TextView) convertView.findViewById(R.id.text_type_title);
            viewHolder.price = (TextView) convertView.findViewById(R.id.text_type_price);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (PaySelectViewHolder) convertView.getTag();
        }
        PaySelectBean.PriceList priceList = mList.get(position);
        viewHolder.title.setText(priceList.getProject());
        viewHolder.price.setText("￥" + priceList.getPrice());
        disposalView(position,convertView);
        return convertView;
    }

    private void disposalView(int position, View convertView) {
        Drawable selectIcon = context.getResources().getDrawable(R.drawable.chongzhi01);
        Drawable unSelectIcon = context.getResources().getDrawable(R.drawable.chongzhi02);
        if (position == defItem) {
            PaySelectViewHolder viewHolder = (PaySelectViewHolder) convertView.getTag();
            viewHolder.img.setImageDrawable(selectIcon);
        }else {
            PaySelectViewHolder viewHolder = (PaySelectViewHolder) convertView.getTag();
            viewHolder.img.setImageDrawable(unSelectIcon);
        }
    }
    class PaySelectViewHolder{
        TextView title,price;
        ImageView img;
    }
}
