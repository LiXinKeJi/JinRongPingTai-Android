package com.finance.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yhrun.alchemy.Common.LoadingDialog;

/**
 * User : yh
 * Date : 17/6/21
 */

public abstract class BaseActivity extends Activity implements View.OnClickListener{
    protected  String title = "标题";
    protected LoadingDialog dialog ;
    protected RelativeLayout NavLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView)findViewById(R.id.Title)).setText(title);
        NavLayout = (RelativeLayout) findViewById(R.id.NavLayout);
        findViewById(R.id.BackImgBtn).setOnClickListener(this);
        dialog = new LoadingDialog(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.BackImgBtn){
            this.onLeftIconClick();
        }
    }

    public abstract void onLeftIconClick();

    protected void showLoading(){
        if(dialog == null){
            dialog = new LoadingDialog(this);
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
