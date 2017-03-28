package tj.com.safe_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;
import tj.com.safe_project.view.SettingitemView;

/**
 * Created by Jun on 17/3/28.
 */
public class SettingActivity extends Activity{
    private SettingitemView siv_update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
    }

    private void initUpdate() {
        siv_update= (SettingitemView) findViewById(R.id.siv_update);
//        获取已有的开关状态，用作显示
        boolean open_update=SpUtils.getBoolean(getApplicationContext(), ConstanValue.OPEN_UPDATE,false);
//        根据上次的存贮取是否选中
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                如果之前选中，点击变补选中
//                获取选中状态
                boolean isCheck=siv_update.isCheck();
//                将原有状态取反
                siv_update.setCheck(!isCheck);
                SpUtils.putBoolean(getApplicationContext(),ConstanValue.OPEN_UPDATE,!isCheck);
            }
        });
    }
}
