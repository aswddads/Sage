package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import tj.com.safe_project.R;
import tj.com.safe_project.service.LockScreenService;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.ServiceUtil;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/4/10.
 */
public class ProcessSettingActivity extends Activity {

    private CheckBox cbShowSystem;
    private CheckBox cb_lock_clear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        initSystemShow();
        initLockScreenClear();
    }

    /**
     * 锁屏清理
     */
    private void initLockScreenClear() {
        cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
//      根据服务的状态回显
        boolean isRunning = ServiceUtil.isRunning(this, "tj.com.safe_project.service.LockScreenService");
        if (isRunning) {
            cb_lock_clear.setText("锁屏清理已开启");
        } else {
            cb_lock_clear.setText("锁屏清理已关闭");
        }
        cb_lock_clear.setChecked(isRunning);
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                是否选中的状态
                if (b) {
                    cb_lock_clear.setText("锁屏清理已开启");
//                    开启服务
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                } else {
                    cb_lock_clear.setText("锁屏清理已关闭");
//                    关闭服务
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        });
    }

    private void initSystemShow() {
        cbShowSystem = (CheckBox) findViewById(R.id.cb_show_system);
//        对存储过的状态进行回显
        boolean showSystem = SpUtils.getBoolean(this, ConstanValue.SHOW_SYSTEM, false);
        cbShowSystem.setChecked(showSystem);
        if (showSystem) {
            cbShowSystem.setText("显示系统进程");
        } else {
            cbShowSystem.setText("隐藏系统进程");
        }
        cbShowSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                是否选中的状态
                if (b) {
                    cbShowSystem.setText("显示系统进程");
                } else {
                    cbShowSystem.setText("隐藏系统进程");
                }
                SpUtils.putBoolean(getApplicationContext(), ConstanValue.SHOW_SYSTEM, b);
            }
        });
    }
}
