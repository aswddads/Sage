package tj.com.safe_project.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import tj.com.safe_project.engine.ProcessInfoProvider;

/**
 * Created by Jun on 17/4/10.
 */
public class LockScreenService extends Service{

    private IntentFilter intentFilter;
    private InnerReceiver innerReceiver;

    @Override
    public void onCreate() {
//        锁屏对应的action
        intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,intentFilter);
        super.onCreate();
    }

    class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
//      清理手机正在运行的进程
            ProcessInfoProvider.killAll(getApplicationContext());
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (innerReceiver!=null){
            unregisterReceiver(innerReceiver);
        }
        super.onDestroy();
    }
}
