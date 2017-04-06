package tj.com.safe_project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/3/28.
 */

public class SettingClickView extends RelativeLayout {
    private TextView tv_des;
    private TextView tv_title;


    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        xml->view   将设置界面的条目转换成view对象,直接添加到当前settingitemview对应的view中
//        View.inflate(context,R.layout.setting_item_view,this);
        View view = View.inflate(context, R.layout.setting_click_view, null);
        this.addView(view);
//        自定义组合中控件标题描述
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
    }

    /**
     *
     * @param title 设置标题内容
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    /**
     *
     * @param des 设置描述内容
     */
    public void setDes(String des){
        tv_des.setText(des);
    }
}
