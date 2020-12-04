package com.jxd.fc.fuzhu.JxdUtils;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import java.io.File;
import android.content.Context;

public class Data {

    //默认 应用包名
    public static String pkgName_def = "com.jxd.fc";
    //默认 备份路径
    public static String BakPath_def = Environment.getExternalStorageDirectory().getAbsolutePath() + "/backup/.jxd";
    //游戏数据目录
    public static String RootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.jxd.fc";
    //角色数据目录
    public static String jsPath = RootPath + File.separator + "js";

    //获取角色列表
    public static ListAdapter getJSlist(Context context) {
        ListAdapter adapter;
        
        File file = new File(jsPath);
        if (!file.exists() || !file.isDirectory() || file.list().length <= 0) {
            adapter = null;
        } else {
            String[] files=file.list();
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, files);
        }
        return adapter;
    }
}
