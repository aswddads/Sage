package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/3/31.
 */
public class ContactListActivity  extends Activity{
    private ListView mLv_contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    private void initData() {
//        读取联系人可能是一个耗时操作，需要放置到子线程进行操作
        new Thread(){
            @Override
            public void run() {
                //        获取系统联系人数据,内容解析器
                ContentResolver contentresolver=getContentResolver();

                Cursor cursor=contentresolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,null,null);
//                循环游标，直至没有为止
                while(cursor.moveToNext()){
                    String id=cursor.getString(0);
                    Log.i("ContactListActivity",id);
                }
                cursor.close();
            }
        }.start();

    }

    private void initUI() {
        mLv_contact= (ListView) findViewById(R.id.lv_contact);
    }
}
