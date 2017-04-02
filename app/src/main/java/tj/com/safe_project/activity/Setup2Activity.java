package tj.com.safe_project.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.utils.ToastUtil;
import tj.com.safe_project.view.SettingitemView;

/**
 * Created by Jun on 17/3/30.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingitemView mSiv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUI();
    }

    @Override
    protected void showNextPage() {

        String serialNum = SpUtils.getString(this, ConstanValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(serialNum)) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请绑定sim卡");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent1 = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent1);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        mSiv_sim_bound = (SettingitemView) findViewById(R.id.siv_sim_bound);
//        回显，读取绑定状态
        String sim_number = SpUtils.getString(getApplicationContext(), ConstanValue.SIM_NUMBER, "");
//        判断是否序列号为空
        if (TextUtils.isEmpty(sim_number)) {
            mSiv_sim_bound.setCheck(false);
        } else {
            mSiv_sim_bound.setCheck(true);
        }
        mSiv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                获取原有状态,
                boolean ischeck = mSiv_sim_bound.isCheck();
                mSiv_sim_bound.setCheck(!ischeck);
                if (!ischeck) {
//                    存储  获取sim卡序列号TelePhoneManger
                    TelephonyManager telephone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = telephone.getSimSerialNumber();
//                    存储
                    SpUtils.putString(getApplicationContext(), ConstanValue.SIM_NUMBER, simSerialNumber);
                } else {
//                    将存储序列号的节点，从sp删除
                    SpUtils.remove(getApplicationContext(), ConstanValue.SIM_NUMBER);
                }
            }
        });
    }
}
