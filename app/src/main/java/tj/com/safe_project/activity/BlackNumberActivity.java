package tj.com.safe_project.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import tj.com.safe_project.R;
import tj.com.safe_project.beam.BlackNumberInfo;
import tj.com.safe_project.db.dao.BlackNumberDao;
import tj.com.safe_project.utils.ToastUtil;

import static tj.com.safe_project.R.id.iv_delete;
import static tj.com.safe_project.R.id.tv_mode;
import static tj.com.safe_project.R.id.tv_phone;

/**
 * Created by Jun on 17/4/8.
 */
public class BlackNumberActivity extends Activity {

    private ImageView ivAdd;
    private ListView lvBlacknumber;
    private BlackNumberDao mBlackNumberDao;
    private List<BlackNumberInfo> mBlackNumberInfoList;
    private MyAdapter mAdapter;
    private int mode=1;
    private boolean isLoad=false;
    private int mCount;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//    4.告知listview可以去设置数据适配器
            if (mAdapter==null){
                mAdapter = new MyAdapter();
                lvBlacknumber.setAdapter(mAdapter);
            }else{
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumberInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return mBlackNumberInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            /**
             * 第一种优化listview   复用view
             * 第二种优化   对findViewById次数的优化  使用viewHolder
             *
             * listview如果有多个条目的时候，分页算法  每次加载20条，逆序返回
             */
//            View view1=null;
//            if(view==null){
//               view1 = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
//            }else{
//                view1=view;
//            }
            ViewHolder viewHolder=null;
            if(view==null){
             view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
//                将findViewById过程封装到view＝null的情景下去执行
                viewHolder=new ViewHolder();
                viewHolder.tv_phone = (TextView) view.findViewById(tv_phone);
                viewHolder.tv_mode = (TextView) view.findViewById(tv_mode);
                viewHolder.iv_delete = (ImageView) view.findViewById(iv_delete);
                view.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) view.getTag();
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    1  数据库删除
                    mBlackNumberDao.delete(mBlackNumberInfoList.get(i).phone);
//                    2  集合中删除，通知数据适配器刷新
                    mBlackNumberInfoList.remove(i);
//                    3  通知适配器更新
                    if (mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });


            viewHolder.tv_phone.setText(mBlackNumberInfoList.get(i).phone);
//            tv_phone.setText(mBlackNumberInfoList.get(i).mode);
            int mode = Integer.parseInt(mBlackNumberInfoList.get(i).mode);
            switch (mode) {
                case 1:
                    viewHolder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tv_mode.setText("拦截所有");
                    break;
            }
            return view;
        }
    }
//将viewHolder定义为静态，不会创建多个对象
    static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);
        initUI();
        initData();
    }

    /**
     * 获取数据库中的所有电话号码，是一个耗时操作
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //1.获取操作黑名单数据库的对象
                mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
                //2.查询所有数据
                //mBlackNumberInfoList = mBlackNumberDao.findAll();
                //2.查询部分数据
                mBlackNumberInfoList=mBlackNumberDao.find(0);
                mCount=mBlackNumberDao.getCount();
                //3.通过消息机制告诉主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

//    private void loadData(int index) {
//        //1.获取操作黑名单数据库的对象
//        mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
//        //2.查询所有数据
//        //mBlackNumberInfoList = mBlackNumberDao.findAll();
//        //2.查询部分数据
//        mBlackNumberInfoList=mBlackNumberDao.find(0);
//        //3.通过消息机制告诉主线程可以去使用包含数据的集合
//        mHandler.sendEmptyMessage(0);
//    }


    private void initUI() {
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        lvBlacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
//        监听滚动状态
        lvBlacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
//            滚动过程中状态发生改变调用
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
//                AbsListView.OnScrollListener.SCROLL_STATE_FLING  飞速滚动
//                AbsListView.OnScrollListener.SCROLL_STATE_IDLE;   空闲状态
//                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;  那手触摸滚动
                /**
                 * 条件一  滚动到停止状态
                 * 条件二  最后一个条目可见（最后一个条目的索引值>＝数据适配器中集合的总条目数－1）
                 */
                if(mBlackNumberInfoList!=null){
                    if (i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            &&lvBlacknumber.getLastVisiblePosition()>=mBlackNumberInfoList.size()-1
                            &&!isLoad){
                         //加载下一页数据   isLoad防止重复加载数据   当前正在加载 则为true  加载完毕  赋值为false
//                        如果条目总数大于集合大小时，才可以继续加载更多
                        if (mCount>mBlackNumberInfoList.size()){
                            new Thread() {
                                @Override
                                public void run() {
                                    //1.获取操作黑名单数据库的对象
                                    mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
                                    //2.查询所有数据
                                    //mBlackNumberInfoList = mBlackNumberDao.findAll();
                                    //2.查询部分数据(20)
                                    List<BlackNumberInfo> moreData=mBlackNumberDao.find(mBlackNumberInfoList.size());
                                    //3.添加下一页数据的过程
                                    mBlackNumberInfoList.addAll(moreData);
                                    //4.通知数据适配器刷新
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }
            }
//             滚动过程中调用
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();//自己定义对话框的布局
        View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        dialog.setView(view,0,0,0,0);
        final EditText et_phone1= (EditText) view.findViewById(R.id.et_phone1);
        RadioGroup rg_group= (RadioGroup) view.findViewById(R.id.rg_group);

        Button bt_submit= (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel= (Button) view.findViewById(R.id.bt_cancel);

//        监听选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_sms:
//                        拦截短信
                        mode=1;
                        break;
                    case R.id.rb_phone:
//                        拦截电话
                        mode=2;
                        break;
                    case R.id.rb_all :
//                        拦截所有
                        mode=3;
                        break;
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                1. 获取输入框中的电话号码
                String phone=et_phone1.getText().toString();
                if (!TextUtils.isEmpty(phone)){
//                  2  数据库插入
                    mBlackNumberDao.insert(phone,mode+"");
//                    3  让数据库和集合保持同步（1 数据库中数据重新读一遍  2  手动向集合中添加一个插入数据构建的对象）
                    BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
                    blackNumberInfo.phone=phone;
                    blackNumberInfo.mode=mode+"";

//                    4   将对象插入到集合顶部
                    mBlackNumberInfoList.add(0,blackNumberInfo);
//                    5   通知数据适配器刷新
                    if (mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();

                }else{
                    ToastUtil.show(getApplicationContext(),"输入拦截号码");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
