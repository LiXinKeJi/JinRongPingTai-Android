package com.finance.client.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.finance.client.MyApplication;
import com.finance.client.R;
import com.finance.client.activity.LoginActivity;
import com.finance.client.activity.RuleDescriptionActivity;
import com.finance.client.model.StartImageModel;
import com.finance.client.util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Slingge on 2018/6/23 0023.
 */

public class WelComeFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        final StartImageModel.imgBean model = (StartImageModel.imgBean) getArguments().getSerializable("model");
        final ImageView image = (ImageView) view.findViewById(R.id.iv_bg);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RuleDescriptionActivity.class);
                intent.putExtra("url", model.url);
                startActivity(intent);
            }
        });
        TextView tv_in = (TextView) view.findViewById(R.id.tv_in);

        if (getArguments().getInt("flag", -1) == 0) {
            tv_in.setVisibility(View.VISIBLE);
            tv_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.openActivity(getActivity(), LoginActivity.class);
                    SPUtil.putBoolean(getActivity(), "isFirst", true);
                    getActivity().finish();
                }
            });
        } else {
            tv_in.setVisibility(View.GONE);
        }

        ImageLoader.getInstance().displayImage(model.image, image);
    }

}
