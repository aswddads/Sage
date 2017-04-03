package tj.com.safe_project.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import tj.com.safe_project.R;
import tj.com.safe_project.service.LocationService;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/4/2.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        判断是否开启防盗保护
        boolean open_security = SpUtils.getBoolean(context, ConstanValue.OPEN_SECURITY, false);
        if (open_security) {
            //        获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
//            循环遍历短信
            for (Object object : objects) {
//                获取短信对象
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
//                获取基本信息
                String originatingAddress = message.getDisplayOriginatingAddress();
                String messageBody = message.getMessageBody();
//                判断是否播放音乐的关键字
                if(messageBody.contains("#*alarm*#")){
//                    播放音乐（MediaPlayer）
                    MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.alarm);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if(messageBody.contains("#*location*#")){
//                    开启获取位置的服务
                    context.startService(new Intent(context,LocationService.class));
                }
                if(messageBody.contains("#*lockscreen*#")){
//                    锁屏的相关代码
                }
                if(messageBody.contains("#*wipedata*#")){
//                    清除数据
                }
            }
        }
    }
}
