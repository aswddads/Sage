package tj.com.safe_project.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Jun on 17/3/27.
 */

public class ToastUtil {
    public static void show(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
