package tj.com.safe_project.global;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Jun on 17/4/12.
 */

public class MyApplication  extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
//        捕获全局异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
//                 在获取到了未捕获的异常后，处理的方法
                throwable.printStackTrace();
//                将捕获到的异常存储到sd卡
                String path= Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator+"error.log";
                File file=new File(path);
                try {
                    PrintWriter printWriter=new PrintWriter(file);
                    throwable.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//            异常捕获    上传到服务器
                System.exit(0);
            }
        });
    }
}
