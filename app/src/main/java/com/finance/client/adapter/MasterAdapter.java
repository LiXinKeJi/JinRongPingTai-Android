package com.finance.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.model.MasterDao;
import com.finance.library.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.List;
import java.util.Map;

/**
 * User : yh
 * Date : 17/9/2
 */

public class MasterAdapter extends BaseAdapter{
    private Context mContext;
    private List<MasterDao> lists;
    private boolean searchAdapter = false;
    private View.OnClickListener followListener;
    public MasterAdapter(Context mContext,List<MasterDao> lists){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(mContext, R.layout.master_item_layout,null);
        final MasterDao info = lists.get(position);
        ((TextView)view.findViewById(R.id.Title)).setText(info.getCategory());
        ((TextView)view.findViewById(R.id.Name)).setText(info.getName());
        ImageLoaderUtil.getInstance().displayImage(info.getLogo(), (ImageView) view.findViewById(R.id.HeadImg));
        ((TextView)view.findViewById(R.id.ID)).setText("ID 号："+info.getMerchantID());
        ((TextView)view.findViewById(R.id.Desc)).setText(info.getSignature());
        ((TextView)view.findViewById(R.id.Score)).setText(info.getScore());
        ((TextView)view.findViewById(R.id.Fans)).setText(""+info.getFansNumber());
//        if(info.getStatus().equals("0")){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.black_15);
//            ((TextView)view.findViewById(R.id.StatusInfo)).setText("已订购");
//            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
//        }else if(info.getStatus().equals("1")){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.black_15);
//            ((TextView)view.findViewById(R.id.StatusInfo)).setText("订购");
//            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
//        }else if(info.getStatus().equals("2")){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.gray_15);
//            ((TextView)view.findViewById(R.id.StatusInfo)).setText("订购已满");
//            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#777777"));
//        }


        if(info.getAttention().equals("0")){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.black_15);
            ((TextView)view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
            ((TextView)view.findViewById(R.id.StatusInfo)).setText("添加");
            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            ((TextView)view.findViewById(R.id.StatusInfo)).setEnabled(true);
//            view.findViewById(R.id.StatusLayout).setEnabled(true);
        }else if(info.getAttention().equals("1")){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.black_15);
//            view.findViewById(R.id.StatusLayout).setEnabled(false);
            ((TextView)view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
            ((TextView)view.findViewById(R.id.StatusInfo)).setEnabled(false);
            ((TextView)view.findViewById(R.id.StatusInfo)).setText("已添加");
            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
        }
        if(searchAdapter && "0".equals(info.getAttention())){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.black_15);
//            view.findViewById(R.id.StatusLayout).setEnabled(true);
            ((TextView)view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.black_15);
            ((TextView)view.findViewById(R.id.StatusInfo)).setEnabled(true);
            ((TextView)view.findViewById(R.id.StatusInfo)).setText("添加");
            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#ffffff"));
            ((TextView)view.findViewById(R.id.StatusInfo)).setTag(position);
            ((TextView)view.findViewById(R.id.StatusInfo)).setOnClickListener(followListener);
//            view.findViewById(R.id.StatusLayout).setTag(position);
//            view.findViewById(R.id.StatusLayout).setOnClickListener(followListener);
        }else if(searchAdapter && "1".equals(info.getAttention())){
//            view.findViewById(R.id.StatusLayout).setBackgroundResource(R.drawable.gray_15);
//            view.findViewById(R.id.StatusLayout).setEnabled(false);
            ((TextView)view.findViewById(R.id.StatusInfo)).setBackgroundResource(R.drawable.gray_15);
            ((TextView)view.findViewById(R.id.StatusInfo)).setText("已添加");
            ((TextView)view.findViewById(R.id.StatusInfo)).setEnabled(false);
            ((TextView)view.findViewById(R.id.StatusInfo)).setTextColor(Color.parseColor("#777777"));
        }else{
            ((TextView)view.findViewById(R.id.StatusInfo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    follow(position,(TextView)view.findViewById(R.id.StatusInfo));

//                    if(!searchAdapter && !info.getStatus().equals("2")) {
//                        Intent intent = new Intent(mContext, PaySelectActivity.class);
//                        intent.putExtra("merchantID",info.getMerchantID());
//                        mContext.startActivity(intent);
//                    }
                }
            });
        }
        return view;
    }


    private void follow(int index, final TextView tv){
        final MasterDao info = lists.get(index);
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","attention");
        params.put("uid", UserUtil.uid);
        params.put("merchantID",info.getMerchantID());
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setParams(params)
                .execute(new AsyncResponseHandler() {
                    @Override
                    public void onResult(boolean success, Object result, ResponseError error) {
                        if(success){
                            Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                            info.setAttention("1");
                           notifyDataSetChanged();
//                            return;
                        }
                    }
                });
    }
}
