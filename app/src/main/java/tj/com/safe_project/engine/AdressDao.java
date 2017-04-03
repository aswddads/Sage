package tj.com.safe_project.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jun on 17/4/3.
 */

public class AdressDao {
    //    1.指定访问数据库的路径
    public static String path = "data/data/tj.com.safe_project/files/address.db";

    private static String mAddress = "未知号码";

    //    2.开启数据库的链接(只读方式)进行访问(传递一个电话号码)
    public static String getAddress(String phone) {
        mAddress = "未知号码";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

/**
 * 正则表达式匹配手机号码
 */
        String regulaxExpression = "^1[3-8]\\d{9}";
        if (phone.matches(regulaxExpression)) {


            phone = phone.substring(0, 7);
//        查询
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
//        3.查询即可
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
//            4.通过data1查询到的结果作为结果查询data2
                Cursor indexCursor = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                if (indexCursor.moveToNext()) {
//                获取查询的归属地
                    mAddress = indexCursor.getString(0);
                } else {
                    mAddress = "未知号码";
                }
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "紧急号码";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务号码";
                    break;
                case 7:
                    mAddress = "本地号码";
                    break;
                case 8:
                    mAddress = "本地号码";
                    break;
                case 11:
//                    (3+8)  区号（3位）＋座机（外地） 查询data2
                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, "area=?", new String[]{area}, null, null, null);
                    if (cursor.moveToNext()) {
                        mAddress = cursor.getString(0);
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
                case 12:
//                    区号（4位）＋座机
                    String area1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        mAddress = cursor1.getString(0);
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
            }
        }
        return mAddress;
    }
}
