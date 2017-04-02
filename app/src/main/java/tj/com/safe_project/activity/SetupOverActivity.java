package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/3/29.
 */
public class SetupOverActivity extends Activity {
    private TextView tv_phone;
    private TextView tv_reset_setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over = SpUtils.getBoolean(this, ConstanValue.SETUP_OVER, false);
        if (setup_over) {
            //        密码输入成功，并且导航界面设置完成－－－－－>设置完成功能列表界面
            setContentView(R.layout.activity_setup_over);
            initUI();
        } else {
            //        未设置完成，跳转到导航页面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
//            开启一个新界面，关闭功能列表
            finish();
        }
    }

    private void initUI() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
//        设置联系人
        String phone=SpUtils.getString(this,ConstanValue.CONTACT_PHONE,"");
        tv_phone.setText(phone);
//        重新设置条目被点击
        tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
