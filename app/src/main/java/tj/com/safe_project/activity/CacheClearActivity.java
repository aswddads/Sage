package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/4/11.
 */
public class CacheClearActivity extends Activity{

    private Button bt_clear;
    private ProgressBar pb_bar;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initUI();
        intitData();
    }

    /**
     * 遍历手机应用
     */
    private void intitData() {
        new Thread(){
            @Override
            public void run() {
                //        1  获取包管理者对象
                pm = getPackageManager();
//        2  获取安装在手机上的所有应用
                List<PackageInfo> installedPackages= pm.getInstalledPackages(0);
//        3  给进度条设置最大值
                pb_bar.setMax(installedPackages.size());
//        4  遍历应用，获取有缓存的应用信息   应用名称  缓存大小  图标   包名
                for (PackageInfo packageInfo: installedPackages) {
                    String packageName=packageInfo.packageName;
                    getPackageCache(packageName);
                }


                super.run();
            }
        }.start();
    }

    /**
     * 通过包名获取缓存信息
     * @param packageName
     */
    private void getPackageCache(String packageName) {

        try {
            Class<?> clazz=Class.forName("android.content.pm.PackageManager");
//            Method method=clazz.getMethod("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
//            method.invoke(pm,"com.android.browser",);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initUI() {
        bt_clear = (Button) findViewById(R.id.bt_clear);
        pb_bar = (ProgressBar)findViewById(R.id.pb_bar);
        tv_name = (TextView)findViewById(R.id.tv_name);
        ll_add_text =  (LinearLayout) findViewById(R.id.ll_add_text);
    }
}
