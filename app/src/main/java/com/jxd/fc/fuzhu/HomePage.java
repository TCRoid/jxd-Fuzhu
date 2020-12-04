package com.jxd.fc.fuzhu;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.CommonUtils.AppUtils;
import com.CommonUtils.FileUtils;
import com.jxd.fc.fuzhu.JxdUtils.Data;
import com.jxd.fc.fuzhu.JxdUtils.PlugDialog;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePage extends Fragment {

    //数据目录名，数据路径，实时路径，apk路径
    private String DataName,apkPath;
    //CardView 按钮
    private CardView plug,setting,help,info;
    //线程 进度条相关
    private ProgressDialog ProDialog;
    private Runnable runnable;

    private SharedPreferences SPs;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.page_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ////////***变量初始化***//
        SPs = getActivity().getSharedPreferences("setting", Context.MODE_WORLD_READABLE);

        DataName = "com.jxd.fc";
        apkPath = getActivity().getExternalCacheDir().getAbsolutePath() + "/jxd.apk";//cache目录下的apk安装文件

        plug = (CardView) getActivity().findViewById(R.id.page_home_puzzle);
        setting = (CardView) getActivity().findViewById(R.id.page_home_setting);
        help = (CardView) getActivity().findViewById(R.id.page_home_help);
        info = (CardView) getActivity().findViewById(R.id.page_home_infomation);
        ////////***变量初始化***结束***//

        //插件
        plug.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View p1) {
                    PlugDialog();
                }
            });
        //设置
        setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SettingDialog();
                }
            });
        //帮助
        help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HelpDialog();
                }
            });
        //关于
        info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InfoDialog();
                }
            });



        //启动游戏 悬浮按钮
        FloatingActionButton FAB_play = getActivity().findViewById(R.id.page_home_fab_play);
        FAB_play.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    if (AppUtils.isInstalled(getActivity(), SPs.getString("pkgName", ""))) {
                        AppUtils.runApp(getActivity() , SPs.getString("pkgName", ""));
                    } else {
                        installDialog();
                    }
                }
            });




    }


    ////////***弹窗类代码***//
    //插件
    private void PlugDialog() {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());//注意括号里.this
        View v = inf.inflate(R.layout.dialog_view_plugin, null);
        
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("插件")
            .setView(v)
            .setPositiveButton("返回", null);

        final AlertDialog dialog = build.create();
        dialog.show(); //show要放在前面
        
        //插件 获取虚天古镜
        Button plugin1 = (Button) v.findViewById(R.id.plugin_GetXutiangujing);
        plugin1.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View p1) {
                    PlugDialog.GetXutiangujing(getActivity());
                    dialog.dismiss();
                }
            });
    }

    
    //设置
    private void SettingDialog() {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());//注意括号里.this
        View v = inf.inflate(R.layout.dialog_view_setting, null);

        final EditText set_pkgName = (EditText) v.findViewById(R.id.dialog_view_setting1);
        final EditText set_bakPath = (EditText) v.findViewById(R.id.dialog_view_setting2);

        final TextInputLayout set_pkgName_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_setting1_Layout);
        final TextInputLayout set_bakPath_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_setting2_Layout);

        //应用包名
        set_pkgName_Layout.setHint("九仙道应用包名：");
        set_pkgName.setSingleLine();
        set_pkgName.setText(SPs.getString("pkgName", ""));
        set_pkgName.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                }

                @Override
                public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                    //判断 应用包名 是否合法
                    Pattern pattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)+([.][a-zA-Z_][a-zA-Z0-9_]*)+$");
                    Matcher matcher = pattern.matcher(set_pkgName.getText().toString());

                    if (set_pkgName.getText().toString().equals("")) {
                        set_pkgName_Layout.setError("包名不能为空！");
                    } else if (!matcher.matches()) {
                        set_pkgName_Layout.setError("包名不合法");
                    } else {
                        set_pkgName_Layout.setError(null);
                        set_pkgName_Layout.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable p1) {
                }
            });

        //备份路径
        set_bakPath_Layout.setHint("备份路径（目录）:");
        set_bakPath.setSingleLine();
        set_bakPath.setText(SPs.getString("bakPath", ""));
        set_bakPath.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                }

                @Override
                public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                    //判断路径是否合法
                    if (set_bakPath.getText().toString().equals("")) {
                        set_bakPath_Layout.setError("路径不能为空！");
                    } else if (set_bakPath.getText().toString().contains("//") || set_bakPath.getText().toString().contains("\\")) {
                        set_bakPath_Layout.setError("路径不合法");
                    } else {
                        set_bakPath_Layout.setError(null);
                        set_bakPath_Layout.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable p1) {
                }
            });

        //开始弹窗
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("设置")
            .setView(v)
            .setPositiveButton("确定", null)
            .setNegativeButton("取消", null)
            .setNeutralButton("恢复默认", null);

        final AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show(); //show要放在前面

        //监听 确定
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    if (set_pkgName_Layout.getError() == null && set_bakPath_Layout.getError() == null) {
                        //保存SP内容
                        SPs.edit().putString("pkgName", set_pkgName.getText().toString()).commit();
                        SPs.edit().putString("bakPath", set_bakPath.getText().toString()).commit();
                        dialog.cancel();//取消弹窗
                    }
                }
            });

        //监听 恢复默认
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    //恢复默认 弹窗
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("确定恢复默认？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dia, int which) {
                                set_pkgName.setText(Data.pkgName_def);
                                set_bakPath.setText(Data.BakPath_def);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                    dialog.show();
                }
            });


    }

    //帮助
    private void HelpDialog() {
        AlertDialog build = new AlertDialog.Builder(getActivity())
            .setTitle("帮助")
            .setMessage(FileUtils.readAssetsTxt(getActivity(), "help", "utf-8"))
            .setPositiveButton("确定", null)
            .create();
        build.show();
    }

    //关于
    private void InfoDialog() {
        AlertDialog build = new AlertDialog.Builder(getActivity())
            .setTitle("关于")
            .setMessage(R.string.infomation)
            .setNegativeButton("确定", null)
            .setPositiveButton("更新历史", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    //更新历史 弹窗
                    AlertDialog build = new AlertDialog.Builder(getActivity())
                        .setTitle("更新历史")
                        .setMessage(FileUtils.readAssetsTxt(getActivity(), "history", "utf-8"))
                        .setPositiveButton("确定", null)
                        .create();
                    build.show();
                }
            })
            .create();
        build.show();
    }
    ////////***弹窗类代码***结束***//




    ////////***安装九仙道 代码***//
    //安装 弹窗
    private void installDialog() {
        //未安装
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setMessage("未发现安装九仙道，是否本地安装？（不消耗流量）\n（若已安装，可以设置更改应用包名）");
        build.setPositiveButton("本地安装", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    install_jxd();//安装九仙道
                }
            });
        build.setNegativeButton("取消", null);
        build.create().show();
    }

    //安装九仙道
    private void install_jxd() {
        if (new File(apkPath).exists() && FileUtils.getFileMD5(apkPath).equals("3ec5471f30037db9d3b193e9cdeb4f35")) {
            //本地已有apk文件
            AppUtils.installApk(getActivity(), apkPath);
        } else {   
            //线程执行代码
            runnable = new Runnable() {
                @Override
                public void run() {
                    AppUtils.copyFromAssets(getActivity(), "jxd.apk", new File(apkPath).getParent(), "jxd.apk");
                    AppUtils.installApk(getActivity() , apkPath);
                    ProDialog.dismiss();//线程完成，进度框消失
                }
            };
            ProDialog_thread(getActivity(), runnable);//执行线程
        }
    }
    ////////***安装九仙道 代码***结束***//





    //线程 进度框
    public void ProDialog_thread(Context context, Runnable runnable) {
        //初始化进度框
        ProDialog = new ProgressDialog(context);//构建进度框
        ProDialog.setMessage("加载中...");
        ProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置进度条样式
        ProDialog.setCancelable(false);
        ProDialog.setCanceledOnTouchOutside(false);
        ProDialog.show();

        //新建线程
        new Thread(runnable)
            .start();//线程开始
    }







}//class
