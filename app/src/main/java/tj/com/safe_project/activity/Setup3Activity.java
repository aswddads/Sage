package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/3/30.
 */
public class Setup3Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }
    public void prePage(View view){
        Intent intent1=new Intent(getApplicationContext(),Setup2Activity.class);
        startActivity(intent1);
        finish();
    }
    public void nextPage(View view){
        Intent intent=new Intent(getApplicationContext(),Setup4Activity.class);
        startActivity(intent);
        finish();
    }
}
