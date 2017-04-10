package tj.com.safe_project.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tj.com.safe_project.R;
import tj.com.safe_project.beam.ProcessInfo;

/**
 * Created by Jun on 17/4/9.
 */

public class ProcessInfoProvider {
//    获取进程总数
    public static int getProcessCount(Context context){
//        获取activityManager
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcess=am.getRunningAppProcesses();
//        返回集合的总数
        return runningAppProcess.size();
    }

    /**
     *
     * @param context
     * @return  返回可用内存数为bytes
     */
    public static long getAvailSpace(Context context){
        //        获取activityManager
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //构建存储可用内存对象
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        // 给memoryInfo赋值
        am.getMemoryInfo(memoryInfo);
//        获取memoryInfo中相应可用内存
       return memoryInfo.availMem;
    }
    /**
     * 4.2版本以上
     */
//    public static long getTotalSpace(Context context){
//        //        获取activityManager
//        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        //构建存储可用内存对象
//        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
//        // 给memoryInfo赋值
//        am.getMemoryInfo(memoryInfo);
////        获取memoryInfo中相应可用内存
//        return memoryInfo.totalMem;
//    }

    /**
     *
     * @param context
     * @return
     */
    public static long getTotalSpace(Context context){
//        内存大小都有写入文件  读取proc/meminfo文件  读取第一行，获取的字符转换为bytes返回

        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        try {
            fileReader=new FileReader("proc/meminfo");
            bufferedReader=new BufferedReader(fileReader);
            String lineOne=bufferedReader.readLine();
//            将字符串转换成字符的数组
            char[] charArray=lineOne.toCharArray();
//            循环每一个字符  如果其ascll码在0～9，说明此字符有效
            StringBuffer stringBuffer=new StringBuffer();
            for (char c:charArray) {
                if (c>='0'&&c<='9'){
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileReader!=null&&bufferedReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     *
     * @param ctx  上下文环境
     * @return  当前运行进程的信息
     */
    public static List<ProcessInfo> getProcessInfo(Context ctx){
        List<ProcessInfo> processInfoList=new ArrayList<ProcessInfo>();
//      获取进程相关信息
//        获取activityManager管理者对象和packageManager管理者对象
        ActivityManager am= (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm=ctx.getPackageManager();
//        获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcess=am.getRunningAppProcesses();
//        遍历，获取相关信息
        for (ActivityManager.RunningAppProcessInfo info:runningAppProcess) {
            ProcessInfo processInfo=new ProcessInfo();
//            获取进程的名称＝＝应用的包名
             processInfo.packageName=info.processName;
//            获取进程占用的内存大小（传递一个进程对应的pid数组）
            Debug.MemoryInfo[] processMemoryInfo=am.getProcessMemoryInfo(new int[]{info.pid});
//            返回数组中索引位置为0的对象，为当前进程的内存信息的对象
            Debug.MemoryInfo memoryInfo=processMemoryInfo[0];
//            获取已使用大小
            processInfo.memSize=memoryInfo.getTotalPrivateDirty()*1024;
//            获取应用名称
            try {
                ApplicationInfo applicationInfo=pm.getApplicationInfo(processInfo.packageName,0);
                processInfo.name=applicationInfo.loadLabel(pm).toString();
//                获取应用图标
                processInfo.icon=applicationInfo.loadIcon(pm);
//                判断是否为系统进程(状态机)
                if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem=true;
                }else {
                    processInfo.isSystem=false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //需要处理
                processInfo.name=info.processName;
                processInfo.icon=ctx.getResources().getDrawable(R.drawable.ic_launcher);
                processInfo.isSystem=true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }

    /**
     *  杀死进程的方法
     * @param ctx  上下文
     * @param processInfo    杀死进程的javaBeam对象
     */
    public static void killProcess(Context ctx,ProcessInfo processInfo) {
//        获取activityManager管理者对象
        ActivityManager am= (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
//        杀死指定包名的进程(权限)
        am.killBackgroundProcesses(processInfo.packageName);
    }

    /**
     *   杀死去全部进程
     * @param ctx  上下文环境
     */
    public static void killAll(Context ctx) {
        //        获取activityManager
        ActivityManager am= (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //        获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcess=am.getRunningAppProcesses();
//        循环进程，杀死
        for (ActivityManager.RunningAppProcessInfo info:runningAppProcess) {
//            除了自身进程
            if (info.processName.equals(ctx.getPackageName())){
                continue;
            }else{
                am.killBackgroundProcesses(info.processName);
            }
        }
    }
}
