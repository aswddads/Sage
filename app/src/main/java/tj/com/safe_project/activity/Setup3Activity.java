package tj.com.safe_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.utils.ToastUtil;

/**
 * Created by Jun on 17/3/30.
 */
public class Setup3Activity extends BaseSetupActivity {
    private EditText mEt_phone_number;
    private Button mBt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
    }

    @Override
    protected void showNextPage() {
        //        输入号码
        String et_phone= mEt_phone_number.getText().toString();

//        存储号码
//        String contact_phone=SpUtils.getString(getApplicationContext(),ConstanValue.CONTACT_PHONE,"");
        if (!TextUtils.isEmpty(et_phone)){
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
//            如果为输入的号码，则需要存储到sp中
            SpUtils.putString(getApplicationContext(), ConstanValue.CONTACT_PHONE,et_phone);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(getApplicationContext(),"输入号码有误！");
        }

    }

    @Override
    protected void showPrePage() {
        Intent intent1 = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent1);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    private void initUI() {
//        显示联系人
        mEt_phone_number = (EditText) findViewById(R.id.et_phone_number);
//        获取号码的回显
        String phone=SpUtils.getString(getApplicationContext(),ConstanValue.CONTACT_PHONE,"");
        mEt_phone_number.setText(phone);
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
        if(data!=null){
            //        返回当前界面接收结果
            String phone= data.getStringExtra("phone");
//        过滤号码的特殊字符(将中线转换成空字符)
            phone=phone.replace("-","").replace(" ","").trim();
            mEt_phone_number.setText(phone);
//            将联系人存储至sp中
            SpUtils.putString(getApplicationContext(), ConstanValue.CONTACT_PHONE,phone);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
