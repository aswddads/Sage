package tj.com.safe_project.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tj.com.safe_project.beam.BlackNumberInfo;
import tj.com.safe_project.db.BlackNumberOpenHelper;

/**
 * Created by Jun on 17/4/8.
 */

public class BlackNumberDao {
    private  BlackNumberOpenHelper blackNumberOpenHelper;
    //    BlackNumberDao单例模式
/**
 * 1.私有化构造方法
 * 2.声明当前类的对象
 * 3.提供一个静态方法，如果当前对象为空，创建一个新的
 */
    private BlackNumberDao(Context context){
//        创建数据库及其表结构
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    private static BlackNumberDao blackNumberDao=null;
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao==null){
             blackNumberDao=new BlackNumberDao(context);
        }
        return blackNumberDao;
    }
//    增加一个条目

    /**
     *
     * @param phone  拦截电话
     * @param mode   拦截模式   1.短信   2.电话   3.所有
     */
    public void insert(String phone,String mode){
//        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);
        db.close();
    }

    /**
     * 从数据库中删除一条电话号码
     * @param phone   删除的号码
     */
    public void delete(String phone){
        //        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        db.delete("blacknumber","phone = ?",new String[]{phone});
        db.close();
    }

    /**
     *
     * @param phone  更新号码
     * @param mode   更新的模式
     */
    public void update(String phone,String mode){
        //        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("mode",mode);
        db.update("blacknumber",values,"phone=?",new String[]{phone});
        db.close();
    }

    /**
     *
     * @return  查询数据库所有类型
     */
    public List<BlackNumberInfo> findAll(){
        //        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor=db.query("blacknumber",new String[]{"phone","mode"},null,null,null,null,"_id desc");
        List<BlackNumberInfo> blackNumberInfoList=new ArrayList<BlackNumberInfo>();
        /**
         * 写代码要注意细节    上次把while写成了if
         */
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.phone= cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfoList;
    }

    /**
     * 每次查询20条
     * @param index  查询的索引值
     */
    public List<BlackNumberInfo> find(int index){
        //        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;",new String[]{index+""});
        List<BlackNumberInfo> blackNumberInfoList=new ArrayList<BlackNumberInfo>();
        /**
         * 写代码要注意细节    上次把while写成了if
         */
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.phone= cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfoList;
    }

    /**
     *
     * @return   数据库数据总条数
     */
    public int getCount(){
        int count=0;
        //        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from blacknumber",null);
        if(cursor.moveToNext()){
            count=cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     *
     * @param phone   作为查询条件的电话号码
     * @return      传入电话号码的拦截模式
     */
    public int getMode(String phone){
        //        1.开启数据库
        SQLiteDatabase db=blackNumberOpenHelper.getReadableDatabase();
        int mode=0;
        Cursor cursor=db.query("blacknumber",new String[]{"mode"},"phone=?",new String[]{phone},null,null,null);
        if(cursor.moveToNext()){
            mode=cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
