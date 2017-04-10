package tj.com.safe_project.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tj.com.safe_project.db.AppLockOpenHelper;

/**
 * Created by Jun on 17/4/8.
 */

public class AppLockDao {
    private AppLockOpenHelper appLockOpenHelper;
    //    BlackNumberDao单例模式
/**
 * 1.私有化构造方法
 * 2.声明当前类的对象
 * 3.提供一个静态方法，如果当前对象为空，创建一个新的
 */
    private AppLockDao(Context context){
//        创建数据库及其表结构
        appLockOpenHelper = new AppLockOpenHelper(context);
    }
    private static AppLockDao appLockDao=null;
    public static AppLockDao getInstance(Context context){
        if (appLockDao==null){
            appLockDao=new AppLockDao(context);
        }
        return appLockDao;
    }

    /**
     * 插入方法
     * @param packagename
     */
    public void insert(String packagename){
        SQLiteDatabase db=appLockOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("packagename",packagename);
        db.insert("applock",null,contentValues);
        db.close();
    }

    /**
     * 删除方法
     * @param packagename
     */
    public void delete(String packagename){
        SQLiteDatabase db=appLockOpenHelper.getWritableDatabase();
        db.delete("applock","packagename=?",new String[]{packagename});
        db.close();
    }

    /**
     * 查询所有
     */
    public List<String> findAll(){
        SQLiteDatabase db=appLockOpenHelper.getWritableDatabase();
        Cursor cursor=db.query("applock",new String[]{"packagename"},null,null,null,null,null);
        List<String> lockPackageList=new ArrayList<>();
        while (cursor.moveToNext()){
            lockPackageList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lockPackageList;
    }
}
