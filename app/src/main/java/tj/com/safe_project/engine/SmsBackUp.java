package tj.com.safe_project.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jun on 17/4/9.
 */

public class SmsBackUp {
    private static int index = 0;

    //    备份短信  ProgressDialog pd  对话框
//    A  传递一个进度条所在对话框
//    B   传递一个进度条
    public static void backup(Context context, String path, CallBack callBack) {
        FileOutputStream fos = null;
        Cursor cursor = null;
        //需要用到的对象：上下文环境，备份文件夹的路径，进度条所在的对话框用于更新进度
//      1.获取备份短信写入的文件
        try {
            File file = new File(path);
//        2.获取内容解析器,获取短信数据库的数据
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
//        3.文件的输出流
            fos = new FileOutputStream(file);
//            4.序列化数据库中读取的数据,放置到xml中
            XmlSerializer newSerializer = Xml.newSerializer();
//            5.给出xml做相应配置
            newSerializer.setOutput(fos, "utf-8");
            newSerializer.startDocument("utf-8", true);
            newSerializer.startTag(null, "smss");
//          6  备份短信总数指定
//            A  传进来是对话框，指定对话框进度条的总是
//            B   传进来是进度条，指定进度条的总数
//            pd.setMax(cursor.getCount());
            if (callBack!=null){
                callBack.setMax(cursor.getCount());
            }

//            7.读取数据库中每一条数据写入xml
            while (cursor.moveToNext()) {
                newSerializer.startTag(null, "sms");

                newSerializer.startTag(null, "address");
                newSerializer.text(cursor.getString(0));
                newSerializer.endTag(null, "address");

                newSerializer.startTag(null, "date");
                newSerializer.text(cursor.getString(1));
                newSerializer.endTag(null, "date");

                newSerializer.startTag(null, "type");
                newSerializer.text(cursor.getString(2));
                newSerializer.endTag(null, "type");

                newSerializer.startTag(null, "body");
                newSerializer.text(cursor.getString(3));
                newSerializer.endTag(null, "body");

                newSerializer.endTag(null, "sms");

                index++;
                Thread.sleep(300);//让用户看到备份过程
//                8 每循环一次，跟新进度条   ProgressDialog可以在子线程更新相应的进度条改变
//            A  传进来是对话框，指定对话框进度条的百分比
//            B   传进来是进度条，指定进度条的百分比
//                pd.setProgress(index);
                if (callBack!=null){
                    callBack.setProgress(index);
                }
            }
            newSerializer.endTag(null, "smss");
            newSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null && cursor != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cursor.close();
            }
        }
    }

    /**
     * 回调
     * 1.定义一个接口
     * 2.定义接口未实现业务逻辑方法（短信总数设置，备份过程中短信百分比的更新）
     * 3.传递一个实现了此接口的类的对象，（至备份短信的工具类中），接口实现类，一定实现了上述未实现的方法（就决定了该使用什么）
     * 4.获取传递进来的对象,在合适的地方（设置总数，设置百分比）做方法的调用，
     */
    public interface CallBack {
//        短信总数未实现方法（由自己决定对话框活着进度条调用）
        public void setMax(int max);
//备份过程百分比未实现的方法
        public void setProgress(int index);
    }
}
