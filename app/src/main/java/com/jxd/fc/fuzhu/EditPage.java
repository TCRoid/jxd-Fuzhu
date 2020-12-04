package com.jxd.fc.fuzhu;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.CommonUtils.AppUtils;
import com.CommonUtils.FileUtils;
import com.CommonUtils.ToolUtils;
import com.jxd.fc.fuzhu.Adapter.MyDialogListAdapter;
import com.jxd.fc.fuzhu.Adapter.MyListAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPage extends Fragment implements FragmentBackHandler {


    private LinearLayout LL1,LL2,LL3;
    private ListView ListV;
    //列表数据
    private List<Map<String,Object>> ListData = new ArrayList<Map<String,Object>>();
    //数据目录名，数据路径，apk路径
    private String DataName,RootPath,apkPath;
    //实时路径（公共）
    public String Path;
    //线程 进度条相关
    private ProgressDialog ProDialog;
    private Runnable runnable;
    //退出
    private static boolean isExit;
    //下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences SPs;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.page_edit, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);     

        ////////***变量初始化***//
        SPs = getActivity().getSharedPreferences("setting", Context.MODE_WORLD_READABLE);

        DataName = "com.jxd.fc";
        apkPath = getActivity().getExternalCacheDir().getAbsolutePath() + "/jxd.apk";//cache目录下的apk安装文件
        RootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + DataName;
        Path = RootPath; //当前目录(实时)

        LL1 = getActivity().findViewById(R.id.page_edit_LL1);//列表
        LL2 = getActivity().findViewById(R.id.page_edit_LL2);//无游戏数据
        LL3 = getActivity().findViewById(R.id.page_edit_LL3);//空文件夹
        ListV = getActivity().findViewById(R.id.page_edit_List);
        ////////***变量初始化***结束***//


        CheckData();//检测游戏数据




        /////////***列表事件****//
        //设置列表点击事件
        ListV.setOnItemClickListener(new OnItemClickListener(){             
                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    if (Integer.parseInt(ListData.get(p3).get("isDirectory") + "") == 2) {
                        Path = Path + File.separator + ListData.get(p3).get("fileName") + "";//路径
                        openDir(Path);
                    } else {
                        String filePath = Path + File.separator + ListData.get(p3).get("fileName") + "";//文件路径
                        String fileName = ListData.get(p3).get("fileName") + "";//文件名
                        EditDialog(filePath, fileName);
                    }
                }
            });

        //列表长按事件
        ListV.setOnItemLongClickListener(new OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    String filePath = Path + File.separator + ListData.get(p3).get("fileName") + "";//文件路径
                    String fileName = ListData.get(p3).get("fileName") + "";//文件名
                    SelectDialog(filePath, fileName);

                    return true; //返回true，就不执行列表点击事件
                }
            });

        //下拉刷新列表
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.page_edit_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);//设置主题颜色
        //设置监听  
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
                @Override
                ////*******刷新事件
                public void onRefresh() {
                    //新建线程
                    new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //线程内更新UI
                                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                            openDir(Path);//重新打开该文件夹，实现刷新
                                        }
                                    });
                                //隐藏下拉刷新
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        })
                        .start();//线程开始
                }
            });
        /////////***列表事件***结束***//



        ////////***悬浮按钮***//
        //新建 悬浮按钮
        FloatingActionButton FAB_new = getActivity().findViewById(R.id.page_edit_fab_new);
        FAB_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File files = new File(RootPath);
                    if (!files.exists() || !files.isDirectory() || files.list().length <= 0) {
                        ToolUtils.toast(getActivity(), "无游戏数据");
                    } else {
                        NewDialog(Path);
                    }
                }
            });

        //刷新 悬浮按钮
//        FloatingActionButton FAB_refresh = getActivity().findViewById(R.id.page_edit_fab_refresh);
//        FAB_refresh.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View p1) {
//                    //openDir(Path);//重新打开该文件夹，实现刷新
//                    CheckData();
//                }
//			});
//

        //长按事件 提示
        FAB_new.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ToolUtils.toast(getActivity(), "新建文件");
                    return true;
                }
            });
//        FAB_refresh.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    ToolUtils.toast(getActivity(), "刷新列表");
//                    return true;
//                }
//            });
        ////////***悬浮按钮***结束***//


        //安装 按钮
        CardView install = (CardView) getActivity().findViewById(R.id.page_edit_install);
        install.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    install_jxd();//安装九仙道
                }
            });



    }










    //检测游戏数据（是否存在）
    private void CheckData() {
        File file = new File(RootPath);
        if (!file.exists() || !file.isDirectory() || file.list().length <= 0) {
            LL1.setVisibility(View.GONE);
            LL2.setVisibility(View.VISIBLE);  
        } else {
            LL2.setVisibility(View.GONE);
            LL1.setVisibility(View.VISIBLE);

            openDir(Path);
        }//else
    }

    //打开文件夹
    private void openDir(String Dir) {
        File file = new File(Dir);
        //检测游戏数据（是否为空文件夹）
        if (!file.exists() || !file.isDirectory() || file.list().length <= 0) {
            LL1.setVisibility(View.GONE);
            LL3.setVisibility(View.VISIBLE);  
        } else {
            LL3.setVisibility(View.GONE);
            LL1.setVisibility(View.VISIBLE);

            //列表数据
            ListV = getActivity().findViewById(R.id.page_edit_List);
            ListData = GetPathFilsList(Dir);
            MyListAdapter ListAdapter = new MyListAdapter(getActivity(), ListData);
            ListV.setAdapter(ListAdapter);   
        }
    }

    //获取文件列表
    private List<Map<String,Object>> GetPathFilsList(String path) {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> filelist = new ArrayList<Map<String,Object>>();

        String[] Files = new File(path).list();

        try {
            for (String file : Files) {
                Map<String, Object> map = new HashMap<String, Object>();
                if (new File(path + File.separator + file).isDirectory()) {
                    map.put("isDirectory", 2);
                    map.put("fileName", file);

                    list.add(map);
                } else {
                    map.put("isDirectory", 1);
                    map.put("fileName", file);

                    filelist.add(map);
                }

            }
            list.addAll(filelist);
            return list;
        } catch (Exception e) {
            return null;
        }
    }




    ////////***弹窗类代码***//

    //新建文件 弹窗
    private void NewDialog(final String folder_path) {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());//注意括号里.this
        View v = inf.inflate(R.layout.dialog_view_new, null);
        final EditText new_name = (EditText) v.findViewById(R.id.dialog_view_new1);
        final EditText new_content = (EditText) v.findViewById(R.id.dialog_view_new2);

        final TextInputLayout new_name_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_new1_Layout);
        final TextInputLayout new_content_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_new2_Layout);

        new_name_Layout.setHint("文件名：");
        new_name.setSingleLine();
        new_content_Layout.setHint("文件内容（可为空）：");

        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("新建文件")
            .setView(v)
            .setPositiveButton("确定", null)
            .setNegativeButton("取消", null);

        final AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show(); //show要放在前面

        //监听 确定键
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    if (!FileUtils.isValid(new_name.getText().toString(), folder_path)) {
                        ToolUtils.toast(getActivity(), "文件名无效");
                    } else {
                        String content=new_content.getText().toString();

                        if (content.equals("") || new_content.getText() == null) {
                            content = "";
                        }

                        String path = folder_path + File.separator + new_name.getText().toString();
                        if (new File(path).exists()) {
                            ToolUtils.toast(getActivity(), "文件名重复");
                        } else {
                            try {
                                FileUtils.saveToFile(path, content, "utf-8");//创建文件
                            } catch (IOException e) {}
                            openDir(folder_path);//重新打开该文件夹，实现刷新
                            dialog.cancel();//取消弹窗
                        }
                    }
                }
            });
    }


    //编辑弹窗
    private void EditDialog(final String path, final String name) {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());
        View v = inf.inflate(R.layout.dialog_view_edit, null);
        final EditText edit = (EditText) v.findViewById(R.id.dialog_view_edit);     
        final TextInputLayout edit_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_edit_Layout);

        //edit_Layout.setHint("编辑内容：");
        //设置布局
        TextInputLayout.LayoutParams params = (TextInputLayout.LayoutParams) edit_Layout.getLayoutParams();
        params.setMargins(16, 16, 8, 0);

        try {
            edit.setText(FileUtils.readFileByLines(path, "utf-8"));
        } catch (IOException e) {}

        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle(name)
            .setView(v);

        build.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    try {
                        FileUtils.saveToFile(path, edit.getText().toString(), "utf-8");
                    } catch (IOException e) {}
                }
            });

        build.setNegativeButton("取消", null);
        AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //列表选择 对话框
    private void SelectDialog(final String path, final String name) {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle("选择操作");
        //列表选择项
        int[] SelImageList = {R.drawable.ic_rename,R.drawable.ic_delete};
        final String[] SelTextList = {"重命名","删除"};
        build.setAdapter(new MyDialogListAdapter(getActivity(), SelImageList, SelTextList), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (SelTextList[which]) {
                        case "重命名":
                            renameFile(path, name);
                            break;

                        case "删除":
                            deleteFile(path, name);
                            break;
                    }
                }
            });
        build.setPositiveButton("取消", null);
        build.create().show();
    }


    //重命名文件 弹窗
    private void renameFile(final String path, final String name) {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());
        View v = inf.inflate(R.layout.dialog_view_edit, null);
        final EditText edit = (EditText) v.findViewById(R.id.dialog_view_edit);
        final TextInputLayout edit_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_edit_Layout);

        edit_Layout.setHint("请输入文件名：");
        edit.setText(name);
        edit.setSingleLine();

        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("重命名")
            .setView(v)
            .setPositiveButton("确定", null)
            .setNegativeButton("取消", null);

        final AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show(); //show要放在前面

        //监听 确定键
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    //新命名文件路径
                    String new_path=Path + File.separator + edit.getText().toString();

                    if (edit.getText().toString().equals("")) {
                        ToolUtils.toast(getActivity(), "请输入文件名");
                    } else if (new File(new_path).exists()) {
                        ToolUtils.toast(getActivity(), "文件名重复");
                    } else if (!FileUtils.isValid(edit.getText().toString(), Path)) {
                        ToolUtils.toast(getActivity(), "文件名无效");
                    } else {
                        if (new File(path).renameTo(new File(new_path))) {
                            openDir(Path);//重新打开该文件夹，实现刷新
                        } else {
                            ToolUtils.toast(getActivity(), "重命名失败");
                        }
                        dialog.cancel();//取消弹窗
                    }
                }
            });
    }


    //删除文件 弹窗
    private void deleteFile(final String path, String name) {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("删除")
            .setMessage("确定要删除 " + name + " ?");

        build.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    File file = new File(path);

                    if (file.exists()) {
                        //文件存在
                        if (file.isFile()) {   
                            //文件
                            if (file.delete()) {
                                openDir(new File(path).getParent());//重新打开该文件夹，实现刷新
                            } else {
                                ToolUtils.toast(getActivity(), "删除失败");
                            }
                        } else {//文件夹
                            //线程执行代码
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (!FileUtils.deleteFolder(path)) {
                                        ToolUtils.toast(getActivity(), "删除失败");
                                    }
                                    //线程内更新UI
									new Handler(getActivity().getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                                openDir(new File(path).getParent());//重新打开该文件夹，实现刷新                           
                                            }
                                        });

                                    ProDialog.dismiss();//线程完成，进度框消失
                                }
                            };
                            ProDialog_thread(getActivity(), runnable);//执行线程
                        }
                    } else {
                        ToolUtils.toast(getActivity(), "未找到文件");
                    }

                }
            });
        build.setNegativeButton("取消", null);
        build.create().show();
    }

    ////////***弹窗类代码***结束***//



    ////////***安装九仙道 代码***//
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


    //返回键
    @Override
    public boolean onBackPressed() {
        if (Path.equals(RootPath)) {
            //双击退出
            if (!isExit) {
                isExit = true;
                ToolUtils.toast(getActivity(), "再按一次退出程序");
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                isExit = false;
                            } catch (InterruptedException e) { }
                        }
                    }).start();
            } else {
                getActivity().finish();//退出
                //super.onBackPressed();
            }
        } else {
            //返回上级
            Path = new File(Path).getParent();
            openDir(Path);
        }
        return true;
    }









}//class
