package tj.com.safe_project.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import tj.com.safe_project.R;
import tj.com.safe_project.service.AddressService;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.ServiceUtil;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.view.SettingClickView;
import tj.com.safe_project.view.SettingitemView;

/**
 * Created by Jun on 17/3/28.
 */
public class SettingActivity extends Activity {
    private SettingitemView siv_update;
    private SettingitemView siv_address;
    private String[] mToastStyleDes;
    private int mToast_style;
    private SettingClickView scv_toast_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
        initAddress();
        initToastStyle();
        initLocation();
    }

    /**
     * 双击居中view所在屏幕位置的处理方法
     */
    private void initLocation() {
        SettingClickView scv_location= (SettingClickView) findViewById(R.id.scv_location);
        scv_location.setTitle("归属地提示位置");
        scv_location.setDes("设置归属地提示位置");
        scv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("电话归属地显示风格");
//       1. 创建描述文字所在的string类型数组
        mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
//        2.sp获取toast样式的索引值（int），用于描述文字
        mToast_style = SpUtils.getInt(this, ConstanValue.TOAST_STYLE,0);
//        获取字符串数组中文字显示在tv_des
        scv_toast_style.setDes(mToastStyleDes[mToast_style]);
//        监听点击事件，弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToastStyleDialog();
            }
        });
    }

    /**
     * 创建选中样式的对话框
     */
    protected void showToastStyleDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择归属地样式");
        //选择单个条目事件监听（string类型的数组（描述颜色的数组），弹出对画框选中条目索引值，点击某一个条目触发的点击事件）
        /**
         * 本处可以使用setSingleItems设置单选，但是我测试时发现不能正常显示被选状态  有待改进
         */
        builder.setItems(mToastStyleDes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {//i  选中的索引值
                //        点击事件：1.记录选中的索引值  2，关闭对话框   3，显示选中的色值文字
                SpUtils.putInt(getApplicationContext(),ConstanValue.TOAST_STYLE,i);
                dialogInterface.dismiss();
                scv_toast_style.setDes(mToastStyleDes[i]);
            }
        });
//        消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 显示电话号码归属地
     */
    private void initAddress() {
        siv_address = (SettingitemView) findViewById(R.id.siv_address);
//        对是否开启状态进行显示
        boolean isRunning=ServiceUtil.isRunning(this,"tj.com.safe_project.service.AddressService");
        siv_address.setCheck(isRunning);
//        点击过程中，状态的切换
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheck = siv_address.isCheck();//返回之前的状态
                siv_address.setCheck(!isCheck);
                if (!isCheck) {
//                    开启服务管理土司
                    startService(new Intent(getApplicationContext(),AddressService.class));
                }else{
//                    关闭服务，不需要显示土司
                    stopService(new Intent(getApplicationContext(),AddressService.class));
                }
            }
        });
    }

    private void initUpdate() {
        siv_update = (SettingitemView) findViewById(R.id.siv_update);
//        获取已有的开关状态，用作显示
        boolean open_update = SpUtils.getBoolean(getApplicationContext(), ConstanValue.OPEN_UPDATE, false);
//        根据上次的存贮取是否选中
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                如果之前选中，点击变补选中
//                获取选中状态
                boolean isCheck = siv_update.isCheck();
//                将原有状态取反
                siv_update.setCheck(!isCheck);
                SpUtils.putBoolean(getApplicationContext(), ConstanValue.OPEN_UPDATE, !isCheck);
            }
        });
    }
}
