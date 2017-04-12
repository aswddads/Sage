package tj.com.safe_project.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import tj.com.safe_project.beam.AppInfo;

/**
 * Created by Jun on 17/4/9.
 */

public class AppInfoProvider {

    /**
     * 返回当前手机所有应用的相关信息（名称，包名，图标，（内存，sd卡），（系统，用户））
     * ctx  获取包管理者的上下文
     */
    public static List<AppInfo> getAppInfoList(Context ctx) {
//     1  包管理者对象
        PackageManager pm = ctx.getPackageManager();
//        2  获取安装在手机上应用相关信息的集合
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> appInfoList=new ArrayList<AppInfo>();
//        3 遍历应用信息的集合
        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();
//            获取应用的包名
            appInfo.packageNmae = packageInfo.packageName;
//            应用名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
//            applicationInfo.uid  每一个应用的唯一标识
            appInfo.name = applicationInfo.loadLabel(pm).toString()+applicationInfo.uid;
//            获取图标
            appInfo.icon=applicationInfo.loadIcon(pm);
//          判断是否为系统应用（每个手机的flag值不同）
            if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
//                系统应用
                appInfo.isSystem=true;
            }else{
                //      非系统应用
                appInfo.isSystem=false;
            }
//            是否为sd卡中安装的应用
            if((applicationInfo.flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
//                sd卡应用
                appInfo.isSdCard=true;
            }else{
                //      非sd卡应用
                appInfo.isSdCard=false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
