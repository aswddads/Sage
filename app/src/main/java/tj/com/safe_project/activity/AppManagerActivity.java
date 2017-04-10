package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tj.com.safe_project.R;
import tj.com.safe_project.beam.AppInfo;
import tj.com.safe_project.engine.AppInfoProvider;
import tj.com.safe_project.utils.ToastUtil;

import static android.text.format.Formatter.formatFileSize;

/**
 * Created by Jun on 17/4/9.
 */
public class AppManagerActivity extends Activity {

    private StatFs statFs;
    private List<AppInfo> mAppInfoList;
    private ListView lv_app_list;
    private MyAdapter mAdapter;
    private List<AppInfo> mSystemList;
    private List<AppInfo> mCustomerList;
    private TextView tv_des;
    private AppInfo mAppInfo;
    private PopupWindow popupWindow;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new MyAdapter();
            lv_app_list.setAdapter(mAdapter);
            if (tv_des != null && mCustomerList != null) {
                tv_des.setText("用户应用（" + mCustomerList.size() + ")");
            }
        }
    };
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
            return mSystemList.size() + mCustomerList.size() + 2;
        }

        @Override
        public AppInfo getItem(int i) {
            if (i == 0 || i == mCustomerList.size() + 1) {
                return null;
            } else {
                if (i < mCustomerList.size() + 1) {
                    return mCustomerList.get(i - 1);
                } else {
//                返回系统应用对应条目的对象
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
                    holder.tv_title.setText("用户应用（" + mCustomerList.size() + "）");
                } else {
                    holder.tv_title.setText("系统应用（" + mSystemList.size() + "）");
                }
                return view;
            } else {
//           展示图片加文字
                ViewHolder holder = null;
                if (view == null) {
                    view = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                    holder.tv_path = (TextView) view.findViewById(R.id.tv_path);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(i).icon);
                holder.tv_name.setText(getItem(i).name);
                if (getItem(i).isSystem) {
                    holder.tv_path.setText("系统应用");
                } else {
                    holder.tv_path.setText("手机应用");
                }
                return view;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initTitle();
        initList();
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == mCustomerList.size() + 1 || i == 0) {
                    return;
                } else {
                    if (i < mCustomerList.size() + 1) {
                        mAppInfo = mCustomerList.get(i - 1);
                    } else {
                        mAppInfo = mSystemList.get(i - mCustomerList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void initList() {
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        tv_des = (TextView) findViewById(R.id.tv_des);
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//滚动过程中调用方法  i 第一个可见条目  i1 当前一个屏幕可见条目数  i2  条目总数   AbsListView为lisview对象
                if (mCustomerList != null && mSystemList != null) {
                    if (i >= mCustomerList.size() + 1) {
//                    滚动到类系统条目
                        tv_des.setText("系统应用（" + mSystemList.size() + ")");
                    } else {
//                    滚动到了用户条目
                        tv_des.setText("手机应用（" + mCustomerList.size() + ")");
                    }
                }
            }
        });
    }

    protected void showPopupWindow(View view) {
        View popupView = View.inflate(getApplicationContext(), R.layout.popupwindow_layout, null);
        TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAppInfo.isSystem) {
                    ToastUtil.show(getApplicationContext(), "不能卸载系统应用");
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageNmae()));
                    startActivity(intent);
                }
                if (popupWindow!=null){
                    popupWindow.dismiss();
                }

            }
        });
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                通过桌面启动应用
                PackageManager pm = getPackageManager();
//                开启指定包名
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageNmae());
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    ToastUtil.show(getApplicationContext(), "此应用不能开启");
                }
                if (popupWindow!=null){
                    popupWindow.dismiss();
                }
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            //            分享  第三方平台
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用，名称为："+mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                if (popupWindow!=null){
                    popupWindow.dismiss();
                }
            }
        });

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

//         缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);

//        动画集合
        AnimationSet animationSet = new AnimationSet(true);

//        添加两个动画
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);


        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        设置一个透明背景，为了响应回退
        popupWindow.setBackgroundDrawable(new ColorDrawable());
//        指定窗体位置   view  即为点中条目的对象
        popupWindow.showAsDropDown(view, 200, -view.getHeight());

        popupView.startAnimation(animationSet);
    }

    private void initTitle() {
        TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
//        获取磁盘(内存,区分手机运行内存)可用大小
        String path = Environment.getDataDirectory().getAbsolutePath();
//        获取sd卡可用大小
        String sPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        获取以上两个路径的可用大小
        String memoryAvailSpace = formatFileSize(this, getAvailSpace(path));
        String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sPath));
        tv_memory.setText("磁盘可用:" + memoryAvailSpace);
        tv_sd_memory.setText("sd卡可用:" + sdMemoryAvailSpace);
    }

    //  int 代表多少个G
    private long getAvailSpace(String path) {
//        获取可用磁盘大小类
        statFs = new StatFs(path);
//        获取可用区块的个数
        long count = statFs.getAvailableBlocks();
//        获取区块的大小
        long size = statFs.getBlockSize();
//        可用区块个数＊区块大小＝可用空间
        return count * size;//1bytes=8bit
    }

    @Override
    protected void onResume() {
//        重新获取数据
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplication());
                mSystemList = new ArrayList<AppInfo>();
                mCustomerList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem) {
//                      系统应用
                        mSystemList.add(appInfo);
                    } else {
//                        手机应用
                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();

        super.onResume();
    }
}
