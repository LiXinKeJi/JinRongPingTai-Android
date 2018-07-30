package com.finance.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.finance.client.MyApplication;
import com.finance.client.R;
import com.finance.client.http.StartImageHttp;
import com.finance.client.model.StartImageModel;
import com.finance.client.util.SPUtil;
import com.finance.client.util.UserUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity implements StartImageHttp.ImageCallBack {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        image = (ImageView) findViewById(R.id.image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StartImageHttp.getImage(this);
    }


    @Override
    public void image(final StartImageModel model) {
        toTask(model);
        if (model == null) {
            return;
        }

        ImageLoader.getInstance().displayImage(model.image, image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RuleDescriptionActivity.class);
                intent.putExtra("url", model.url);
                startActivity(intent);
                timer.cancel();
            }
        });
    }

    private Timer timer;

    private void toTask(final StartImageModel model) {
        final String uid = UserUtil.getUid(this);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean isFirst = SPUtil.getBoolean(StartActivity.this, "isFirst", false);
                if (!isFirst) {
                    Intent intent = new Intent(StartActivity.this, WelComeActivity.class);
                    intent.putExtra("model", model);
                    startActivity(intent);
                    finish();
                    return;
                }
                if (!TextUtils.isEmpty(uid)) {
                    MyApplication.uId = uid;
                    MyApplication.openActivity(StartActivity.this, MainActivity.class);
                } else {
                    MyApplication.openActivity(StartActivity.this, LoginActivity.class);
                }
                finish();
            }
        };
        timer.schedule(task, 3000);//第一次执行前的毫秒延迟时间。在随后的执行之间毫秒内的时间
    }

}
