package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.finance.client.R;

/**
 * User : yh
 * Date : 17/9/5
 */

public class AboutActivity extends BaseActivity{
    private String content = "本APP旨在提供一个单项的信息发送和订阅的共享平台，发布者提供个性话的文字内容、照片等信息，订阅者可以根据个人喜好选择关注多个发布者的信息；使用APP可以发布行业内有价值的内容，也可以作为团队内部、或者老师向学员发送通知消息使用的APP。本APP由两个组成：一个共享发布者APP，用户注册后可以选择关注感兴趣的发布者；另一个是共享推送端APP，任何人都可以注册为发布者，认证通过后的发布者可以在这里发布大象共享信息，还可以获得预先设定的酬金。";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "关于我们";
        setContentView(R.layout.activity_about_layout);
        super.onCreate(savedInstanceState);
        ((TextView)findViewById(R.id.Content)).setText(content);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
