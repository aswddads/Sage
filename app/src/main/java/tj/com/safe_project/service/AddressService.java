package tj.com.safe_project.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import tj.com.safe_project.R;
import tj.com.safe_project.engine.AdressDao;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/4/5.
 */
public class AddressService extends Service {
    private TelephonyManager mTM;
    private PhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWM;
    private String mAddress;
    private TextView tv_toast;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private int[] mDrawableIds;
    private int mScreenHeight;
    private int mScreenWidth;
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Override
    public void onCreate() {
//         电话状态的监听（服务开启的时候监听，关闭的时候不需要监听）
//        第一次开启服务，就需要去管理土司的显示，电话状态的监听：1.找电话管理者  2.监听电话状态
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        获取窗体对象，用于挂载自定义toast
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight=mWM.getDefaultDisplay().getHeight();
        mScreenWidth=mWM.getDefaultDisplay().getWidth();

//        监听拨出电话的广播接收者  过滤条件
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
//        创建广播接收者（需要权限）
        mInnerOutCallReceiver = new InnerOutCallReceiver();
//        注册
        registerReceiver(mInnerOutCallReceiver,intentFilter);
        super.onCreate();
    }
    private class InnerOutCallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
//      接收到此广播后，需要显示自定义的土司，显示播出归属地号码
//           获取播出号码的api
            String phone=getResultData();
            showToast(phone);
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
//        手动重写，电话状态发生改变会出发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲状态，没有任何活动   移出土司
//                    挂断电话，从窗体移出土司
                    if (mWM != null && mViewToast != null) {
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机，至少有一个电话活动
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃   展示土司
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber)  {
//        Toast.makeText(this,incomingNumber,Toast.LENGTH_SHORT).show();
        final WindowManager.LayoutParams params = mParams;
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
//        土司显示效果（土司布局文件）xml->view  将土司挂载在window窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

        mViewToast.setOnTouchListener(new View.OnTouchListener() {
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
                        if (params.x>mScreenWidth-mViewToast.getWidth()){
                            params.x=mScreenWidth-mViewToast.getWidth();
                        }
                        if (params.y>mScreenHeight-mViewToast.getHeight()-22){
                            params.y=mScreenHeight-mViewToast.getHeight()-22;
                        }

//                        告知窗体土司需要按照手势移动显示
                        mWM.updateViewLayout(mViewToast,params);

                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:

                        SpUtils.putInt(getApplicationContext(),ConstanValue.LOCATION_X,params.x);
                        SpUtils.putInt(getApplicationContext(),ConstanValue.LOCATION_Y,params.y);

                        break;
                }
//                return false;//返回false  不相应事件
                /**
                 * 既要响应点击事件，又要响应拖拽过程，则此返回结果需要为false
                 */
                return true;
            }
        });





//        读取sp中存储土司位置的x，y做标值(params.x,params.y)左上角坐标
        params.x=SpUtils.getInt(getApplicationContext(),ConstanValue.LOCATION_X,0);
        params.y=SpUtils.getInt(getApplicationContext(),ConstanValue.LOCATION_Y,0);

//        从sp中获取色值文字索引，匹配图片，用作展示
        mDrawableIds = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_gren};
        int toastStyleIndex=SpUtils.getInt(getApplicationContext(), ConstanValue.TOAST_STYLE,0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);
//        在窗体上挂载view需要权限
        mWM.addView(mViewToast, params);
//        获取来电号码，需要查询归属地
        query(incomingNumber);
    }

    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AdressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
//        取消对电话状态的监听(开启服务时候监听的对象,不监听)
        if (mTM != null && mPhoneStateListener != null) {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if(mInnerOutCallReceiver!=null){
//            注销
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
