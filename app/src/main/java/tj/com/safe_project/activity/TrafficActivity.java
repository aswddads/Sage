package tj.com.safe_project.activity;

import android.app.Activity;
import android.os.Bundle;

import tj.com.safe_project.R;

/**
 * Created by Jun on 17/4/12.
 */
public class TrafficActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
////        获取流量（R 代表接收）  下载流量
//        long mobileRxBytes=TrafficStats.getMobileRxBytes();
////        (T total(手机总流量(上传＋下载)))
//        long mobileTxPackets=TrafficStats.getMobileTxPackets();
////         下载流量总和（手机 流量＋wifi）
//        long totalRxBytes=TrafficStats.getTotalRxBytes();
////        手机总流量（手机＋wifi）上传＋下载
//        long totalTxBytes1=TrafficStats.getTotalTxBytes();
//
////        流量获取模块 需要发送短信   或者从运营商的api接口发送请求
        setContentView(R.layout.activity_traffic);
    }
}
