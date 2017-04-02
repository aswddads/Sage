package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/3/31.
 */
public class ContactListActivity extends Activity {
    private ListView mLv_contact;
    private MyAdapter mmAdapter;
    private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            填充数据适配器
            mmAdapter=new MyAdapter();
            mLv_contact.setAdapter(mmAdapter);
        }
    };
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view_item=View.inflate(getApplicationContext(),R.layout.listview_contact_item,null);
            TextView tv_name= (TextView) view_item.findViewById(R.id.tv_name);
            TextView tv_phone= (TextView) view_item.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(i).get("name"));
            tv_phone.setText(getItem(i).get("phone"));
            return view_item;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    private void initData() {
//        读取联系人可能是一个耗时操作，需要放置到子线程进行操作
        new Thread() {
            @Override
            public void run() {
                //        获取系统联系人数据,内容解析器
                ContentResolver contentresolver = getContentResolver();

                Cursor cursor = contentresolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null, null, null);
//                循环游标，直至没有为止
                contactList.clear();
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
//                    Log.i("ContactListActivity",id);
//                    根据用户唯一性id值，查询data表和mimetype表生成的视图，获取data以及mimetype字段
                    Cursor indexCursor = contentresolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id=?", new String[]{id}, null);
//                    循环获取电话和姓名
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);
                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
//                                数据非空
                                hashMap.put("phone", data);
                            }
                        } else {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                    }
                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
//                消息机制,告诉主线程可以使用线程即可，子线程已经完成数据准备
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        mLv_contact = (ListView) findViewById(R.id.lv_contact);
        mLv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                 1.获取点击的索引指向集合中的对象,通过mAdapter对象获取
                if(mmAdapter!=null){
                    HashMap<String,String> hashMap=mmAdapter.getItem(i);
//                    获取当前点击的电话号码,给第三个导航界面使用
                    String phone=hashMap.get("phone");
//                    结束此界面前，将数据返回到上一个界面
                    Intent intent=new Intent();
                    intent.putExtra("phone",phone);
//                    setResult传出的数据在onActivityResult中接收
                    setResult(0,intent);
                    finish();
                }
            }
        });
    }
}
