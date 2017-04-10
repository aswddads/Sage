package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tj.com.safe_project.R;
import tj.com.safe_project.beam.ProcessInfo;
import tj.com.safe_project.engine.ProcessInfoProvider;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.utils.ToastUtil;

/**
 * Created by Jun on 17/4/9.
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_process_count;
    private TextView tv_memory_nfo;
    private TextView tv_des;
    private ListView lv_process_list;
    private Button bt_all;
    private Button bt_reverse;
    private Button bt_clear;
    private Button bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;
    private ProcessInfo mProcessInfo;


    private MyAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new MyAdapter();
            lv_process_list.setAdapter(mAdapter);

            if (tv_des != null && mCustomerList != null) {
                tv_des.setText("用户进程（" + mCustomerList.size() + ")");
            }
        }
    };
    private long availSpace;
    private long totalSpace;

    class MyAdapter extends BaseAdapter {
        //        获取适配器中条目类型总数  修改成两种（纯文本、图片＋文本）
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }
        //        listview添加两个描述条目

        //        指定索引指向的条目类型  条目类型状态码指定（0（复用系统），1）
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return 0;//纯文本
            } else {
                return 1;//图片加文本
            }
        }

        @Override
        public int getCount() {
            if (SpUtils.getBoolean(getApplicationContext(), ConstanValue.SHOW_SYSTEM, false)) {
                return mSystemList.size() + mCustomerList.size() + 2;
            } else {
                return mCustomerList.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int i) {
            if (i == 0 || i == mCustomerList.size() + 1) {
                return null;
            } else {
                if (i < mCustomerList.size() + 1) {
                    return mCustomerList.get(i - 1);
                } else {
//                返回系统进程对应条目的对象
                    return mSystemList.get(i - mCustomerList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
//展示文字条目
                ViewTitleHolder holder = null;
                if (view == null) {
                    view = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                    view.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) view.getTag();
                }
                if (i == 0) {
                    holder.tv_title.setText("用户进程（" + mCustomerList.size() + "）");
                } else {
                    holder.tv_title.setText("系统进程（" + mSystemList.size() + "）");
                }
                return view;
            } else {
//           展示图片加文字
                ViewHolder holder = null;
                if (view == null) {
                    view = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView) view.findViewById(R.id.tv_memory_info);
                    holder.cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(i).icon);
                holder.tv_name.setText(getItem(i).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(i).memSize);
                holder.tv_memory_info.setText(strSize);
//本进程不能被选中，将其隐藏
                if (getItem(i).packageName.equals(getPackageName())) {
                    holder.cb_box.setVisibility(View.GONE);
                } else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(i).isCheck);

                return view;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initUI();
        initTitleData();
        initListData();
    }

    private void initListData() {
        new Thread() {
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplication());
                mSystemList = new ArrayList<ProcessInfo>();
                mCustomerList = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : mProcessInfoList) {
                    if (info.isSystem) {
//                      系统进程
                        mSystemList.add(info);
                    } else {
//                        手机进程
                        mCustomerList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数：" + mProcessCount);

//        获取可用内存大小，并且格式化
        availSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, availSpace);

//        获取总内存大小，并且格式化
        totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, totalSpace);

        tv_memory_nfo.setText("剩余／总共：" + strAvailSpace + "/" + strTotalSpace);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_nfo = (TextView) findViewById(R.id.tv_memory_info);

        tv_des = (TextView) findViewById(R.id.tv_des);


        lv_process_list = (ListView) findViewById(R.id.lv_process_list);

        bt_all = (Button) findViewById(R.id.bt_all);
        bt_reverse = (Button) findViewById(R.id.bt_reverse);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//滚动过程中调用方法  i 第一个可见条目  i1 当前一个屏幕可见条目数  i2  条目总数   AbsListView为lisview对象
                if (mCustomerList != null && mSystemList != null) {
                    if (i >= mCustomerList.size() + 1) {
//                    滚动到类系统条目
                        tv_des.setText("系统进程（" + mSystemList.size() + ")");
                    } else {
//                    滚动到了用户条目
                        tv_des.setText("手机进程（" + mCustomerList.size() + ")");
                    }
                }
            }
        });
        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                view 是选中条目的对象，通过其可找到控件checkbox  切换状态
                if (i == mCustomerList.size() + 1 || i == 0) {
                    return;
                } else {
                    if (i < mCustomerList.size() + 1) {
                        mProcessInfo = mCustomerList.get(i - 1);
                    } else {
                        mProcessInfo = mSystemList.get(i - mCustomerList.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.packageName.equals(getPackageName())) {
//                            选中条目与本应用的包名不一致，才需要去设置取反，和设置单选框
//                            状态取反
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
//                            显示状态
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_all:
                SelectAll();
                break;
            case R.id.bt_reverse:
                SelectReverse();
                break;
            case R.id.bt_clear:
                clearAll();
                break;
            case R.id.bt_setting:
                setting();
                break;
        }

    }

    private void setting() {
        Intent intent= new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       通知适配器刷新
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 清理选中进程
     */
    private void clearAll() {
//        获取选中进程
        //                被选中   创建一个记录需要杀死进程的集合
        List<ProcessInfo> KillProcessList = new ArrayList<ProcessInfo>();
        for (ProcessInfo info : mCustomerList) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (info.isCheck) {
//                记录需要杀死的进程
                KillProcessList.add(info);
            }
        }
        for (ProcessInfo info : mSystemList) {
            if (info.isCheck) {
//                记录需要杀死的进程
                KillProcessList.add(info);
            }
        }
//        遍历KillProcessList，去移除mCustomerList和mSystemList需要杀死的进程
        long totalReleaseSpace = 0;
        for (ProcessInfo processInfo : KillProcessList) {
//            判断当前进程在哪个集合 并移除
            if (mCustomerList.contains(processInfo)) {
                mCustomerList.remove(processInfo);
            }
            if (mSystemList.contains(processInfo)) {
                mSystemList.remove(processInfo);
            }
//            杀死记录在KillProcessList中的进程
            ProcessInfoProvider.killProcess(this, processInfo);
//          记录释放空间的大小
            totalReleaseSpace += processInfo.memSize;
        }
//        集合改变后，通知适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
//        进程总数的更新
        mProcessCount = mProcessCount - KillProcessList.size();
//        更新可用剩余空间
        availSpace = totalReleaseSpace + availSpace;
        tv_process_count.setText("进程总数" + mProcessCount);
        tv_memory_nfo.setText("剩余／总共" + Formatter.formatFileSize(this, availSpace) + "／" + totalSpace);

        /**
         * 还可以通过占位符进行土司描述
         */
        ToastUtil.show(this, "杀死了" + KillProcessList.size() + "个进程" + ",释放了" +
                Formatter.formatFileSize(this, totalReleaseSpace) + "空间");
    }

    private void SelectReverse() {
        //        1  将所有集合中对象的ischeck的对象设置取反   要过滤掉当前应用
        for (ProcessInfo info : mCustomerList) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            } else {
                info.isCheck = !info.isCheck;
            }
        }
        for (ProcessInfo info : mSystemList) {
            info.isCheck = !info.isCheck;
        }
//        通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void SelectAll() {
//        1  将所有集合中对象的ischeck的对象设置为true  代表全选  要过滤掉当前应用
        for (ProcessInfo info : mCustomerList) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            } else {
                info.isCheck = true;
            }
        }
        for (ProcessInfo info : mSystemList) {
            info.isCheck = true;
        }
//        通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
