package com.jxd.fc.fuzhu.JxdUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.CommonUtils.FileUtils;
import com.CommonUtils.ToolUtils;
import java.io.File;
import java.io.IOException;

public class PlugDialog {

    public static void GetXutiangujing(final Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle("获取虚天古镜")
            .setMessage("在战斗时，虚天古镜可用于查看探索到的妖兽属性。但游戏中没有途径获得该道具，本插件可直接帮助角色获得虚天古镜。")
            .setPositiveButton("选择角色", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dia, int which) {
                    if (Data.getJSlist(context) == null) {
                        ToolUtils.toast(context, "无角色");
                    } else {
                        //选择角色弹窗
                        AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("选择角色")
                            .setAdapter(Data.getJSlist(context), new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    //写入数据
                                    String js = Data.getJSlist(context).getItem(p2).toString();//角色
                                    String path = Data.jsPath + File.separator + js + "/储物/物品/虚天古镜";
                                    try {
                                        FileUtils.saveToFile(path, "1", "utf-8");
                                        ToolUtils.toast(context, "选择角色：" + js);
                                    } catch (IOException e) {}
                                }
                            })
                            .create();
                        dialog.show();
                    }
                }
            })
            .setNegativeButton("取消", null)
            .create();
        dialog.show();
    }



}//class
