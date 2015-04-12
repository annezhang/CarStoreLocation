package com.example.locationcarstore;

import java.util.ArrayList;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.locationcarstore.MyApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBUtil {

    private static String SID = "sId";
    private static String CITY = "city";
    private static String NAME = "name";
    private static String QUALIFI = "qualifi";
    private static String ADDR = "addr";
    private static String TEL = "tel";
    private static String CREDIT = "credit";
    private static String IS4S = "is4s";
    private static String MAJOR = "major";
    private static String LAT = "lat";
    private static String LNG = "lng";
    private static String TABLE_STORE = "repairstore";
    private Context context;
    private DBManager mHelper;
    private SQLiteDatabase mDB;
    private static DBUtil instance;

    private ArrayList<StoreClass> list;

    // private int offset = 0; private LatLng currentll;

    public static final synchronized DBUtil getInstance(Context c) {
        if (instance == null) {
            instance = new DBUtil(c.getApplicationContext());
        }
        return instance;
    }

    private DBUtil(Context ctx) {
        context = ctx.getApplicationContext();
        mHelper = new DBManager(ctx);
        mHelper.openDatabase();
        mHelper.closeDatabase();
        list = new ArrayList<StoreClass>();
    }

    public ArrayList<StoreClass> getAllStoreItems() {
        mHelper.openDatabase();
        mDB = mHelper.getDatabase();
        // String args[] = { cityName };
        // ArrayList<StoreClass> list = new ArrayList<StoreClass>();

        String sqlString = "SELECT *" + " FROM " + TABLE_STORE;
        Cursor cursor = mDB.rawQuery(sqlString, null);

        Log.i("DBUtil", "cursor.count = " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            int sId = cursor.getColumnIndex(SID);
            int city = cursor.getColumnIndex(CITY);
            int name = cursor.getColumnIndex(NAME);
            int qualifi = cursor.getColumnIndex(QUALIFI);
            int addr = cursor.getColumnIndex(ADDR);
            int tel = cursor.getColumnIndex(TEL);
            int credit = cursor.getColumnIndex(CREDIT);
            int is4s = cursor.getColumnIndex(IS4S);
            int major = cursor.getColumnIndex(MAJOR);
            int lat = cursor.getColumnIndex(LAT);
            int lng = cursor.getColumnIndex(LNG);
            do {
                StoreClass store = new StoreClass();
                // store.sId = cursor.getInt(sId);
                store.city = cursor.getString(city);
                store.name = cursor.getString(name);
                store.qualifi = cursor.getString(qualifi);
                store.addr = cursor.getString(addr);
                store.tel = cursor.getString(tel);
                store.credit = cursor.getString(credit);
                store.is4s = cursor.getString(is4s);
                store.major = cursor.getString(major);
                store.lat = cursor.getInt(lat);
                store.lng = cursor.getInt(lng);
                list.add(store);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mHelper.closeDatabase();
        mDB.close();
        return list;
    }

    public ArrayList<StoreClass> getStoreItemsByCity(String cityName, int offset) {
        mHelper.openDatabase();
        mDB = mHelper.getDatabase();
        String args[] = {cityName};
        // ArrayList<StoreClass> list = new ArrayList<StoreClass>();

        String sqlString =
                "SELECT *" + " FROM " + TABLE_STORE + " WHERE " + CITY + " = ?" + " ORDER BY "
                        + CREDIT + " DESC"; // +
                                            // " LIMIT "
                                            // +
                                            // Constants.NUM_PER_PAGE
                                            // +
                                            // " OFFSET "
                                            // + offset;
        Cursor cursor = mDB.rawQuery(sqlString, args);

        Log.i("DBUtil", "cursor.count = " + cursor.getCount());

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            int sId = cursor.getColumnIndex(SID);
            int city = cursor.getColumnIndex(CITY);
            int name = cursor.getColumnIndex(NAME);
            int qualifi = cursor.getColumnIndex(QUALIFI);
            int addr = cursor.getColumnIndex(ADDR);
            int tel = cursor.getColumnIndex(TEL);
            int credit = cursor.getColumnIndex(CREDIT);
            int is4s = cursor.getColumnIndex(IS4S);
            int major = cursor.getColumnIndex(MAJOR);
            int lat = cursor.getColumnIndex(LAT);
            int lng = cursor.getColumnIndex(LNG);
            do {
                StoreClass store = new StoreClass();
                // store.sId = cursor.getInt(sId);
                store.city = cursor.getString(city);
                store.name = cursor.getString(name);
                store.qualifi = cursor.getString(qualifi);
                store.addr = cursor.getString(addr);
                store.tel = cursor.getString(tel);
                store.credit = cursor.getString(credit);
                store.is4s = cursor.getString(is4s);
                store.major = cursor.getString(major);
                store.lat = cursor.getInt(lat);
                store.lng = cursor.getInt(lng);
                if (MyApplication.isGetCurrentAddr) {
                    store.distance = CaculateDistance(store.lat, store.lng);
                    Log.i("caculateDistance", " distance = " + store.distance);
                }
                list.add(store);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mHelper.closeDatabase();
        mDB.close();
        return list;
    }

    public double CaculateDistance(int lat, int lng) {

        double latd = lat / 1E6;
        double lngd = lng / 1E6;
        Log.i("caculateDistance", "latd = " + latd + "  lngd = " + lngd);
        double distance = DistanceUtil.getDistance(MyApplication.currentll, new LatLng(latd, lngd));

        return distance / 1000;

    }

    public void addlatlng(String addr, int lat, int lng) {
        mHelper.openDatabase();
        mDB = mHelper.getDatabase();
        String args[] = {addr};

        ContentValues values = new ContentValues();
        values.put(LAT, lat);
        values.put(LNG, lng);

        // Which row to update, based on the ID
        String selection = ADDR + " LIKE ?";
        // String[] selectionArgs = { String.valueOf(rowId) };

        int count = mDB.update(TABLE_STORE, values, selection, args);
        Log.i("addll", addr + "add ll successfull !   " + count);
        mHelper.closeDatabase();
        mDB.close();
    }

}
