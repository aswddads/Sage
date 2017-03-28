package tj.com.safe_project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Jun on 17/3/28.
 */

public class FocusTextView extends TextView {
//    使用通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }
//    由系统调用，（带属性，上下文环境构造方法）
    public FocusTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
//    由系统调用，（带属性，上下文环境构造方法，布局文件中定义样式文件构造方法）
    public FocusTextView(Context context, AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
    }
//    重写获取焦点方法,系统调用默认获取焦点

    @Override
    public boolean isFocused() {
        return true;
    }
}
