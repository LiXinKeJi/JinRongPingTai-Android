package com.finance.client.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.client.activity.ModifyUserInfoActivity;
import com.finance.client.activity.ReNewActivity;
import com.finance.client.activity.SettingActivity;
import com.finance.client.activity.WalletActivity;
import com.finance.client.model.UserInfoDao;
import com.finance.client.model.UserInfoResultDao;
import com.finance.client.util.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.util.Map;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{
    public static UserInfoDao userInfo;
    private RoundedImageView headImg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_layout,null);
        view.findViewById(R.id.SettingLayout).setOnClickListener(this);
        view.findViewById(R.id.WalletLayout).setOnClickListener(this);
        view.findViewById(R.id.XDLayout).setOnClickListener(this);
        view.findViewById(R.id.InfoLayout).setOnClickListener(this);
        headImg = (RoundedImageView) view.findViewById(R.id.mine_HeadImg);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        requestInfo();
    }

    private void requestInfo(){
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","getUserInfo");
        params.put("uid", UserUtil.uid);
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(UserInfoResultDao.class)
                .execute(new AsyncResponseHandler<UserInfoResultDao>() {
                    @Override
                    public void onResult(boolean success, UserInfoResultDao result, ResponseError error) {
                        dismissLoading();
                        if(!success){
//                            Toast.makeText(MineFragment.this.getContext(), error.errorMsg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        userInfo = result.getUserInfo();
                        updateView();
                    }
                });

    }
    private void updateView(){
        if (userInfo!=null&&!TextUtils.isEmpty(userInfo.getNickName())) {
            ((TextView) getView().findViewById(R.id.Name)).setText(userInfo.getNickName());
        }
        if (userInfo!=null&&!TextUtils.isEmpty(userInfo.getUid())) {
            ((TextView) getView().findViewById(R.id.ID)).setText("ID号: " + userInfo.getUid());
        }
        if (userInfo!=null&&!TextUtils.isEmpty(userInfo.getAvatar())) {
            ImageLoaderUtil.getInstance().displayImage(userInfo.getAvatar(),headImg);
        }else {
            headImg.setImageResource(R.drawable.ic_launcher);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.SettingLayout){
            Intent intent = new Intent(this.getContext(), SettingActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.XDLayout){

            Intent intent = new Intent(this.getContext(), ReNewActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.WalletLayout){
            Intent intent = new Intent(this.getContext(), WalletActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.InfoLayout){
            Intent intent = new Intent(this.getContext(), ModifyUserInfoActivity.class);
            intent.putExtra("userinfo",userInfo);
            startActivity(intent);
        }
    }
}
