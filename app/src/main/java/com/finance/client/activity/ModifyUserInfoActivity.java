package com.finance.client.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.common.IndustryChooseDialog;
import com.finance.client.common.RegionChooseDialog;
import com.finance.client.model.ModifyAvatarResultDao;
import com.finance.client.model.UserInfoDao;
import com.finance.client.util.Content;
import com.finance.client.util.ImageUtil;
import com.finance.client.util.PermissionUtil;
import com.finance.library.BaseActivity;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.lling.photopicker.PhotoPickerActivity;
import com.yhrun.alchemy.Util.ImageLoaderUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;

/**
 * User : yh
 * Date : 17/9/13
 */

public class ModifyUserInfoActivity extends BaseActivity{
    private TextView rightBtn;
    private String avatar = "",categoryInfo = "",address;
    private JSONArray category;
    private TextView txtSign;
    private UserInfoDao userinfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "编辑个人信息";
        setContentView(R.layout.activity_modify_user_info);
        super.onCreate(savedInstanceState);
        rightBtn = (TextView) findViewById(R.id.RightBtnText);
        rightBtn.setText("保存");
        rightBtn.setVisibility(View.VISIBLE);
        userinfo= (UserInfoDao) getIntent().getSerializableExtra("userinfo");
        txtSign=(TextView)findViewById(R.id.Sign);
        findViewById(R.id.HeadImg).setOnClickListener(this);
        findViewById(R.id.HYLayout).setOnClickListener(this);
        findViewById(R.id.CityLayout).setOnClickListener(this);
        findViewById(R.id.SignLayout).setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        updateView(userinfo);
        requestCategory();
    }

    private void updateView(UserInfoDao userinfo) {
        if (userinfo!=null) {
            ImageLoaderUtil.getInstance().displayImage(userinfo.getAvatar(), (ImageView) findViewById(R.id.HeadImg));
            ((TextView) findViewById(R.id.nickName)).setText(userinfo.getNickName());
            ((TextView) findViewById(R.id.ID)).setText(userinfo.getUid());
            ((TextView) findViewById(R.id.Trade)).setText(userinfo.getIndustry());
            ((TextView) findViewById(R.id.City)).setText(userinfo.getAddress());
            ((TextView) findViewById(R.id.Phone)).setText(userinfo.getPhoneNum());
            ((TextView) findViewById(R.id.Wechat)).setText(userinfo.getWeChat());
            ((TextView) findViewById(R.id.Sign)).setText(userinfo.getSignature());
        }
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


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.HeadImg:
                if (PermissionUtil.ApplyPermissionAlbum(this, 0)) {
                    this.takePhoto();
                }
                break;
            case R.id.HYLayout:
                this.chooseCategory();
                break;
            case R.id.CityLayout:
                chooseRegion();
                break;
            case R.id.SignLayout:
                Intent intent=new Intent(ModifyUserInfoActivity.this,SignActivity.class);
                intent.putExtra("sign", ((TextView)findViewById(R.id.Sign)).getText().toString());
                startActivityForResult(intent,1000);
                break;
            case R.id.RightBtnText:
               submit();
                break;
            default:break;
        }
    }

    private void submit() {
        String nickName = ((TextView)findViewById(R.id.nickName)).getText().toString().trim();
        String address = ((TextView)findViewById(R.id.City)).getText().toString().trim();
        String industry = ((TextView)findViewById(R.id.Trade)).getText().toString().trim();
        String weChat = ((TextView)findViewById(R.id.Wechat)).getText().toString().trim();
        String signature = ((TextView)findViewById(R.id.Sign)).getText().toString().trim();
        editUserInfo(nickName,address,industry,weChat,signature);
    }

    private void editUserInfo(String nickName, String address, String industry, String weChat,String signature) {
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        String json = "{\"cmd\":\"editUserInfo\",\"uid\":\""+UserUtil.uid+"\",\"avatar\":\""+avatar+"\"" +
                ",\"address\":\""+address+"\",\"nickName\":\""+nickName+"\",\"industry\":\""+categoryInfo+"\"" +
                ",\"weChat\":\""+weChat+"\",\"signature\":\""+signature+"\"}";
        params.put("json",json);
        Log.i("666", "editUserInfo: " + avatar);
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(ModifyUserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissLoading();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.i("6666", "onResponse: " + response);
                Gson gson = new Gson();
                dismissLoading();
                ModifyAvatarResultDao modifyAvatarResultDao = gson.fromJson(response,ModifyAvatarResultDao.class);
                if (modifyAvatarResultDao.getResult().equals("1")){
                    Toast.makeText(ModifyUserInfoActivity.this,modifyAvatarResultDao.getResultNote(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功" , Toast.LENGTH_SHORT).show();
                UserInfoDao userinfo = modifyAvatarResultDao.getUserInfo();
                updateView(userinfo);
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
//                            Toast.makeText(ModifyUserInfoActivity.this, "获取分类数据成功", Toast.LENGTH_SHORT).show();
                            try {
                                category = new JSONObject(result).getJSONArray("flist");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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


    private void chooseCategory(){
        if(category == null){
            Toast.makeText(this, "无法获取分类数据", Toast.LENGTH_SHORT).show();
            return;
        }
        IndustryChooseDialog dialog = new IndustryChooseDialog(this,category);
        dialog.setOnCategoryChangeListener(new IndustryChooseDialog.onCategoryChangeListener() {
            @Override
            public void onChange(String info,String id) {
                categoryInfo = id;
                ((TextView)findViewById(R.id.Trade)).setText(info);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 5) {
            ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
            if (result == null || result.isEmpty()) {
                return;
            }
            UploadImage(result.get(0));
        }
        if (requestCode==1000&resultCode==1002)
        {
            String str=data.getStringExtra("sign");
            txtSign.setText(str);
        }
    }


    private void UploadImage(String path){
        ((ImageView)findViewById(R.id.HeadImg)).setImageURI(Uri.fromFile(new File(path)));
        avatar = ImageUtil.imageFile2Base64(path);
    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
