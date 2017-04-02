package tj.com.safe_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jun on 17/4/1.
 */
public abstract class BaseSetupActivity extends Activity{
    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //       2   创建手势管理的对象，用作在管理在onTouchEvent(event)传递过来的手势动作
        mGestureDetector=new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            //            监听手势的移动的方法
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 0) {
//              由右向左，移动到下一页      调用子类的下一页方法，抽象方法
                    /**
                     * 第一个界面的时候，跳转到第二个界面
                     * 第二个界面，跳转到第三个界面
                     * ........
                     */
                    showNextPage();
                }
                if (e1.getX() - e2.getX() < 0) {
                    //    由左向右，移动到上一页      调用子类的上一页方法，抽象方法
                    /**
                     * 第一个界面的时候，无响应
                     * 第二个界面，跳转到第一个界面
                     * ........
                     */
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //    1  监听屏幕上响应的事件类型（按下（1），移动（多次），抬起（1））
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//       3 通过手势处理类，接收多种事件类型，用作处理方法
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
//    下一页的抽象方法，由子类决定跳转到哪个界面
    protected abstract void showNextPage();
//    上一页的抽象方法，由子类决定跳转到哪个界面
    protected abstract void showPrePage();

    /**
     * 点击下一页的按钮时候，根据子类的showNextPage方法做相应跳转
     * @param view
     */
    public void nextPage(View view){
        showNextPage();
    }
    /**
     * 点击上一页的按钮时候，根据子类的showPrePage方法做相应跳转
     * @param view
     */
    public void prePage(View view){
        showPrePage();
    }
}