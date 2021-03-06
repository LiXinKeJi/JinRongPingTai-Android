package com.finance.client.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import com.finance.client.util.Content;
import com.finance.client.util.ImageUtil;
import com.finance.client.util.PermissionUtil;
import com.finance.client.util.ToastUtils;
import com.finance.client.util.UserUtil;
import com.lling.photopicker.PhotoPickerActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * User : yh
 * Date : 17/8/16
 */

public class ActivityBaseInfo extends BaseActivity {
    private String avatar;
    private String address;
    private String categoryInfo, categoryId;
    private JSONArray category;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = "完善资料";
        setContentView(R.layout.activity_base_info);
        super.onCreate(savedInstanceState);
        uid = getIntent().getStringExtra("uid");
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
        if (v.getId() == R.id.CityLayout) {
            chooseRegion();
        } else if (v.getId() == R.id.HeadImg) {
            if (PermissionUtil.ApplyPermissionAlbum(this, 0)) {
                this.takePhoto();
            }
        } else if (v.getId() == R.id.SubmitBtn) {
            this.modifyUserInfo();
        } else if (v.getId() == R.id.HYLayout) {
            this.chooseCategory();
        }
    }

    private void chooseRegion() {
        RegionChooseDialog dialog = new RegionChooseDialog(this);
        dialog.setOnRegionChangeListener(new RegionChooseDialog.onRegionChangeListener() {
            @Override
            public void onChange(String info, String cityId) {
                address = info;
                ((TextView) findViewById(R.id.City)).setText(address);
            }
        });
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 0) {//询问结果
            this.takePhoto();
        } else {//禁止使用权限，询问是否设置允许
            Toast.makeText(this, "需要访问内存卡和拍照权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseCategory() {
        if (category == null) {
            Toast.makeText(this, "无法获取分类数据", Toast.LENGTH_SHORT).show();
            return;
        }
        IndustryChooseDialog dialog = new IndustryChooseDialog(this, category,-1);
        dialog.setOnCategoryChangeListener(new IndustryChooseDialog.onCategoryChangeListener() {
            @Override
            public void onChange(String info, String id) {
                categoryInfo = info;
                categoryId=id;
                ((TextView) findViewById(R.id.Trade)).setText(categoryInfo);
            }
        });
//        dialog.setData(category);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    private void takePhoto() {
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI);
        intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 1);
        startActivityForResult(intent, 5);
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

    private void UploadImage(String path) {
        //ImageLoaderUtil.getInstance().displayImage("file://"+path, (ImageView) findViewById(R.id.HeadImg));
        ((ImageView) findViewById(R.id.HeadImg)).setImageURI(Uri.fromFile(new File(path)));

        avatar = ImageUtil.imageFile2Base64(path);
        //base64 encode

    }

    private void modifyUserInfo() {
        if (TextUtils.isEmpty(avatar)) {
            Toast.makeText(this, "请选择上传头像", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请选择地址信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(categoryInfo)) {
            Toast.makeText(this, "请选择行业分类", Toast.LENGTH_SHORT).show();
            return;
        }
        String nickName = ((EditText) findViewById(R.id.nickName)).getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        sumbitBaseInfo(nickName);
    }

    private void sumbitBaseInfo(String nickName) {
        Map<String, String> params = new HashMap<>();
        final String json = "{\"cmd\":\"completeUserInfo\",\"uid\":\"" + uid + "\",\"avatar\":\"" + avatar + "\"" +
                ",\"nickName\":\"" + nickName + "\",\"industry\":\"" + categoryId + "\",\"address\":\"" + address + "\"}";
        Log.e("完善资料行业id......",categoryId);
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(ActivityBaseInfo.this, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(ActivityBaseInfo.this, "" + obj.getString("resultNote"));
                        return;
                    }
                    UserUtil.saveUid(ActivityBaseInfo.this, uid);
                    startActivity(new Intent(ActivityBaseInfo.this, MainActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestCategory() {
        Map<String, String> params = new HashMap<>();
        final String json = "{\"cmd\":\"getIndustryCategory\",\"uid\":\"" + uid + "\"}";
        params.put("json", json);
        showLoading();
        OkHttpUtils.post().url(Content.DOMAIN).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                ToastUtils.makeText(ActivityBaseInfo.this, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                Log.e("行业分类......",response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("result").equals("1")) {
                        ToastUtils.makeText(ActivityBaseInfo.this, "" + obj.getString("resultNote"));
                        return;
                    }
                    category = obj.getJSONArray("flist");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
