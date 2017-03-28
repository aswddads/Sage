package tj.com.safe_project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/3/28.
 */

public class SettingitemView extends RelativeLayout {
    private static String NAMESPACE="http://schemas.android.com/apk/res/tj.com.safe_project";
    private CheckBox cb_box;
    private TextView tv_des;
    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    public SettingitemView(Context context) {
        this(context, null);
    }

    public SettingitemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingitemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        xml->view   将设置界面的条目转换成view对象,直接添加到当前settingitemview对应的view中
//        View.inflate(context,R.layout.setting_item_view,this);
        View view = View.inflate(context, R.layout.setting_item_view, null);
        this.addView(view);
//        自定义组合中控件标题描述
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);
//      获取自定义属性  AttributeSet中获取
        initAttrs(attrs);

        tv_title.setText(mDestitle);

    }

    /**
     * attrs 构造方法中维护好的属性集合
     *
     * @param attrs 返回属性集合中自定义属性的值
     */
    private void initAttrs(AttributeSet attrs) {
//        attrs.getAttributeCount(); 获取属性总个数
//        for (int i = 0; i < attrs.getAttributeCount(); i++) {
//            attrs.getAttributeName(i);
//            attrs.getAttributeValue(i);
//        }
//        通过命名空间＋属性名获取属性值
         mDestitle=attrs.getAttributeValue(NAMESPACE,"destitle");
         mDesoff=attrs.getAttributeValue(NAMESPACE,"desoff");
         mDeson=attrs.getAttributeValue(NAMESPACE,"deson");

    }

    /**
     * @return 返回当前settingview是否选中状态, 在点击过程中调用
     */
    public boolean isCheck() {
        return cb_box.isChecked();
    }

    //    是否开启
    public void setCheck(boolean isCheck) {
        cb_box.setChecked(isCheck);
        if (isCheck) {
            tv_des.setText(mDeson);
        } else {
            tv_des.setText(mDesoff);
        }
    }
}
