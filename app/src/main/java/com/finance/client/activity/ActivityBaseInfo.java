package com.finance.client.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.common.IndustryChooseDialog;
import com.finance.client.common.RegionChooseDialog;
import com.finance.client.util.ImageUtil;
import com.finance.client.util.PermissionUtil;
import com.finance.library.BaseActivity;
import com.finance.library.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.lling.photopicker.PhotoPickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


/**
 * User : yh
 * Date : 17/8/16
 */

public class ActivityBaseInfo extends BaseActivity{
    private String avatar;
    private String address;
    private String categoryInfo;
    private JSONArray category;
    private String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "完善资料";
        setContentView(R.layout.activity_base_info);
        super.onCreate(savedInstanceState);
        uid=getIntent().getStringExtra("uid");
        findViewById(R.id.CityLayout).setOnClickListener(this);
        findViewById(R.id.HeadImg).setOnClickListener(this);
        findViewById(R.id.SubmitBtn).setOnClickListener(this);
        findViewById(R.id.HYLayout).setOnClickListener(this);
        this.requestCategory();
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.CityLayout){
            chooseRegion();
        }else if(v.getId() == R.id.HeadImg){
            if (PermissionUtil.ApplyPermissionAlbum(this, 0)) {
                this.takePhoto();
            }
        }else if(v.getId() == R.id.SubmitBtn){
            this.modifyUserInfo();
        }else if(v.getId() == R.id.HYLayout){
            this.chooseCategory();
        }
    }

    private void chooseRegion(){
        RegionChooseDialog dialog = new RegionChooseDialog(this);
        dialog.setOnRegionChangeListener(new RegionChooseDialog.onRegionChangeListener() {
            @Override
            public void onChange(String info, String cityId) {
                address = info;
                ((TextView)findViewById(R.id.City)).setText(address);
            }
        });
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 0) {//询问结果
            this.takePhoto();
        } else {//禁止使用权限，询问是否设置允许
            Toast.makeText(this,"需要访问内存卡和拍照权限",Toast.LENGTH_SHORT).show();;
        }
    }

    private void chooseCategory(){
        if(category == null){
            Toast.makeText(this, "无法获取分类数据", Toast.LENGTH_SHORT).show();
            return;
        }
        IndustryChooseDialog dialog = new IndustryChooseDialog(this,category);
        dialog.setOnCategoryChangeListener(new IndustryChooseDialog.onCategoryChangeListener() {
            @Override
            public void onChange(String info,String id) {
                categoryInfo = info;
                ((TextView)findViewById(R.id.Trade)).setText(categoryInfo);
            }
        });
//        dialog.setData(category);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    private void takePhoto(){
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI);
        intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN,1 );
        startActivityForResult(intent,5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {
            return;
        }
        ArrayList<String> result = intent.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
        if (result == null || result.isEmpty()) {
            return;
        }
        this.UploadImage(result.get(0));
    }

    private void UploadImage(String path){
        //ImageLoaderUtil.getInstance().displayImage("file://"+path, (ImageView) findViewById(R.id.HeadImg));
        ((ImageView)findViewById(R.id.HeadImg)).setImageURI(Uri.fromFile(new File(path)));

        avatar = ImageUtil.imageFile2Base64(path);
        //base64 encode

    }

    private void modifyUserInfo(){
        if(TextUtils.isEmpty(avatar)){
            Toast.makeText(this, "请选择上传头像", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(this, "请选择地址信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(categoryInfo)){
            Toast.makeText(this, "请选择行业分类", Toast.LENGTH_SHORT).show();
            return;
        }
        String nickName = ((EditText)findViewById(R.id.nickName)).getText().toString();
        if(TextUtils.isEmpty(nickName)){
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
//        String uid = getIntent().getStringExtra("uid");
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","completeUserInfo");
        params.put("uid",uid);
        params.put("avatar",avatar);
        params.put("nickName",nickName);
        params.put("industry",categoryInfo);
        params.put("address",address);
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        UserUtil.saveUid(ActivityBaseInfo.this,uid);
                        startActivity(new Intent(ActivityBaseInfo.this,MainActivity.class));
                        finish();
                    }
                });

    }

    private void requestCategory(){
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","getIndustryCategory");
        params.put("uid", UserUtil.uid);
        AsyncClient.Get()
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(String.class)
                .execute(new AsyncResponseHandler<String>() {
                    @Override
                    public void onResult(boolean success, String result, ResponseError error) {
                        dismissLoading();
                        if(success){
                            Toast.makeText(ActivityBaseInfo.this, "获取分类数据成功", Toast.LENGTH_SHORT).show();
                            try {
                                category = new JSONObject(result).getJSONArray("flist");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
}
