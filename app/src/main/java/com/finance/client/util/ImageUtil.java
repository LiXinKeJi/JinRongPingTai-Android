package com.finance.client.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.google.common.base.Strings;
import com.yhrun.alchemy.Util.ImageLoaderUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * User : yh
 * Date : 16/12/6
 */

public class ImageUtil {
    public static void displayImage(String url,ImageView imgView){
        if(Strings.isNullOrEmpty(url)){
            return;
        }
        ImageLoaderUtil.getInstance().displayImage(url,imgView);
    }

    public static String imageFile2Base64(String file){
        try {
            FileInputStream input = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inSampleSize = 6;
            final Bitmap photo = BitmapFactory.decodeStream(input, null, options);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] b = stream.toByteArray();
            String dst = Base64.encodeToString(b, Base64.DEFAULT);
            return dst;
        }catch (Exception e){
            return null;
        }
    }
//    public static void ImageShow(Context mContext, ArrayList<String> lists){
//        Intent intent = new Intent(mContext, ImagePager.class);
//        intent.putExtra("ImageList",lists);
//        intent.putExtra("index",0);
//        mContext.startActivity(intent);
//
//    }
}
