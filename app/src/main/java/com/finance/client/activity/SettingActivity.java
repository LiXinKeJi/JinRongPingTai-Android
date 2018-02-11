package com.finance.client.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.util.Content;
import com.finance.client.util.Utils;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;

import org.json.JSONObject;

import java.util.Map;

/**
 * User : yh
 * Date : 17/8/17
 */

public class SettingActivity extends BaseActivity{
    private ImageView ic_spot;

    private boolean isDown=false;
    private String version_name, updata_url;//获取的版本名,下载地址
    private int version_code;//获取的版本号
    private ImageView PushBtn,MsgBtn,SysBtn;
    private boolean push=true,msg=true,sys=true;
//    private SwitchButton PushBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "设置";
        setContentView(R.layout.activity_setting);
        super.onCreate(savedInstanceState);
        ic_spot=(ImageView)findViewById(R.id.ic_spot);
        findViewById(R.id.ModifyPwdLayout).setOnClickListener(this);
        findViewById(R.id.PhoneBindLayout).setOnClickListener(this);
        findViewById(R.id.RuleLayout).setOnClickListener(this);
        findViewById(R.id.PushBtn).setOnClickListener(this);
        findViewById(R.id.MsgBtn).setOnClickListener(this);
        findViewById(R.id.SysBtn).setOnClickListener(this);
        findViewById(R.id.SuggestLayout).setOnClickListener(this);
        findViewById(R.id.UpdateLayout).setOnClickListener(this);
        findViewById(R.id.AboutLayout).setOnClickListener(this);
        findViewById(R.id.LogoutBtn).setOnClickListener(this);
        PushBtn=(ImageView) findViewById(R.id.PushBtn);
        MsgBtn=(ImageView)findViewById(R.id.MsgBtn);
        SysBtn=(ImageView)findViewById(R.id.SysBtn);
        getUpdata();
    }




    private void showUpdateDialog() {
        final AlertDialog builder = new AlertDialog.Builder(this, R.style.Dialog).create(); //
        builder.show();
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.dialog_update, null);
        builder.getWindow().setContentView(view);
        builder.setCancelable(true);//点击返回消失
        builder.setCanceledOnTouchOutside(false);//点击屏幕不消失

        TextView tv_titles = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_yes = (TextView) view.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) view.findViewById(R.id.tv_no);

        tv_titles.setText("当前版本：" + Utils.getVersionName(this) + "，发现新版本："
                + version_name + "，是否更新？");

        Window dialogWindow = builder.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//显示在底部
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始下载
                builder.dismiss();
                Uri uri = Uri.parse(updata_url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    private void getUpdata() {
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd", "getUpdata");
        params.put("type", "1");
        showLoading();
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if (success) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (obj.getString("result").equals("1")) {
                                    Toast.makeText(SettingActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                                    return;
                                }
//                                Toast.makeText(SettingActivity.this, obj.getString("resultNote"), Toast.LENGTH_SHORT).show();
                                version_name=obj.getString("versionName");
                                version_code = Integer.valueOf(obj.getString("versionNumber"));
                                if (!TextUtils.isEmpty(String.valueOf(version_code))&&version_code > Utils.getVersionCode(SettingActivity.this)) {
                                    updata_url=obj.getString("updataAddress");
                                    ic_spot.setVisibility(View.VISIBLE);
                                    isDown = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ModifyPwdLayout){
            Intent intent = new Intent(this,ModifyPwdActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.PhoneBindLayout){
            Intent intent = new Intent(this,PhoneBindActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.RuleLayout){
            Intent intent = new Intent(this,RuleDescriptionActivity.class);
            startActivity(intent);

        }else if(v.getId() == R.id.PushBtn){
            push=!push;
            if (push)
            {
                PushBtn.setImageResource(R.drawable.kai);
            }else
            {
                PushBtn.setImageResource(R.drawable.off);
            }

        }else if(v.getId() == R.id.MsgBtn){
            msg=!msg;
            if (msg)
            {
                MsgBtn.setImageResource(R.drawable.kai);
            }else
            {
                MsgBtn.setImageResource(R.drawable.off);
            }

        }else if(v.getId() == R.id.SysBtn){
            sys=!sys;
            if (sys)
            {
                SysBtn.setImageResource(R.drawable.kai);
            }else
            {
                SysBtn.setImageResource(R.drawable.off);
            }

        }else if(v.getId() == R.id.SuggestLayout){
            Intent intent = new Intent(this,FeedbackActivity.class);
            startActivity(intent);

        }else if(v.getId() == R.id.UpdateLayout){
            //版本更新
            if (isDown) {
                showUpdateDialog();
            } else {
                Toast.makeText(this,"已是最新版本",Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.AboutLayout){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.LogoutBtn){
            UserUtil.logout(this);
            Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
