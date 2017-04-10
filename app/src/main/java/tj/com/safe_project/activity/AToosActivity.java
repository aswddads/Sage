package tj.com.safe_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import tj.com.safe_project.R;
import tj.com.safe_project.engine.SmsBackUp;
import tj.com.safe_project.utils.ToastUtil;

/**
 * Created by Jun on 17/4/3.
 */
public class AToosActivity extends Activity{
    private TextView tv_num_address;
    private TextView tv_sms_backup;
    private ProgressBar pb_bar;
    private TextView tv_commonnumber_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

//        归属地查询
        initPhoneAddress();
        initSmsBackUp();//短信备份
//        常用号码查询
        initCommonNumberQuery();
        initAppLock();

    }

    private void initAppLock() {
        TextView tv_lock= (TextView) findViewById(R.id.tv_lock);
        tv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(),AppLock));
                ToastUtil.show(getApplicationContext(),"未实现");
            }
        });
    }

    private void initCommonNumberQuery() {
        tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
        tv_commonnumber_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
    }

    private void initSmsBackUp() {
        tv_sms_backup= (TextView) findViewById(R.id.tv_sms_backup);
       // pb_bar = (ProgressBar) findViewById(R.id.pb_bar);   进度条
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
//        1. 创建一个带进度条的对话框
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle("短信备份");
//        指定进度条样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平样式
        progressDialog.show();
//        短信获取   调用备份短信方法  是一个耗时操作
        new Thread(){
            @Override
            public void run() {
                String path=Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"smsstj.xml";
                SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
//                        由开发者自己决定使用对话框或者进度条
                        progressDialog.setMax(max);
                      //  pb_bar.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                       // pb_bar.setProgress(index);
                        progressDialog.setProgress(index);
                    }
                });
                progressDialog.dismiss();
            }
        }.start();
    }

    private void initPhoneAddress() {
        tv_num_address=(TextView) findViewById(R.id.tv_num_address);
        tv_num_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
