package com.finance.client.util;

import android.util.Log;

/**
 * Created by Slingge on 2018/7/4 0004.
 */

public class abLog {

    public static void e(String tag, String text) {
        if (text.length() > 4000) {
            int count = 0;
            int i = 0;
            while (i < text.length()) {
                count++;
                if (i + 4000 < text.length()) {
                    Log.e(tag + count, text.substring(i, i + 4000));
                } else {
                    Log.e(tag + count, text.substring(i, text.length()));
                }
                i += 4000;
            }
        }
    }

}
