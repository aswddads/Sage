package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tj.com.safe_project.R;
import tj.com.safe_project.engine.VirusDao;
import tj.com.safe_project.utils.MD5Utils;

/**
 * Created by Jun on 17/4/10.
 */
public class AnitVirusActivity extends Activity {

    private static final int SCANNING = 100;
    private static final int FINISH = 101;
    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pd_bar;
    private LinearLayout ll_add_text;
    private RotateAnimation rotateAnimation;
    private List<ScanInfo> virusscanInfoList;



    private int index = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANNING:
//                    显示正在扫描的名称、在线性布局添加一个正在扫描的textview
                    ScanInfo info= (ScanInfo) msg.obj;
                    tv_name.setText(info.name);
                    TextView textView=new TextView(getApplicationContext());
                    if (info.isVirus){
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒："+info.name);
                    }else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全:"+info.name);
                    }
                    ll_add_text.addView(textView,0);
                    break;
                case FINISH:
//                    顶部文字：扫描完成   动画停止
                    tv_name.setText("扫描完成");
                    iv_scanning.clearAnimation();
//                    告知用户卸载病毒程序
                    uninstallVirus();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void uninstallVirus() {
        for (ScanInfo scanInfo:virusscanInfoList){
            String packageName=scanInfo.packageName;
//            源码
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anit_virus);
        initUI();
        initAnimation();
        checkVirus();
    }

    /**
     * 检测病毒
     */
    private void checkVirus() {

        new Thread() {
            @Override
            public void run() {
//                获取数据库中所有病毒md5码
                List<String> virusList = VirusDao.getVirusList();
                //         获取手机上面的所有应用程序签名文件的md5码
//        1  获取包的管理者对象
                PackageManager pm = getPackageManager();
//        2  获取所有应用程序的签名文件
// (PackageManager.GET_SIGNATURES  代表已安装的应用的签名文件＋PackageManager.GET_UNINSTALLED_PACKAGES  卸载了的签名文件)
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES
                        + PackageManager.GET_UNINSTALLED_PACKAGES);
                virusscanInfoList = new ArrayList<ScanInfo>();
                List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();

//                设置进度条最大值
                pd_bar.setMax(packageInfoList.size());

                //        3  遍历
                for (PackageInfo info : packageInfoList) {
                    ScanInfo scanInfo = new ScanInfo();
                    Signature[] signatures = info.signatures;
//            获取签名文件数组的第一位,然后进行md5，将此md5和数据库中的md5进行比对
                    Signature signature = signatures[0];
                    String string = signature.toCharsString();
                    String encoder = MD5Utils.encoder(string);//32位字符串，16进制
//                    比对是否为病毒
                    if (virusList.contains(encoder)) {
//                        记录病毒
                        scanInfo.isVirus = true;
                        virusscanInfoList.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.packageName = info.packageName;
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfoList.add(scanInfo);
//                    在扫描的过程中更新进度条
                    index++;
                    pd_bar.setProgress(index);
//                    在子线程中发送消息  更新ui（顶部textview 、线性布局的view）
                    try {
                        Thread.sleep(10 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.what = SCANNING;
                    message.obj = scanInfo;
                    mHandler.sendMessage(message);
                }
                Message message = Message.obtain();
                message.what = FINISH;
                mHandler.sendMessage(message);
                super.run();
            }
        }.start();
    }

    class ScanInfo {
        public boolean isVirus;
        public String packageName;
        public String name;
    }

    private void initAnimation() {
        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);//指定动画一直旋转
        iv_scanning.startAnimation(rotateAnimation);
    }

    private void initUI() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_name = (TextView) findViewById(R.id.tv_name);
        pd_bar = (ProgressBar) findViewById(R.id.pd_bar);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
    }
}
