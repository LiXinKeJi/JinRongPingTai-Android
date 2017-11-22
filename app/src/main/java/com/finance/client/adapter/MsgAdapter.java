package com.finance.client.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.activity.MsgListActivity;
import com.finance.client.R;
import com.finance.client.activity.ScoreActivity;
import com.finance.client.model.MsgInfoDao;
import com.finance.client.util.SeePhotoViewUtil;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgAdapter extends BaseAdapter{
    private List<MsgInfoDao> mList;
    private MsgListActivity mContext;
    private boolean showDeleteIcon;

    public MsgAdapter(MsgListActivity mContext, List<MsgInfoDao> list){
        this.mContext = mContext;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void setShowDeleteIcon(boolean show){
        showDeleteIcon = show;
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.msg_info_layout,null);
        final MsgInfoDao info = mList.get(position);
        ((TextView)view.findViewById(R.id.Title)).setText(info.getName());
        ((TextView)view.findViewById(R.id.Time)).setText(info.getTime());
        ((TextView)view.findViewById(R.id.Score)).setText(info.getScore());
        if(!TextUtils.isEmpty(info.getLogo())) {
            ImageLoaderUtil.getInstance().displayImage(info.getLogo(), (ImageView) view.findViewById(R.id.Logo));
        }
        if(!info.getType().equals("1")){
            view.findViewById(R.id.Content).setVisibility(View.VISIBLE);
            view.findViewById(R.id.Image).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.Content)).setText(info.getContent());

        }else {
            view.findViewById(R.id.Content).setVisibility(View.VISIBLE);
            view.findViewById(R.id.Image).setVisibility(View.VISIBLE);
            final List<String> urls=new ArrayList<>();
            urls.add(info.getContent());
            if(!TextUtils.isEmpty(info.getContent())) {

                ImageLoaderUtil.getInstance().displayImage(info.getContent(), (ImageView) view.findViewById(R.id.Image));
            }
            view.findViewById(R.id.Image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SeePhotoViewUtil.toPhotoView(mContext, urls, 0);
                }
            });

        }
        if(info.getIsComment() == 0){
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.dinggou02);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView)view.findViewById(R.id.Score)).setCompoundDrawables(drawable,null,null,null);
        }else{
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.score_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView)view.findViewById(R.id.Score)).setCompoundDrawables(drawable,null,null,null);
        }
        if(showDeleteIcon){
            view.findViewById(R.id.DeleteIcon).setVisibility(View.VISIBLE);
            view.findViewById(R.id.DeleteIcon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.deleteMsg(position);
                }
            });
        }else{
            view.findViewById(R.id.DeleteIcon).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(info.getScore())&&Double.parseDouble(info.getScore())>0)
        {
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.score_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView)view.findViewById(R.id.Score)).setCompoundDrawables(drawable,null,null,null);
        }else
        {
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.dinggou02);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView)view.findViewById(R.id.Score)).setCompoundDrawables(drawable,null,null,null);
        }
        view.findViewById(R.id.ScoreBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(info.getIsComment() ==0) {
                    Intent intent = new Intent(mContext, ScoreActivity.class);
                    intent.putExtra("msgId", info.getMessageID());
                    mContext.startActivityForResult(intent, 0xff);
                }else{
                    Toast.makeText(mContext, "已评价", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

}
