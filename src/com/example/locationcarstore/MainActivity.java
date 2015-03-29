package com.example.locationcarstore;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.locationcarstore.LocationApplication.MyLocationListener;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
	
	MapView mMapView = null;  
	BaiduMap mBaiduMap = null;
	public LocationClient mLocationClient = null;
	public BDLocationListener mMyLocationListener = new MyLocationListener();
	
	LatLng point = null;
	BitmapDescriptor bitmap = null;
	OverlayOptions option = null;
	
	Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SDKInitializer.initialize(getApplicationContext()); 
        
        setContentView(R.layout.activity_main);
        
        mContext = this.getApplicationContext();
        
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        
        mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		//option.setCoorType();//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		int span=1000;
		option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
		//option.setIsNeedAddress(checkGeoLocation.isChecked());
		mLocationClient.setLocOption(option);
		
        mLocationClient.start();
        
       // LatLng point = new LatLng(39.963175, 116.400244);  
      //����Markerͼ��  
      bitmap = BitmapDescriptorFactory  
          .fromResource(R.drawable.icon_marka);  
     
    }
    
    public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			
			
			point = new LatLng(location.getLatitude(), location.getLongitude()); 
			option = new MarkerOptions()  
	          .position(point)  
	          .icon(bitmap);  
	      //�ڵ�ͼ�����Marker������ʾ  
			
			
			
	      mBaiduMap.addOverlay(option);
			
		}


	}
    
    



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
