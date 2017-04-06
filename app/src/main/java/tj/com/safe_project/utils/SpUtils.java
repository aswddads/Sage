package tj.com.safe_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jun on 17/3/28.
 */

public class SpUtils {
    private static SharedPreferences sp;

    //    写   (上下文环境，存储节点名称，存取节点值boolean)
    public static void putBoolean(Context context, String key, boolean value) {
//        (存储文件节点的名称，读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    //    读
    public static boolean getBoolean(Context context, String key, boolean defvalue) {
//        (存储文件节点的名称，读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
       return sp.getBoolean(key,defvalue);//返回默认值或者读取到的值
    }
    public static void putString(Context context, String key, String value) {
//        (存储文件节点的名称，读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    //    读
    public static String getString(Context context, String key, String defvalue) {
//        (存储文件节点的名称，读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defvalue);//返回默认值或者读取到的值
    }

    /**
     *
     * @param context 上下文环境
     * @param key   移除节点的名称
     */
    public static void remove(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    public static int getInt(Context context,String key,int value) {
        if(sp==null){
            sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
       return sp.getInt(key,value);
    }
    public static void putInt(Context context,String key,int value) {
        if(sp==null){
            sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key,value).commit();
    }
}
