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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.CommonUtils.FileUtils;
import com.CommonUtils.ToolUtils;
import com.CommonUtils.ZipUtils;
import com.jxd.fc.fuzhu.Adapter.MyDialogListAdapter;
import com.jxd.fc.fuzhu.Adapter.MyListAdapter_Backup;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackupPage extends Fragment {

    private LinearLayout LL1,LL2;
    private ListView ListV;
    //列表数据
    private List<Map<String,Object>> ListData = new ArrayList<Map<String,Object>>();
    //数据目录名，数据路径，备份目录，角色数据目录
    private String DataName,RootPath,BakPath,jsPath;
    //线程 进度条相关
    private ProgressDialog ProDialog;
    private Runnable runnable;
    //下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences SPs;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.page_backup, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ////////***变量初始化***//
        SPs = getActivity().getSharedPreferences("setting", Context.MODE_WORLD_READABLE);

        BakPath = SPs.getString("bakPath", "");
        DataName = "com.jxd.fc";
        RootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + DataName;
        jsPath = RootPath + File.separator + "js";
        LL1 = getActivity().findViewById(R.id.page_backup_LL1);//列表
        LL2 = getActivity().findViewById(R.id.page_backup_LL2);//无备份数据
        ListV = getActivity().findViewById(R.id.page_backup_List);
        ////////***变量初始化***结束***//


        openDir(BakPath);


        /////////***列表事件***//
        //列表点击事件
        ListV.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    boolean All=false;
                    if (ListData.get(p3).get("isAll").toString().equals("true")) {
                        All = true;
                    }
                    String filepath = BakPath + File.separator + ListData.get(p3).get("fileName") + "";//文件路径
                    String fileName = ListData.get(p3).get("fileName") + "";//文件名

                    if (!All) {
                        File files = new File(RootPath);
                        if (!files.exists() || !files.isDirectory() || files.list().length <= 0) {
                            ToolUtils.toast(getActivity(), "无游戏数据，无法还原角色的备份数据");
                        } else {
                            recoverBak(filepath, fileName, All);
                        }
                    } else {
                        recoverBak(filepath, fileName, All);
                    }

                }
            });

        //列表长按事件
        ListV.setOnItemLongClickListener(new OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    String filePath = BakPath + File.separator + ListData.get(p3).get("fileName") + "";//文件路径
                    String fileName = ListData.get(p3).get("fileName") + "";//文件名
                    SelectDialog(filePath, fileName);

                    return true; //返回true，就不执行列表点击事件
                }
            });

        //下拉刷新列表
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.page_backup_swipe_refresh);
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
                                            openDir(BakPath);//重新打开该文件夹，实现刷新                           
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

        /////////***悬浮按钮代码***//
        //新建 备份
        FloatingActionButton FAB_new = (FloatingActionButton) getActivity().findViewById(R.id.page_backup_fab_new);
        FAB_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File files = new File(jsPath);
                    if (!files.exists() || !files.isDirectory() || files.list().length <= 0) {
                        //无角色数据
                        files = new File(RootPath);
                        if (!files.exists() || !files.isDirectory() || files.list().length <= 0) {
                            //无游戏数据
                            ToolUtils.toast(getActivity(), "无游戏数据");
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
                                .setMessage("无角色数据，是否选择全部数据备份？")
                                .setPositiveButton("全部备份", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface p1, int p2) {
                                        //新建 全部备份
                                        NewDialog_All();
                                    }
                                })
                                .setNegativeButton("取消", null);
                            build.create().show();
                        }    
                    } else {
                        //新建 备份
                        NewDialog();
                    }
                }
            });

        //新建 全部备份
        FloatingActionButton FAB_new_all = (FloatingActionButton) getActivity().findViewById(R.id.page_backup_fab_new_all);
        FAB_new_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File files = new File(RootPath);
                    if (!files.exists() || !files.isDirectory() || files.list().length <= 0) {
                        //无游戏数据
                        ToolUtils.toast(getActivity(), "无游戏数据");
                    } else {
                        NewDialog_All();
                    }
                }
            });


        //长按事件 提示
        FAB_new.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ToolUtils.toast(getActivity(), "角色数据备份");
                    return true;
                }
            });
        FAB_new_all.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ToolUtils.toast(getActivity(), "全部数据备份");
                    return true;
                }
            });
        /////////***悬浮按钮代码***结束***//


    }



    //打开文件夹
    private void openDir(String Dir) {
        File file = new File(Dir);
        //检测游戏数据（是否为空文件夹）
        if (!file.exists() || !file.isDirectory() || file.list().length <= 0) {
            LL1.setVisibility(View.GONE);
            LL2.setVisibility(View.VISIBLE);  
        } else {
            LL2.setVisibility(View.GONE);
            LL1.setVisibility(View.VISIBLE);

            ListV = getActivity().findViewById(R.id.page_backup_List);
            ListData = GetPathFilsList(Dir);
            MyListAdapter_Backup ListAdapter = new MyListAdapter_Backup(getActivity(), ListData);
            ListV.setAdapter(ListAdapter);   
        }
    }

    //获取文件列表
    private List<Map<String,Object>> GetPathFilsList(String path) {
        List<Map<String,Object>> listDataA = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> listDataB = new ArrayList<Map<String,Object>>();

        try {
            //String[] Files = new File(path).list();
            File[] Files = FileUtils.SortByDate(path);//按最后修改时间排序
            for (File file : Files) {
                Map<String, Object> map = new HashMap<String, Object>();
                
                //file 即为 文件路径
                
                //取后缀名
                //String file_name=filepath.getName();
                //String suffix=file_name.substring(file_name.lastIndexOf(".") + 1);
                String suffix = FileUtils.getSuffix(file.toString());

                if (suffix.equals("all")) {
                    map.put("isAll", "true");
                    map.put("fileName", file.getName());
                    map.put("filePath", file);

                    listDataA.add(map);
                } else if (!suffix.equals("")) {
                    map.put("isAll", "false");
                    map.put("fileName", file.getName());
                    map.put("filePath", file);

                    listDataB.add(map);
                }
            }
            listDataA.addAll(listDataB);
            return listDataA;
        } catch (Exception e) {
            return null;
        }
    }



    ////////***弹窗类代码***//
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

        TextInputLayout edit_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_edit_Layout);

        //取后缀名
        final String suffix=name.substring(name.lastIndexOf(".") + 1);
        //取前缀名
        String prefix=name.substring(0, name.lastIndexOf("."));

        edit_Layout.setHint("请输入文件名：");
        edit.setText(prefix);
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
                    String new_path=BakPath + File.separator + edit.getText().toString() + "." + suffix;

                    if (edit.getText().toString().equals("")) {
                        ToolUtils.toast(getActivity(), "请输入文件名");
                    } else if (new File(new_path).exists()) {
                        ToolUtils.toast(getActivity(), "文件名重复");
                    } else if (!FileUtils.isValid(edit.getText().toString(), BakPath)) {
                        ToolUtils.toast(getActivity(), "文件名无效");
                    } else {
                        if (new File(path).renameTo(new File(new_path))) {
                            openDir(BakPath);//重新打开该文件夹，实现刷新
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
        //取前缀名
        String prefix=name.substring(0, name.lastIndexOf("."));

        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("删除")
            .setMessage("确定要删除 " + prefix + " ?");

        build.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    File file = new File(path);

                    if (file.exists()) {
                        //文件
                        if (file.delete()) {
                            openDir(BakPath);//重新打开该文件夹，实现刷新
                        } else {
                            ToolUtils.toast(getActivity(), "删除失败");
                        }

                    } else {
                        ToolUtils.toast(getActivity(), "未找到文件");
                    }

                }
            });
        build.setNegativeButton("取消", null);
        build.create().show();
    }


    //备份还原 弹窗
    private void recoverBak(final String path, final String name, final boolean isAll) {
        AlertDialog.Builder build=new AlertDialog.Builder(getActivity())
            .setTitle("还原")
            .setMessage("确定将备份数据还原？");
        build.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2) {
                    if (isAll) {
                        //线程执行代码
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                ZipUtils.unZipFiles(path, RootPath);
                                ProDialog.dismiss();//线程完成，进度框消失
                            }
                        };
                        ProDialog_thread(getActivity(), runnable);//执行线程
                    } else {
                        //线程执行代码
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                String suffix=name.substring(name.lastIndexOf(".") + 1);
                                ZipUtils.unZipFiles(path, jsPath + "/" + suffix);
                                ProDialog.dismiss();//线程完成，进度框消失
                            }
                        };
                        ProDialog_thread(getActivity(), runnable);//执行线程
                    }
                }
            });
        build.setNegativeButton("取消", null);
        build.create().show();
    }


    //全部备份 弹窗
    private void NewDialog_All() {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());
        View v = inf.inflate(R.layout.dialog_view_edit, null);
        final EditText edit = (EditText) v.findViewById(R.id.dialog_view_edit);

        TextInputLayout edit_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_edit_Layout);

        edit_Layout.setHint("请输入备份文件名：");
        edit.setSingleLine();

        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("全部数据备份")
            .setView(v)
            .setPositiveButton("确定", null)
            .setNegativeButton("取消", null);

        final AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show(); //show要放在前面

        //监听确定键
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    //备份文件路径
                    String bakName = BakPath + "/" + edit.getText().toString() + ".all";

                    if (edit.getText().toString().equals("")) {
                        ToolUtils.toast(getActivity(), "请输入备份的文件名");
                    } else if (new File(bakName).exists()) {
                        ToolUtils.toast(getActivity(), "文件名重复");
                    } else if (!FileUtils.isValid(edit.getText().toString(), BakPath)) {
                        ToolUtils.toast(getActivity(), "文件名无效");
                    } else {
                        AllBackup(edit.getText().toString());//全部备份
                        dialog.cancel();//弹窗取消
                    }
                }
            });

    }
    ////////***弹窗类代码***结束***//


    ////////********新建备份 弹窗********
    private void NewDialog() {
        //自定义弹窗
        LayoutInflater inf = LayoutInflater.from(getActivity());//注意括号里.this
        View v = inf.inflate(R.layout.dialog_view_newbak, null);
        final EditText bak_name = (EditText) v.findViewById(R.id.dialog_view_newbakV1);
        final ListView js_list = (ListView) v.findViewById(R.id.dialog_view_newbakV2);//角色列表
        final TextView js_name = (TextView) v.findViewById(R.id.dialog_view_newbakV3);

        TextInputLayout bak_name_Layout = (TextInputLayout) v.findViewById(R.id.dialog_view_newbakV1_Layout);

        bak_name_Layout.setHint("请输入备份文件名：");
        bak_name.setSingleLine();
        js_list.setAdapter(getJSlist());

        AlertDialog.Builder build = new AlertDialog.Builder(getActivity())
            .setTitle("角色数据备份")
            .setView(v)

            .setPositiveButton("备份", null)
            .setNegativeButton("取消", null);

        final AlertDialog dialog = build.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show(); //show要放在前面


        //备份 按钮
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    //备份文件路径
                    final String bakName = BakPath + "/" + bak_name.getText().toString() + "." + js_name.getText();

                    if (bak_name.getText().toString().equals("")) {
                        ToolUtils.toast(getActivity(), "请输入备份的文件名");
                    } else if (new File(bakName).exists()) {
                        ToolUtils.toast(getActivity(), "文件名重复");
                    } else if (!FileUtils.isValid(bak_name.getText().toString(), BakPath)) {
                        ToolUtils.toast(getActivity(), "文件名无效");
                    } else {
                        if (js_name.getText().equals("未选择")) {
                            ToolUtils.toast(getActivity(), "请选择备份的角色");
                        } else {
                            File files = new File(BakPath);
                            if (!files.exists()) {
                                files.mkdirs();
                            }

                            //线程执行代码
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ZipUtils.zipFiles(jsPath + "/" + js_name.getText(), bakName);
                                    } catch (IOException e) {
                                        ToolUtils.toast(getActivity(), "出错");
                                    }

                                    //线程内更新UI
                                    new Handler(getActivity().getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                                openDir(BakPath);
                                            }
                                        });

                                    ProDialog.dismiss();//线程完成，进度框消失
                                }
                            };
                            ProDialog_thread(getActivity(), runnable);//执行线程

                            dialog.cancel();//取消弹窗
                        }
                    }
                }                
            });


        //角色列表 点击事件
        js_list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                    String name = (String) l.getItemAtPosition(position);
                    if (bak_name.getText().toString().equals("")) {
                        bak_name.setText(name);
                    }
                    js_name.setText(name);
                }
            });
    }

    //获取角色列表
    private ListAdapter getJSlist() {
        File file = new File(jsPath);
        String[] files=file.list();

        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files);

        return adapter;
    }
    ////////******新建备份 弹窗******结束******






    ////////***备份类代码***//
    //全部备份
    private void AllBackup(String name) {
        File files = new File(BakPath);

        if (!files.exists()) {
            files.mkdirs();
        }

        final String bakName = BakPath + "/" + name + ".all";//备份文件路径

        //线程执行代码
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    ZipUtils.zipFiles(RootPath, bakName);
                } catch (IOException e) {}
                //线程内更新UI
                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                            openDir(BakPath);//重新打开该文件夹，实现刷新                           
                        }
                    });

                ProDialog.dismiss();//线程完成，进度框消失
            }
        };
        ProDialog_thread(getActivity(), runnable);//执行线程
    }

    ////////***备份类代码***结束***//








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
