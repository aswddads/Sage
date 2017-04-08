package tj.com.safe_project.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import tj.com.safe_project.R;


/**
 * Created by Jun on 17/4/8.
 */
public class RocketService extends Service{
    private int mScreenHeight;
    private int mScreenWidth;
    private WindowManager mWM;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mRocketView;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            params.y=(Integer) msg.obj;
//            告知窗体更新火箭view的位置
            mWM.updateViewLayout(mRocketView,params);
        }
    };
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight=mWM.getDefaultDisplay().getHeight();
        mScreenWidth=mWM.getDefaultDisplay().getWidth();
//        开启火箭
        showRocket();
        super.onCreate();
    }

    private void showRocket() {
//        自定义土司
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  默认能够被触摸
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
//        在响铃的时候显示土司，和电话一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
//        指定土司的指定的位置（左上角）
        params.gravity = Gravity.LEFT + Gravity.TOP;
//        定义土司所在布局，并将其转换为view对象，添加至窗体
        mRocketView = View.inflate(this, R.layout.rockrt_view,null);
//        背景开启动画
       ImageView iv_rocket= (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable animationDrawable= (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();
        mWM.addView(mRocketView, params);
        mRocketView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX=(int)motionEvent.getRawX();
                        startY=(int)motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) motionEvent.getRawX();
                        int moveY = (int) motionEvent.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x=disX;
                        params.y=disY;
                        if (params.x<0){
                            params.x=0;
                        }
                        if (params.y<0){
                            params.y=0;
                        }
                        if (params.x>mScreenWidth-mRocketView.getWidth()){
                            params.x=mScreenWidth-mRocketView.getWidth();
                        }
                        if (params.y>mScreenHeight-mRocketView.getHeight()-22){
                            params.y=mScreenHeight-mRocketView.getHeight()-22;
                        }

//                        告知窗体土司需要按照手势移动显示
                        mWM.updateViewLayout(mRocketView, params);

                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x>100&& params.x<200&& params.y>350){
//                            发射火箭
                            sendRocket();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void sendRocket() {
        new Thread(){
            @Override
            public void run() {
               //        在向上移动过程中，一直减少y轴的值
                for (int i=0;i<11;i++){
                    int height=350-i*35;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg=Message.obtain();
                    msg.obj=height;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(mWM!=null&&mRocketView!=null){
            mWM.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
