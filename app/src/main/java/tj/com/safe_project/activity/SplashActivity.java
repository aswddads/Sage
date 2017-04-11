package tj.com.safe_project.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.utils.StreamUtil;
import tj.com.safe_project.utils.ToastUtil;

import static java.lang.System.currentTimeMillis;


public class SplashActivity extends Activity {
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;
    private RelativeLayout mRl_root;

    protected static final int UPDATE_VERSION = 100;
    protected static final int ENTER_HOME = 101;
    protected static final int URL_ERROR = 102;
    protected static final int IO_ERROR = 103;
    protected static final int JSON_ERROR = 104;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
//                    弹出对话框，提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
//                    Toast.makeText(SplashActivity.this,"",Toast.LENGTH_SHORT).show();
                    ToastUtil.show(SplashActivity.this, "URL 异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(SplashActivity.this, "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "程序解析异常");
                    enterHome();
                    break;
            }
        }
    };


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
//        初始化动画
        initAnimation();
//        初始化数据库操作
        initDB();
        if (!SpUtils.getBoolean(this,ConstanValue.HAS_SORCUT,false)){
//        生成快捷方式
            initShortCut();
        }
    }

    /**
     * 生成快捷方式
     */
    private void initShortCut() {
//     给intent维护图标、名称

        Intent intent=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, R.drawable.ic_launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");
        Intent shortCutIntent=new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortCutIntent);
//        发送广播
        sendBroadcast(intent);
//        告知已经生成快捷方式
        SpUtils.putBoolean(this,ConstanValue.HAS_SORCUT,true);
    }

    //添加淡入淡出动画效果
    private void initAnimation() {
        AlphaAnimation alphAnimation = new AlphaAnimation(0, 1);
        alphAnimation.setDuration(3000);
        mRl_root.startAnimation(alphAnimation);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        mRl_root = (RelativeLayout) findViewById(R.id.rl_root);
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

        if (SpUtils.getBoolean(getApplicationContext(), ConstanValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
//            直接进入主界面  消息机制
//            mHandler.sendMessageDelayed(,4000);  4秒后进入enter_home状态码指定的消息
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }

    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
//        发送请求
        new Thread(new Runnable() {
            Message msg = Message.obtain();
            long startTime = currentTimeMillis();

            @Override
            public void run() {
                try {
//                    http://192.168.43.111:8080/update.json测试阶段不是最优
//                    以下地址仅限于电脑模拟器使用tomcat
                    URL url = new URL("http://10.0.2.2:8080/update.json");
//                    开启链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    这只常见请求头
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
//                    默认为GET请求
//                    connection.setRequestMethod("POST");
//                    获取成功响应码
                    if (connection.getResponseCode() == 200) {
//                      以流的形式，获取数据,将流转化为字符串 ，封装工具类
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.streamToString(is);
//                         Log.i("SplashActivity",json);
                        //json解析
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        //比对版本号
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            //提示用户更新，弹出对话框
                            msg.what = UPDATE_VERSION;
                        } else {
//                             进入主界面
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    //指定睡眠时间，请求网络时间超过4秒不做处理，小于4秒，强制4秒
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
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

    /**
     * 进入主界面
     */
    protected void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        //开启一个新界面后关闭导航页面（导航界面只见一次）
        finish();
    }

    /**
     * 弹出对话框，提示用户更新
     */
    protected void showUpdateDialog() {
//        对话框依赖于activity而存在
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        对话框左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("版本更新");
//        设置描述内容
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                下载apk
                downloadAPK();

            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterHome();
            }
//            取消对话框，进入主界面
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                enterHome();

                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 更新下载,放置apk位置
     */
    private void downloadAPK() {
//        判断sdk是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            获取sdk路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "mobilesafe.apk";
//            发送请求，获取apk，并放置到指定路径  发送请求，传递参数（下载地址，下载后放置的位置，）
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功的apk
                    File file = responseInfo.result;
//                    提示用户下载
                    installAPK(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    e.printStackTrace();
                }

                //                刚刚开始下载的方法
                @Override
                public void onStart() {
                    super.onStart();
                }

                //              下载过程中的方法（下载apk的总大小，当前的下载为中，会否正在下载）
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.i("M", "下载中...");
                }
            });
        }
    }

    /**
     * 安装对应apk
     *
     * @param file
     */
    private void installAPK(File file) {
//        系统界面,源码,安装入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//        文件作为数据源
        intent.setData(Uri.fromFile(file));
//        设置安装的类型
        intent.setType("application/vnd.android.package-rchive");
        startActivityForResult(intent, 0);
    }

    /**
     * 开启一个activity,返回结果调用的方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initDB() {
//     电话数据库拷贝
        initAddressDB("address.db");
//        常用号码数据库的拷贝过程
        initAddressDB("commonnum.db");
//拷贝病毒数据库
        initAddressDB("antivirus.db");


    }

    /**
     * 归属地数据库初始化,拷贝至Files文件下
     */
    private void initAddressDB(String name) {
//       1. 在Files文件夹下创建同名name数据库文件的过程
        File files = getFilesDir();
        File file = new File(files, name);
        if (file.exists()) {
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
//        2. 输入流读取第三方资产目录下的文件
        try {
            stream = getAssets().open(name);
//            将读取的内容写入到指定文件下
            fos = new FileOutputStream(file);
//            每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs)) != -1) {
                fos.write(bs, 0, temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(stream!=null&&fos!=null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
