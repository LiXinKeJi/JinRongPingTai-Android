package com.finance.client.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.activity.ModifyUserInfoActivity;
import com.finance.client.activity.ReNewActivity;
import com.finance.client.activity.SettingActivity;
import com.finance.client.activity.WalletActivity;
import com.finance.client.model.UserInfoDao;
import com.finance.client.model.UserInfoResultDao;
import com.finance.client.util.Content;
import com.finance.client.util.UserUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yhrun.alchemy.Util.ImageLoaderUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{
    private View view;
    public static UserInfoDao userInfo;
    private RoundedImageView headImg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mine_layout,null);
        initView();
        return view;
    }

    private void initView() {
        view.findViewById(R.id.SettingLayout).setOnClickListener(this);
        view.findViewById(R.id.WalletLayout).setOnClickListener(this);
        view.findViewById(R.id.XDLayout).setOnClickListener(this);
        view.findViewById(R.id.InfoLayout).setOnClickListener(this);
        headImg = (RoundedImageView) view.findViewById(R.id.mine_HeadImg);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestInfo();
    }

    private void requestInfo(){
        Map<String,String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"getUserInfo\",\"uid\":\""+ UserUtil.uid+"\"}";
        params.put("json",json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("6666", "onResponse: " + response);
                Gson gson = new Gson();
                dismissLoading();
                UserInfoResultDao userInfoResultDao = gson.fromJson(response,UserInfoResultDao.class);
                if (userInfoResultDao.getResult().equals("1")){
                    Toast.makeText(getActivity(),userInfoResultDao.getResultNote(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userInfo = userInfoResultDao.getUserInfo();
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getNickName())) {
                    ((TextView) getView().findViewById(R.id.Name)).setText(userInfo.getNickName());
                }
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getUid())) {
                    ((TextView) getView().findViewById(R.id.ID)).setText("ID号: " + userInfo.getUid());
                }
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                    ImageLoaderUtil.getInstance().displayImage(userInfo.getAvatar(),headImg);
                }else {
                    headImg.setImageResource(R.drawable.ic_launcher);
                }
            }
        });

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
