package com.finance.client.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.activity.MsgListActivity;
import com.finance.client.activity.ScoreActivity;
import com.finance.client.model.MsgInfoDao;
import com.finance.client.util.SeePhotoViewUtil;
import com.makeramen.roundedimageview.RoundedImageView;
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
    private String type;
    public MsgAdapter(MsgListActivity mContext, List<MsgInfoDao> list, String type){
        this.mContext = mContext;
        this.mList = list;
        this.type = type;
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
        MsgViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_info_layout,null);
            viewHolder = new MsgViewHolder();
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.Title);
            viewHolder.mTime = (TextView) convertView.findViewById(R.id.Time);
            viewHolder.mScore = (TextView) convertView.findViewById(R.id.Score);
            viewHolder.mContent = (TextView) convertView.findViewById(R.id.Content);
            viewHolder.mDeleteIcon = (ImageView) convertView.findViewById(R.id.DeleteIcon);
            viewHolder.mImage = (ImageView) convertView.findViewById(R.id.Image);
            viewHolder.mLogo = (RoundedImageView) convertView.findViewById(R.id.Logo);
            viewHolder.mScoreBtn = (TextView) convertView.findViewById(R.id.ScoreBtn);
            viewHolder.rl_msg_score = (RelativeLayout) convertView.findViewById(R.id.rl_msg_score);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (MsgViewHolder) convertView.getTag();
        }
        final MsgInfoDao info = mList.get(position);
        viewHolder.mTitle.setText(info.getName());
        viewHolder.mTime.setText(info.getTime());
        viewHolder.mScore.setText(info.getScore());
        String img = info.getLogo();
        if (type.equals("0")){
            viewHolder.rl_msg_score.setVisibility(View.GONE);
        }else {
            viewHolder.rl_msg_score.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(img)) {
            ImageLoaderUtil.getInstance().displayImage(info.getLogo(), viewHolder.mLogo);
        }
        if(!info.getType().equals("1")){
            viewHolder.mContent.setVisibility(View.VISIBLE);
            viewHolder.mImage.setVisibility(View.GONE);
            viewHolder.mContent.setText(info.getContent());
        }else {
            viewHolder.mContent.setVisibility(View.GONE);
            viewHolder.mImage.setVisibility(View.VISIBLE);
            final List<String> urls = new ArrayList<>();
            urls.add(info.getContent());
            if(!TextUtils.isEmpty(info.getContent())) {
                ImageLoaderUtil.getInstance().displayImage(info.getContent(), viewHolder.mImage);
            }
            viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SeePhotoViewUtil.toPhotoView(mContext, urls, 0);
                }
            });
        }
        if(info.getIsComment() == 0){
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.dinggou02);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mScore.setCompoundDrawables(drawable,null,null,null);
        }else{
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.score_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mScore.setCompoundDrawables(drawable,null,null,null);
        }
        if(showDeleteIcon){
            viewHolder.mDeleteIcon.setVisibility(View.VISIBLE);
            viewHolder.mDeleteIcon .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.deleteMsg(position);
                }
            });
        }else{
            viewHolder.mDeleteIcon .setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(info.getScore())&&Double.parseDouble(info.getScore())>0)
        {
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.score_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mScore.setCompoundDrawables(drawable,null,null,null);
        }else
        {
            Drawable drawable= mContext.getResources().getDrawable(R.drawable.dinggou02);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mScore.setCompoundDrawables(drawable,null,null,null);
        }
        viewHolder.mScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(info.getIsComment() ==0) {
                    Intent intent = new Intent(mContext, ScoreActivity.class);
                    intent.putExtra("msgId", info.getMessageId());
                    mContext.startActivityForResult(intent, 0xff);
                }else{
                    Toast.makeText(mContext, "已评价", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    class MsgViewHolder{
        RoundedImageView mLogo;
        ImageView mImage,mDeleteIcon;
        TextView mTitle,mTime,mScore,mContent,mScoreBtn;
        RelativeLayout rl_msg_score;
    }
}
