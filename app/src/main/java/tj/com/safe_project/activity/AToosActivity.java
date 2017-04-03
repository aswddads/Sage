package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/4/3.
 */
public class AToosActivity extends Activity{
    private TextView tv_num_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

//        归属地查询
        initPhoneAddress();

    }

    private void initPhoneAddress() {
        tv_num_address=(TextView) findViewById(R.id.tv_num_address);
        tv_num_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
