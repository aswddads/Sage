package tj.com.safe_project.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/4/2.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//      1  获取开机后号码序列号
      TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String serialNumber=tm.getSimSerialNumber();
//       2  sp中存储的序列卡号
        String simNumber=SpUtils.getString(context, ConstanValue.SIM_NUMBER,"");
//        对比
        if(!serialNumber.equals(simNumber)){
//            发送给选中联系人
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage("",null,"sim change!!!",null,null);
        }else{

        }
    }
}
