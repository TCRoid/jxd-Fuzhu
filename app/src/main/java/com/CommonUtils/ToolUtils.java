package com.CommonUtils;

import android.content.Context;
import android.widget.Toast;

public class ToolUtils {

    //Toast提示
    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


    
}
