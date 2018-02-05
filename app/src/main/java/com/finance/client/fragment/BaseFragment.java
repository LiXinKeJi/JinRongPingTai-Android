package com.finance.client.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.yhrun.alchemy.Common.LoadingDialog;

/**
 * User : yh
 * Date : 17/8/14
 */

public class BaseFragment extends Fragment{
    protected LoadingDialog dialog ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new LoadingDialog(getContext());
    }

    protected void showLoading(){
        if(dialog == null){
            dialog = new LoadingDialog(getContext());
        }
        dialog.show();
    }

    protected void dismissLoading(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}
