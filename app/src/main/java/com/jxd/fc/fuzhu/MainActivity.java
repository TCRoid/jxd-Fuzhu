package com.jxd.fc.fuzhu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.CommonUtils.ToolUtils;
import com.jxd.fc.fuzhu.Adapter.MyFragmemtPagerAdapter;
import java.util.ArrayList;
import com.jxd.fc.fuzhu.JxdUtils.Data;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    //导航栏 
    private RadioGroup tab;
    //作为页面容器的ViewPager 
    private ViewPager viewpager;  
    //页面集合 
    private ArrayList<Fragment> arrayList;
    //三个Fragment（页面） 
    private HomePage page_home;//主页
    private EditPage page_edit;//编辑
    private BackupPage page_backup;//备份
    //三个导航按钮
    private RadioButton tab_home,tab_edit,tab_backup;
    
    private SharedPreferences SPs;
    private static boolean isExit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化界面
        initView();
        
        //////***SharedPreferences初始化***
        SPs = this.getSharedPreferences("setting", Context.MODE_WORLD_READABLE);
        //九仙道 应用包名
        if (SPs.getString("pkgName", "").equals(""))
        {
            SPs.edit().putString("pkgName", Data.pkgName_def).commit();
		}
        //备份路径
        if (SPs.getString("bakPath", "").equals(""))
        {
            SPs.edit().putString("bakPath", Data.BakPath_def).commit();
        }
        
        
        
        //初始化 导航按钮
        tab_home = (RadioButton) findViewById(R.id.tab_home);
        tab_edit = (RadioButton) findViewById(R.id.tab_edit);
        tab_backup = (RadioButton) findViewById(R.id.tab_backup);
        
        //导航按钮 长按事件
        tab_home.setOnLongClickListener(new OnLongClickListener(){
                @Override
                public boolean onLongClick(View p1) {
                    ToolUtils.toast(getApplicationContext(), "主页");
                    return true;
                }
            });
        tab_edit.setOnLongClickListener(new OnLongClickListener(){
                @Override
                public boolean onLongClick(View p1) {
                    ToolUtils.toast(getApplicationContext(), "数据修改");
                    return true;
                }
            });
        tab_backup.setOnLongClickListener(new OnLongClickListener(){
                @Override
                public boolean onLongClick(View p1) {
                    ToolUtils.toast(getApplicationContext(), "数据备份还原");
                    return true;
                }
            });

            
    }





    //初始化
    private void initView() {
        //页面添加
        arrayList = new ArrayList<Fragment>();

        page_home = new HomePage();
        arrayList.add(page_home);

        page_edit = new EditPage();
        arrayList.add(page_edit);

        page_backup = new BackupPage();
        arrayList.add(page_backup);

        tab = (RadioGroup)findViewById(R.id.navigate);
        tab.setOnCheckedChangeListener(this);//状态改变监听

        //RadioButton tab_home = (RadioButton) findViewById(R.id.tab_home);
        //tab_home.setChecked(true);//主页默认选中

        //页面绑定
        viewpager = (ViewPager)findViewById(R.id.viewPager);
        viewpager.setAdapter(new MyFragmemtPagerAdapter(getSupportFragmentManager(), arrayList));
        viewpager.setCurrentItem(0); //设置主页是第一页

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int i) {
                    switch (i) {
                        case 0:
                            tab.check(R.id.tab_home);
                            break;
                        case 1:
                            tab.check(R.id.tab_edit);
                            break;
                        case 2:
                            tab.check(R.id.tab_backup);
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }

            });

    }

    //RadioGroup 状态改变监听
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.tab_home:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tab_edit:
                viewpager.setCurrentItem(1);
                break;
            case R.id.tab_backup:
                viewpager.setCurrentItem(2);
                break;
        }

    }


    //返回键
    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            //双击退出
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
                //finish();
                super.onBackPressed();//退出
            }

        }

    }






}//class
