package tj.com.safe_project.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Jun on 17/4/5.
 */

public class ServiceUtil {

    private static ActivityManager mAM;

    /**
     * @param context 是为了获取上下文环境，普通的类中没有getSystemService
     * @param serviceName 判断服务是否正在运行
     * @return  true  在运行
     */
    public static boolean isRunning(Context context,String serviceName){
//        获取activityMannger对象，可以获取当前正在运行的服务
        mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        获取手机中正在运行的服务集合(多少个服务)
        List<ActivityManager.RunningServiceInfo> runningServiceInfos=mAM.getRunningServices(100);
//        遍历服务集合，拿到每一个服务名称，和传递进来的做对比，如果一致，说明正在运行
        for (ActivityManager.RunningServiceInfo runningServiceInfo:runningServiceInfos) {
//            获取每一个正在运行的服务的类的名称
            if(serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
