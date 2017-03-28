package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

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
        String psd=SpUtils.getString(this, ConstanValue.MOBILE_SAFE_PASSWORD,"");
        if(TextUtils.isEmpty(psd)){
            showSetPsdDialog();
        }else{
            showConfirmPsdDialog();
        }
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPsdDialog() {
    }

    /**
     * 初次设置密码对话框
     */
    private void showSetPsdDialog() {

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
