package tj.com.safe_project.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import tj.com.safe_project.R;
import tj.com.safe_project.engine.CommonnumDao;

/**
 * Created by Jun on 17/4/10.
 */
public class CommonNumberQueryActivity extends Activity {

    private ExpandableListView elv_common_number;
    private List<CommonnumDao.Group> mGroup;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initUI();
        initData();
    }

    /**
     * 可扩展的listview准备数据
     */
    private void initData() {
        CommonnumDao commonnumDao = new CommonnumDao();
        mGroup = commonnumDao.getGroup();
        mAdapter = new MyAdapter();

        elv_common_number.setAdapter(mAdapter);
//        给扩展的listview注册点击事件
        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                startCall(mAdapter.getChild(i, i1).number);
                return false;
            }
        });
    }

    private void startCall(String number) {
//        开启系统的打电话界面
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void initUI() {
        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
        // elv_common_number.
    }


    class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return mGroup.get(i).childList.size();
        }

        @Override
        public CommonnumDao.Group getGroup(int i) {
            return mGroup.get(i);
        }

        @Override
        public CommonnumDao.Child getChild(int i, int i1) {
            return mGroup.get(i).childList.get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * dip=dp   dpi=ppi(像素密度：每一英寸上分布的像素点的个数)
         *
         * @param i
         * @param b
         * @param view
         * @param viewGroup
         * @return
         */
        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("      " + getGroup(i).name);
            textView.setTextColor(Color.BLUE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//            textView.setTextSize(20);
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_name = (TextView) view1.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view1.findViewById(R.id.tv_number);
            tv_name.setText(getChild(i, i1).name);
            tv_number.setText(getChild(i, i1).number);
            return view1;
        }

        /**
         * 孩子节点是否响应点击事件
         *
         * @param i
         * @param i1
         * @return
         */
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
