package com.finance.client.util;

import android.content.Context;
import android.content.Intent;


import com.finance.client.photoView.imagepage.ImagePagerActivity;

import java.io.Serializable;
import java.util.List;


/**
 * 查看图片
 * Created by Slingge on 2017/7/4 0004.
 */

public class SeePhotoViewUtil {

    public static void toPhotoView(Context context, List<String> list, int position) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (Serializable) list);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }


}
