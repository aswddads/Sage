package tj.com.safe_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.utils.ToastUtil;

/**
 * Created by Jun on 17/3/30.
 */
public class Setup4Activity extends BaseSetupActivity {
    private CheckBox mCb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();
    }

    @Override
    protected void showNextPage() {
        boolean open_security = SpUtils.getBoolean(this, ConstanValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtils.putBoolean(this, ConstanValue.SETUP_OVER, true);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(), "未开启防盗设置");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent1 = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent1);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        mCb_box = (CheckBox) findViewById(R.id.cb_bo);
//        1.是否选择的回显过程
        boolean open_security = SpUtils.getBoolean(this, ConstanValue.OPEN_SECURITY, false);
//        2.根据状态修改chexbox的文字显示
        mCb_box.setChecked(open_security);//回显chexbox的状态
        if (open_security) {
            mCb_box.setText("安全设置已开启");
        } else {
            mCb_box.setText("安全设置已关闭");
        }
//        点击过程中，chexbox的状态切换，以及对应的切换后状态的储存
//        mCb_box.setChecked(!mCb_box.isChecked());
        mCb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                b为点击chexbox之后的状态，并存储
                SpUtils.putBoolean(getApplicationContext(), ConstanValue.OPEN_SECURITY, b);
//             修改显示状态
                if (b) {
                    mCb_box.setText("安全设置已开启");
                } else {
                    mCb_box.setText("安全设置已关闭");
                }
            }
        });
    }
}
