package tj.com.safe_project.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import tj.com.safe_project.R;
import tj.com.safe_project.engine.AdressDao;

/**
 * Created by Jun on 17/4/3.
 */
public class QueryAddressActivity extends Activity{

    private TextView mBt_query_result;
    private Button mBt_query;
    private EditText mEt_phone;
    private String mAddress;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            控件使用查询结果
            mBt_query_result.setText(mAddress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        initUi();
    }

    private void initUi() {
        mEt_phone = (EditText) findViewById(R.id.et_phone);
        mBt_query = (Button) findViewById(R.id.bt_query);
        mBt_query_result = (TextView)findViewById(R.id.tv_query_result);

//        点开查询功能
        mBt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=mEt_phone.getText().toString();
                if(!TextUtils.isEmpty(phone)){
                    //                查询是耗时操作
                    query(phone);
                }else{
//                    抖动输入框
                    Animation shake= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    mEt_phone.startAnimation(shake);
                    /**
                     * interpolator插补器，数学函数  ,自定义插补器
                     */
//                    shake.setInterpolator(new Interpolator() {
////                        y=ax+b
//                        @Override
//                        public float getInterpolation(float v) {
//                            return 0;
//                        }
//                    });
////                    Interpolator
////                    CycleInterpolator
                    /**
                     * 手机震动效果(vibrator)
                     */
                    Vibrator vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);//震动时间
                    vibrator.vibrate(new long[]{500,1000,500,1000},-1);//规律震动（震动规则(不震动时间，震动时间)，重复次数）
                }
            }
        });
//        实时查询（监听输入框文本变化）
        mEt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phone=mEt_phone.getText().toString();
                query(phone);
            }
        });
    }

    /**  是一个耗时操作
     *   获取电话号码的归属地
     * @param phone  查询的电话号码
     */
    private void query(final String phone){
        new Thread(){
            @Override
            public void run() {
               mAddress =AdressDao.getAddress(phone);
//                消息机制，告知主线程，可以使用查询结果
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
