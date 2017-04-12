package tj.com.safe_project.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/4/12.
 */
public class BaseCacheClearActivity extends TabActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_clear_cache);
//        1.生成选项卡
//        TabHost.TabSpec Tab1=getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        ImageView imageView=new ImageView(this);
         imageView.setBackgroundResource(R.drawable.blue_bg);
//        选项卡还可以用布局写  然后转换成view对象   View.inflate
        View view=View.inflate(this,R.layout.test,null);
        TabHost.TabSpec Tab1=getTabHost().newTabSpec("clear_cache").setIndicator(view);

        TabHost.TabSpec Tab2=getTabHost().newTabSpec("back").setIndicator("回主页");
//        告知点中选项卡之后的意图
        Tab1.setContent(new Intent(getApplicationContext(),CacheClearActivity.class));
        Tab2.setContent(new Intent(getApplicationContext(),HomeActivity.class));
//        将选项卡维护到host中去（选项卡宿主中）
        getTabHost().addTab(Tab1);
        getTabHost().addTab(Tab2);
    }
}
