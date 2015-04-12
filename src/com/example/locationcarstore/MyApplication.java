package com.example.locationcarstore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.locationcarstore.DBUtil;
import com.example.locationcarstore.StoreClass;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

    public LocationClient mLocationClient;
    public GeofenceClient mGeofenceClient;
    public MyLocationListener mMyLocationListener;

    public String currentAddrStr = "正在获取当前位置...";
    public int currentAddrLat;
    public int currentAddrLng;

    public static boolean isGetCurrentAddr = false;
    public static LatLng currentll = null;
    public static ArrayList<StoreClass> storeslist;
    public static List<StoreClass> storeslistOnShow;
    public static ArrayList<StoreClass> fullStorelist;

    public boolean isFirstCalDis = true;

    @Override
    public void onCreate() {
        super.onCreate();

        // Toast.makeText(getApplicationContext(),
        // "Start !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        setLocationOption(); // 设置定位参数
        mLocationClient.start();
        fullStorelist = new ArrayList<StoreClass>();
        storeslistOnShow = new ArrayList<StoreClass>();
        // storelistBySelection = new ArrayList<StoreClass>();

        DBUtil mDBUtil = DBUtil.getInstance(getApplicationContext());

        fullStorelist = mDBUtil.getStoreItemsByCity("南京", 0);
        storeslist = new ArrayList<StoreClass>(fullStorelist);
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(10000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            Log.i("MyApplication ", "loaction = " + location);
            // Receive Location

            isGetCurrentAddr = true;

            currentAddrStr = location.getAddrStr();
            currentAddrLat = (int) (location.getLatitude() * 1E6);
            currentAddrLng = (int) (location.getLongitude() * 1E6);

            CarStoreActivity.txtview_currentAddr.setText(currentAddrStr);

            currentll = new LatLng(location.getLatitude(), location.getLongitude());

            CaculateDistance();

            if (isFirstCalDis) {
                isFirstCalDis = false;

                SortList();

                for (int i = 0; i < fullStorelist.size(); i++) {
                    storeslist.get(i).distance = fullStorelist.get(i).distance;
                }

            }
            CarStoreActivity.list_store_container.setAdapter(CarStoreActivity.mAdapter);

            CarStoreActivity.mAdapter.notifyDataSetChanged();

            Log.i("MyApplication ", currentAddrStr + "  " + currentAddrLat + currentAddrLng);
        }

    }

    public void SortList() {

        Collections.sort(MyApplication.fullStorelist, new Comparator<StoreClass>() {
            @Override
            public int compare(StoreClass arg0, StoreClass arg1) {
                return arg0.getDistanceOrder().compareTo(arg1.getDistanceOrder());
            }
        });
    }

    public void CaculateDistance() {

        for (int i = 0; i < fullStorelist.size(); i++) {
            double latd = (fullStorelist.get(i).lat) / 1E6;
            double lngd = (fullStorelist.get(i).lng) / 1E6;
            Log.i("caculateDistance", "latd = " + latd + "  lngd = " + lngd);
            double distance = DistanceUtil.getDistance(currentll, new LatLng(latd, lngd));

            fullStorelist.get(i).distance = distance / 1000;
            Log.i("caculateDistance", "");

        }
    }

    public String getCurrentAddr() {

        return currentAddrStr;
    }
}
