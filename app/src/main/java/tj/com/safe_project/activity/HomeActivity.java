package tj.com.safe_project.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.MD5Utils;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.utils.ToastUtil;

/**
 * Created by Jun on 17/3/27.
 */
public class HomeActivity extends Activity {
    private GridView gv_home;
    private String mTitleStr[];
    private int mDrawzbleIds[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
//        初始化数据
        initDtata();
    }

    private void initDtata() {
//        准备数据(文字、图片)
        mTitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };
        mDrawzbleIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmessage_safe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };
//        九宫格设置数据适配器（等同listview）
        gv_home.setAdapter(new MyAdapter());
//        注册点击时间
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //            点中条目的索引
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
//                        开启对话框
                        showDialog();
                        break;
                    case 1:
//                         跳转到通信卫士
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 3:
//                        跳转到进程管理界面
                        startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
                        break;
                    case 5:
//                        跳转到杀毒界面
                        startActivity(new Intent(getApplicationContext(),AnitVirusActivity.class));
                        break;
                    case 7://跳转到高级工具的页面
                        startActivity(new Intent(getApplicationContext(),AToosActivity.class));
                        //finish();
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }


    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    private void showDialog() {
//        判断是否存储密码（sp,字符串）
//        1.设置密码对话框  2.确认密码对话框
        String psd = SpUtils.getString(this, ConstanValue.MOBILE_SAFE_PASSWORD, "");
        if (TextUtils.isEmpty(psd)) {
            showSetPsdDialog();
        } else {
            showConfirmPsdDialog();
        }
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPsdDialog() {
        //        需要自己定义对话框的展示形式  view是由自己转化成的view对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
//        让对话框显示一个自定义的对话框
//        兼容低版本四个角默认的黑色边距
        dialog.setView(view,0,0,0,0);
//        dialog.setView(view);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bu_cancel = (Button) view.findViewById(R.id.bu_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirm_psd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(confirm_psd)) {
//                    进入手机界面    将输入的密码进行md5操作然后与存储的密码进行比对
                    String psd = SpUtils.getString(getApplicationContext(), ConstanValue.MOBILE_SAFE_PASSWORD, "");
                    if (psd.equals(MD5Utils.encoder(confirm_psd))) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码有问题");
                    }
                } else {
//                    输入有误
                    ToastUtil.show(getApplicationContext(), "输入有误");
                }
            }
        });
        bu_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    /**
     * 初次设置密码对话框
     */
    private void showSetPsdDialog() {
//        需要自己定义对话框的展示形式  view是由自己转化成的view对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
//        让对话框显示一个自定义的对话框
//        dialog.setView(view);
        dialog.setView(view,0,0,0,0);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bu_cancel = (Button) view.findViewById(R.id.bu_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirm_psd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirm_psd)) {
//                    进入手机界面
                    if (psd.equals(confirm_psd)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        SpUtils.putString(getApplicationContext(), ConstanValue.MOBILE_SAFE_PASSWORD, MD5Utils.encoder(psd));
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码有问题");
                    }
                } else {
//                    输入有误
                    ToastUtil.show(getApplicationContext(), "输入有误");
                }
            }
        });
        bu_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            返回条目总数
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int i) {
            return mTitleStr[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View mView = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) mView.findViewById(R.id.tv_title);
            ImageView iv_icon = (ImageView) mView.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStr[i]);
            iv_icon.setBackgroundResource(mDrawzbleIds[i]);
            return mView;
        }

    }
}