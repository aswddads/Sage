package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import tj.com.safe_project.R;
import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/3/30.
 */
public class Setup4Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }
    public void prePage(View view){
        Intent intent1=new Intent(getApplicationContext(),Setup3Activity.class);
        startActivity(intent1);
        finish();
    }
    public void nextPage(View view){
        Intent intent=new Intent(getApplicationContext(),SetupOverActivity.class);
        startActivity(intent);
        finish();
        SpUtils.putBoolean(this, ConstanValue.SETUP_OVER,true);
    }
}
