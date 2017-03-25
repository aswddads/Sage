package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.StreamUtil;


public class SplashActivity extends Activity {
    private TextView tv_version_name;
    private int mLocalVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        去除掉当前activity头titile
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

//        初始化 UI
        initUI();
//        初始化数据
        initDate();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }

    /**
     * 初始化数据
     */
    private void initDate() {
//        1.应用版本名称
        tv_version_name.setText("版本名称：" + getVersionName());
//        2.检测（本地版本号，服务器版本号）是否有更新，如果有更新，提示用户下载(member)
        mLocalVersionCode = getVersionCode();
//        3.获取服务器版本号(客服端发请求，服务端响应)（json，xml）
//       http://www.xxoo.com/update.json?key=value  返回200请求成功，流的方式读取
/**
 * json中内容包含：
 *   更新版本号名称、信息、服务器版本号、新版本下载地址
 */

        checkVersion();
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
//        发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    http://192.168.43.111:8080/update.json测试阶段不是最优
//                    以下地址仅限于电脑模拟器使用tomcat
                   URL url= new URL("http://10.0.2.2:8080/update.json");
//                    开启链接
                   HttpURLConnection connection =(HttpURLConnection) url.openConnection();
//                    这只常见请求头
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
//                    默认为GET请求
//                    connection.setRequestMethod("POST");
//                    获取成功响应码
                     if(connection.getResponseCode()==200){
//                      以流的形式，获取数据,将流转化为字符串 ，封装工具类
                         InputStream is=connection.getInputStream();
                         String json=StreamUtil.streamToString(is);
                         Log.i("SplashActivity",json);
                     }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 返回版本号
     *
     * @return 非0则带表获取成功，
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
//        从包的管理者对象中获取指定包名的基本信息（版本号，版本名) 传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
//            获取对应的版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名称：清单文件中
     * return 应用版本名称 返回null代表异常
     */
    private String getVersionName() {
//        包管理者对象PackageManger
        PackageManager pm = getPackageManager();
//        从包的管理者对象中获取指定包名的基本信息（版本号，版本名) 传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
//            获取对应的版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
