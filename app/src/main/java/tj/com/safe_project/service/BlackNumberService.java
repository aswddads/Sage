package tj.com.safe_project.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import tj.com.safe_project.db.dao.BlackNumberDao;
import tj.com.safe_project.utils.ToastUtil;

/**
 * Created by Jun on 17/4/8.
 */

public class BlackNumberService extends Service {

    private InnerSmsReceiver smsReceiver;
    private BlackNumberDao mBlackNumberDao;
    private TelephonyManager mTM;
    private PhoneStateListener mPhoneStateListener;


    @Override
    public void onCreate() {
//        拦截短信
        IntentFilter intentFilter = new IntentFilter();
        smsReceiver = new InnerSmsReceiver();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(2147483647);
        registerReceiver(smsReceiver, intentFilter);
//        监听电话的状态  电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {
//        手动重写，电话状态发生改变会出发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲状态，没有任何活动   移出土司
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机，至少有一个电话活动
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃   挂断电话  aidl文件
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
    class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//          获取短信内容，获取发送短信电话号码，若号码在黑名单，且开启黑名单拦截，则进行拦截

            //        获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
//            循环遍历短信
            for (Object object : objects) {
//                获取短信对象
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
//                获取基本信息
                String originatingAddress = message.getDisplayOriginatingAddress();
                String messageBody = message.getMessageBody();
                mBlackNumberDao = BlackNumberDao.getInstance(context);
                int mode = mBlackNumberDao.getMode(originatingAddress);
                if (mode ==1||mode==3) {
//                    拦截短信
                    ToastUtil.show(getApplicationContext(),"shabi");
                    abortBroadcast();
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
        super.onDestroy();
    }
}
