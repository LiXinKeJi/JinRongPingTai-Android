package com.finance.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finance.client.activity.PaySelectActivity;
import com.finance.client.R;
import com.finance.client.model.ConcernedPublishersDao;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.List;

/**
 * User : yh
 * Date : 17/9/2
 */

public class ReNewAdapter extends BaseAdapter{
    private Context mContext;
    private List<ConcernedPublishersDao> lists;
    private boolean searchAdapter = false;
    private View.OnClickListener followListener;
    public ReNewAdapter(Context mContext, List<ConcernedPublishersDao> lists){
        this.mContext = mContext;
        this.lists = lists;
    }

    public void setFollowListener(View.OnClickListener listener){
        this.followListener = listener;
    }

    public void setSearchAdapter(boolean search){

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
        View view = View.inflate(mContext, R.layout.item_renew,null);
        final ConcernedPublishersDao info = lists.get(position);
        if (info!=null) {
//            ((TextView) view.findViewById(R.id.Name)).setText(info.getCategory());
            ((TextView) view.findViewById(R.id.Name)).setText(info.getName());
            if (TextUtils.isEmpty(info.getLogo()))
            {
                ((ImageView) view.findViewById(R.id.HeadImg)).setImageResource(R.drawable.ic_launcher);
            }else {
                ImageLoaderUtil.getInstance().displayImage(info.getLogo(), (ImageView) view.findViewById(R.id.HeadImg));
            }
            ((TextView) view.findViewById(R.id.ID)).setText(info.getStopTime());
//            ((TextView) view.findViewById(R.id.Desc)).setText(info.getSignature());
//            ((TextView) view.findViewById(R.id.Score)).setText(info.getScore());
            ((TextView) view.findViewById(R.id.Fans)).setText("分类：" + info.getCategory());
            if (info.getStatus().equals("0")) {
                view.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
                ((TextView) view.findViewById(R.id.StatusInfo)).setText("续订");
                ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            } else if (info.getStatus().equals("1")) {
                view.setVisibility(View.GONE);
//                ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
//                ((TextView) view.findViewById(R.id.StatusInfo)).setText("订购");
//                ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            } else if (info.getStatus().equals("2")) {
                view.setVisibility(View.GONE);
//                ((TextView) view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.gray_15);
//                ((TextView) view.findViewById(R.id.StatusInfo)).setText("订购已满");
//                ((TextView) view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#777777"));
            }


            ((TextView) view.findViewById(R.id.StatusInfo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.getStatus().equals("0")) {
                        Intent intent = new Intent(mContext, PaySelectActivity.class);
                        Log.e("id",info.getMerchantID());
                        intent.putExtra("merchantID", info.getMerchantID());
                        intent.putExtra("isOrder", "1");
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        return view;
    }
}
