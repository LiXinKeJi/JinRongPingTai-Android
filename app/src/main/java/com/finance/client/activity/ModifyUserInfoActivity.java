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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.client.R;
import com.finance.client.common.IndustryChooseDialog;
import com.finance.client.common.RegionChooseDialog;
import com.finance.client.model.ModifyAvatarResultDao;
import com.finance.client.model.UserInfoDao;
import com.finance.client.util.ImageUtil;
import com.finance.client.util.PermissionUtil;
import com.finance.library.BaseActivity;
import com.finance.library.Content;
import com.finance.library.Util.UserUtil;
import com.finance.library.network.AsyncClient;
import com.finance.library.network.AsyncResponseHandler;
import com.google.common.collect.Maps;
import com.lling.photopicker.PhotoPickerActivity;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * User : yh
 * Date : 17/9/13
 */

public class ModifyUserInfoActivity extends BaseActivity{
    private TextView rightBtn;
    private String avatar,categoryInfo,address;
    private JSONArray category;
    private TextView txtSign;
    private UserInfoDao userinfo;
    private String avatarFlag="0",nicknameFlag="0",signatureFlag="0",tradeFlag="0",areaFlag="0",wechatFlag="0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "编辑个人信息";
        setContentView(R.layout.activity_modify_user_info);
        super.onCreate(savedInstanceState);
        rightBtn = (TextView) findViewById(R.id.RightBtnText);
        rightBtn.setText("保存");
        rightBtn.setVisibility(View.VISIBLE);
        userinfo= (UserInfoDao) getIntent().getSerializableExtra("userinfo");
//        avatar=userinfo.getAvatar();
        txtSign=(TextView)findViewById(R.id.Sign);
        findViewById(R.id.HeadImg).setOnClickListener(this);
        findViewById(R.id.HYLayout).setOnClickListener(this);
        findViewById(R.id.CityLayout).setOnClickListener(this);
        findViewById(R.id.SignLayout).setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        updateView();
        requestCategory();
        //rightBtn.setVisibility(View.VISIBLE);
    }

    private void updateView() {
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
                if (!TextUtils.isEmpty(avatar)) {
                    modifyUserAvatar();
                }else
                {
                    avatarFlag="1";
                }
                if (!TextUtils.isEmpty(((TextView)findViewById(R.id.City)).getText().toString())) {
                    modifyAddress();
                }else
                {
                    areaFlag="1";
                }
                if (!TextUtils.isEmpty(((TextView)findViewById(R.id.Trade)).getText().toString())) {
                    modifyIndustry();
                }else
                {
                    tradeFlag="1";
                }
                if (!TextUtils.isEmpty(((TextView)findViewById(R.id.nickName)).getText().toString())) {
                    modifyNickName();
                }
                else
                {
                    nicknameFlag="1";
                }
                if (!TextUtils.isEmpty(((TextView)findViewById(R.id.Sign)).getText().toString())) {
                    modifysignature();
                }else
                {
                    signatureFlag="1";
                }
                if (!TextUtils.isEmpty(((TextView)findViewById(R.id.Wechat)).getText().toString())) {
                    modifyWeChat();
                }else
                {
                    wechatFlag="1";
                }
                break;
            default:break;
        }
    }



    private void modifyUserAvatar(){
//        if(TextUtils.isEmpty(avatar)){
//            Toast.makeText(this, "请选择头像", Toast.LENGTH_SHORT).show();
//            return;
//        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changeAvatar");
        params.put("uid", UserUtil.uid);
        params.put("avatar",avatar);
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(ModifyAvatarResultDao.class)
                .execute(new AsyncResponseHandler<ModifyAvatarResultDao>() {
                    @Override
                    public void onResult(boolean success, ModifyAvatarResultDao result, ResponseError error) {
                        dismissLoading();
                        if (success) {
                            if (result.getResult().equals("1")) {
                                Toast.makeText(ModifyUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            avatarFlag="1";
                            userinfo = result.getUserInfo();
                            if (avatarFlag.equals("1")&&signatureFlag.equals("1")&&wechatFlag.equals("1")&&areaFlag.equals("1")&&tradeFlag.equals("1")&&nicknameFlag.equals("1"))
                            {
                                Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功" , Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
//                            updateView();
                    }
                });

    }


    private void modifyNickName(){
        if(TextUtils.isEmpty(((TextView)findViewById(R.id.nickName)).getText().toString())){
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changeNickName");
        params.put("uid", UserUtil.uid);
        params.put("nickName",((TextView)findViewById(R.id.nickName)).getText().toString());
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(ModifyAvatarResultDao.class)
                .execute(new AsyncResponseHandler<ModifyAvatarResultDao>() {
                    @Override
                    public void onResult(boolean success, ModifyAvatarResultDao result, ResponseError error) {
                        dismissLoading();

                        if (result.getResult().equals("1")) {
                            Toast.makeText(ModifyUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        nicknameFlag="1";
                        userinfo=result.getUserInfo();
                        if (avatarFlag.equals("1")&&signatureFlag.equals("1")&&wechatFlag.equals("1")&&areaFlag.equals("1")&&tradeFlag.equals("1")&&nicknameFlag.equals("1"))
                        {
                            Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                            updateView();

                    }
                });

    }
    private void modifyIndustry(){
        if(TextUtils.isEmpty(((TextView)findViewById(R.id.Trade)).getText().toString())){
            Toast.makeText(this, "请选择行业", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changeIndustry");
        params.put("uid", UserUtil.uid);
        params.put("industry",((TextView)findViewById(R.id.Trade)).getText().toString());
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(ModifyAvatarResultDao.class)
                .execute(new AsyncResponseHandler<ModifyAvatarResultDao>() {
                    @Override
                    public void onResult(boolean success, ModifyAvatarResultDao result, ResponseError error) {
                        dismissLoading();
                        if (result.getResult().equals("1")) {
                            Toast.makeText(ModifyUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        tradeFlag="1";
                        userinfo=result.getUserInfo();
                        if (avatarFlag.equals("1")&&signatureFlag.equals("1")&&wechatFlag.equals("1")&&areaFlag.equals("1")&&tradeFlag.equals("1")&&nicknameFlag.equals("1"))
                        {
                            Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功" , Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                            updateView();

                    }
                });

    }
    private void modifyAddress(){
        if(TextUtils.isEmpty(((TextView)findViewById(R.id.Trade)).getText().toString())){
            Toast.makeText(this, "请选择城市", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changeAddress");
        params.put("uid", UserUtil.uid);
        params.put("address",((TextView)findViewById(R.id.City)).getText().toString());
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(ModifyAvatarResultDao.class)
                .execute(new AsyncResponseHandler<ModifyAvatarResultDao>() {
                    @Override
                    public void onResult(boolean success, ModifyAvatarResultDao result, ResponseError error) {
                        dismissLoading();

                        if (result.getResult().equals("1")) {
                            Toast.makeText(ModifyUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        areaFlag="1";
                        userinfo=result.getUserInfo();
                        if (avatarFlag.equals("1")&&signatureFlag.equals("1")&&wechatFlag.equals("1")&&areaFlag.equals("1")&&tradeFlag.equals("1")&&nicknameFlag.equals("1"))
                        {
                            Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功" , Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                            updateView();

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
    private void modifyWeChat(){
        if(TextUtils.isEmpty(((TextView)findViewById(R.id.Trade)).getText().toString())){
            Toast.makeText(this, "请填写微信号", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changeWeChat");
        params.put("uid", UserUtil.uid);
        params.put("weChat",((TextView)findViewById(R.id.Wechat)).getText().toString());
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(ModifyAvatarResultDao.class)
                .execute(new AsyncResponseHandler<ModifyAvatarResultDao>() {
                    @Override
                    public void onResult(boolean success, ModifyAvatarResultDao result, ResponseError error) {
                        dismissLoading();

                        if (result.getResult().equals("1")) {
                            Toast.makeText(ModifyUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        wechatFlag="1";
                        userinfo=result.getUserInfo();
                        if (avatarFlag.equals("1")&&signatureFlag.equals("1")&&wechatFlag.equals("1")&&areaFlag.equals("1")&&tradeFlag.equals("1")&&nicknameFlag.equals("1"))
                        {
                            Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功" , Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                            updateView();

                    }
                });

    }
    private void modifysignature(){
        if(TextUtils.isEmpty(((TextView)findViewById(R.id.Trade)).getText().toString())){
            Toast.makeText(this, "请填写微信号", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        Map<String,String> params = Maps.newHashMap();
        params.put("cmd","changeSignature");
        params.put("uid", UserUtil.uid);
        params.put("signature",((TextView)findViewById(R.id.Sign)).getText().toString());
        AsyncClient.Post()
                .setContext(this)
                .setHost(Content.DOMAIN)
                .setParams(params)
                .setReturnClass(ModifyAvatarResultDao.class)
                .execute(new AsyncResponseHandler<ModifyAvatarResultDao>() {
                    @Override
                    public void onResult(boolean success, ModifyAvatarResultDao result, ResponseError error) {
                        dismissLoading();

                        if (result.getResult().equals("1")) {
                            Toast.makeText(ModifyUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        signatureFlag="1";
                        userinfo=result.getUserInfo();
                        if (avatarFlag.equals("1")&&signatureFlag.equals("1")&&wechatFlag.equals("1")&&areaFlag.equals("1")&&tradeFlag.equals("1")&&nicknameFlag.equals("1"))
                        {
                            Toast.makeText(ModifyUserInfoActivity.this,"编辑个人信息成功" , Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                            updateView();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 5) {
            ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
            if (result == null || result.isEmpty()) {
                return;
            }
            this.UploadImage(result.get(0));
        }
        if (requestCode==1000&resultCode==1002)
        {
            String str=data.getStringExtra("sign");
            txtSign.setText(str);
        }
    }


    private void UploadImage(String path){
        //ImageLoaderUtil.getInstance().displayImage("file://"+path, (ImageView) findViewById(R.id.HeadImg));
        ((ImageView)findViewById(R.id.HeadImg)).setImageURI(Uri.fromFile(new File(path)));

        avatar = ImageUtil.imageFile2Base64(path);
//        modifyUserAvatar();
        //base64 encode

    }

    @Override
    public void onLeftIconClick() {
        finish();
    }
}
