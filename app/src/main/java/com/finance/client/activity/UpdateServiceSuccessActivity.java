package com.finance.client.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.finance.client.R;
import com.finance.library.BaseActivity;

import java.util.Calendar;

/**
 * User : yh
 * Date : 17/9/5
 */

public class UpdateServiceSuccessActivity extends BaseActivity {
    private String price;
    private String projectId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        title = "提现申请";
        setContentView(R.layout.activity_update_success);
        super.onCreate(savedInstanceState);
        long time=System.currentTimeMillis();
        final Calendar mCalendar=Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second=c.get(Calendar.SECOND);
        price= getIntent().getStringExtra("amount");
        projectId=getIntent().getStringExtra("projectId");
        findViewById(R.id.FinishLayout).setOnClickListener(this);
        ((TextView)findViewById(R.id.txt_time)).setText("时间："+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
        if (projectId.equals("100"))
        {
            ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功试订1个月");
        }else if (projectId.equals("200"))
        {
            ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功订购1个月");
        }else if (projectId.equals("300"))
        {
            ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功订购3个月");
        }else if (projectId.equals("400"))
        {
            ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功订购6个月");
        }else if (projectId.equals("500"))
        {
            ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功订购12个月");
        }else if(projectId.equals("600"))
        {
            ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功订购一次性订购");
        }else  if(projectId.equals("1000")){
            ((TextView) findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功订购联系方式");
        }else if (projectId.equals("3000"))
        {
            if (!TextUtils.isEmpty(price)) {
                ((TextView) findViewById(R.id.txt_update_paerson)).setText("恭喜您，app众筹成功，您当前累计消费" + price + "元，当前累计股份" + price + "股币");
            }
        }



//        ((TextView)findViewById(R.id.txt_update_paerson)).setText("恭喜您，已成功增加订购人数"+numOfPerson+"人，当前可订购人数至599人");
//        ((TextView)findViewById(R.id.Content)).setText(content);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId()==R.id.FinishLayout)
        {
            finish();
        }
    }
}
