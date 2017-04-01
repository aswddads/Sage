package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/3/30.
 */
public class Setup3Activity extends Activity {
    private EditText mEt_phone_number;
    private Button mBt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
    }

    private void initUI() {
//        显示联系人
        mEt_phone_number = (EditText) findViewById(R.id.et_phone_number);
//        选择联系人
        mBt_select_number = (Button) findViewById(R.id.bt_select_number);
        mBt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ContactListActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        返回当前界面接收结果
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void prePage(View view) {
        Intent intent1 = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent1);
        finish();
    }

    public void nextPage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
        startActivity(intent);
        finish();
    }
}
