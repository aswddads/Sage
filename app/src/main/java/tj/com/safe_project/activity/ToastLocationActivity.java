package tj.com.safe_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/4/6.
 */
public class ToastLocationActivity extends Activity {

    private Button mIvDrag;
    private Button mBtTop;
    private Button mBtBottom;
    private WindowManager mWM;
    private int screenHeight;
    private int screenWidth;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUI();
    }

    private void initUI() {
//        可拖拽双击居中的控件
        mIvDrag = (Button) findViewById(R.id.iv_drag);
        mBtTop = (Button) findViewById(R.id.bt_top);
        mBtBottom = (Button) findViewById(R.id.bt_bottom);

        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHeight = mWM.getDefaultDisplay().getHeight();
        screenWidth = mWM.getDefaultDisplay().getWidth();

        int locationX = SpUtils.getInt(getApplicationContext(), ConstanValue.LOCATION_X, 0);
        int locationY = SpUtils.getInt(getApplicationContext(), ConstanValue.LOCATION_Y, 0);

//      左上角坐标作用于控件上    Button在相对布局中，所以其所在位置的规则需要由相对布局提供
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);//指定宽高
//        左上角坐标作用于控件参数上
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;

//      将以上规则作用于控件上
        mIvDrag.setLayoutParams(layoutParams);

        if (layoutParams.topMargin > screenHeight / 2) {
            mBtBottom.setVisibility(View.INVISIBLE);
            mBtTop.setVisibility(View.VISIBLE);
        } else {
            mBtBottom.setVisibility(View.VISIBLE);
            mBtTop.setVisibility(View.INVISIBLE);
        }

//        双击事件的监听
        mIvDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//   1.原数组（要被拷贝的数组） 2.原数组的拷贝起始位置索引值 3.目标数组（原数组的数据－－拷贝－－>目标数组）
//   4.目标数组接受值的起始索引值   5.拷贝长度
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
//                    双击居中
                    int left = screenWidth / 2 - mIvDrag.getWidth() / 2;
                    int top = screenHeight / 2 - mIvDrag.getHeight() / 2;
                    int right = screenWidth / 2 + mIvDrag.getWidth() / 2;
                    int bottom = screenHeight / 2 + mIvDrag.getHeight() / 2;
//                    控件按以上显示
                    mIvDrag.layout(left, top, right, bottom);
//                    存储最终位置
                    SpUtils.putInt(getApplicationContext(), ConstanValue.LOCATION_X, mIvDrag.getLeft());
                    SpUtils.putInt(getApplicationContext(), ConstanValue.LOCATION_Y, mIvDrag.getTop());
                }
            }
        });

//        监听某一个控件的拖拽过程（按下，移动（多次触发），抬起）
        mIvDrag.setOnTouchListener(new View.OnTouchListener() {

            private int startY;
            private int startX;

            //            对不同事件做不同的处理
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        int startX= (int) motionEvent.getX();//触发点相距当前控件原点的距离（即左上角）
                        //点击触发点距离原点的距离
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) motionEvent.getRawX();
                        int moveY = (int) motionEvent.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;
//                        1  当前控件所在屏幕的的（左、上）角的位置
//                        mIvDrag.getLeft();//当前控件左与屏幕左边缘的距离
//                        mIvDrag.getTop();//当前控件与屏幕上边缘的距离
                        int left = mIvDrag.getLeft() + disX;//左侧坐标
                        int top = mIvDrag.getTop() + disY;//顶部坐标
                        int right = mIvDrag.getRight() + disX;//右侧左边
                        int bottom = mIvDrag.getBottom() + disY;//底部坐标

//                        容错处理(mIvdrag不能拖出屏幕)
//                        1  左边缘不能超出屏幕
                        if (left < 0) {
                            return true;
                        }
                        //   2  右边缘不能超出屏幕

                        if (right > screenWidth) {
                            return true;
                        }
//                        3  上边缘不能超出屏幕
                        if (top < 0) {
                            return true;
                        }
                        //                        4  下边缘不能超出屏幕   屏幕高度－22＝底边缘显示最大值
/**
 *   还有另外一种算法，就是先减去22dp，然后计算控件距离上下两个控件的距离的中心与屏幕的中心位置作比较
 */
                        if (bottom > screenHeight - 22) {
                            return true;
                        }
                        if (top > screenHeight / 2) {
                            mBtBottom.setVisibility(View.INVISIBLE);
                            mBtTop.setVisibility(View.VISIBLE);
                        } else {
                            mBtBottom.setVisibility(View.VISIBLE);
                            mBtTop.setVisibility(View.INVISIBLE);
                        }


//                        2  告知移动的控件，按计算出来的坐标去做展示
                        mIvDrag.layout(left, top, right, bottom);
//                        3   重置起始坐标
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
//                        存储移动到的位置
                        SpUtils.putInt(getApplicationContext(), ConstanValue.LOCATION_X, mIvDrag.getLeft());
                        SpUtils.putInt(getApplicationContext(), ConstanValue.LOCATION_Y, mIvDrag.getTop());

                        break;
                }
//                return false;//返回false  不相应事件
                /**
                 * 既要响应点击事件，又要响应拖拽过程，则此返回结果需要为false
                 */
                return false;
            }
        });
    }
}
