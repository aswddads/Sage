package tj.com.safe_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Jun on 17/3/29.
 */
public class TestActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view=new TextView(this);
        view.setText("TestActivity");
        setContentView(view);
    }
}
