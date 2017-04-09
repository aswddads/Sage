package tj.com.safe_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/4/9.
 */
public class ProcessManagerActivity extends Activity{

    private TextView tv_process_count;
    private TextView tv_memory_nfo;
    private ListView lv_process_list;
    private Button bt_all;
    private Button bt_reverse;
    private Button bt_clear;
    private Button bt_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initUI();
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_nfo = (TextView)findViewById(R.id.tv_memory_info);

        lv_process_list = (ListView) findViewById(R.id.lv_process_list);

        bt_all = (Button) findViewById(R.id.bt_all);
        bt_reverse = (Button) findViewById(R.id.bt_reverse);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){

    }
}
