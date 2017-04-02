package tj.com.safe_project.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import tj.com.safe_project.utils.ConstanValue;
import tj.com.safe_project.utils.SpUtils;

/**
 * Created by Jun on 17/4/2.
 */
public class LocationService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
//        获取手机经纬度坐标
//        1.获取位置管理者对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        2.以最优的方式获取经纬度坐标
        Criteria criteria = new Criteria();
//        允许花费
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//指定获取经纬度的精确度
        String bestProvider = locationManager.getBestProvider(criteria, true);
//        3.在一定时间间隔，或者一定距离获取经纬度
        MyLocationListener myLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
//            经度
            double longitude=location.getLongitude();
//            纬度
            double latitude=location.getLatitude();
//            发送短信(添加权限)
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(SpUtils.getString(getApplicationContext(), ConstanValue.CONTACT_PHONE,""),null,"longitude="+longitude+"latitude="+latitude,null,null);
            Log.i("LocationService","longitude="+longitude+"latitude="+latitude);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {


        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
