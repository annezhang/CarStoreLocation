package com.example.locationcarstore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AbsListView;
import android.widget.Toast;

public class CarStoreActivity extends Activity implements AbsListView.OnScrollListener {

    private ImageView search_button, map_model;
    public static TextView txtview_currentAddr;
    private Spinner spinner_credit, spinner_carMark, spinner_is4s;
    private ArrayAdapter<CharSequence> spinner_credit_adapter, spinner_carMark_adapter,
            spinner_is4s_adapter;
    public static ListView list_store_container;
    public static ItemListAdapter mAdapter;
    private DBUtil mDBUtil;
    private Geocoder geocoder;

    // public static ArrayList<StoreClass> sotresArray = new
    // ArrayList<StoreClass>();

    int currentFirstVisibleItem = 0;
    int currentVisibleItemCount = 0;
    int totalItemCount = 0;
    int currentScrollState = 0;
    boolean loadingMore = false;
    Long startIndex = 0L;
    // Long offset = 10;
    View footerView;
    static int offset = Constants.NUM_PER_PAGE;
    private static String creditSelection = null;
    private static String is4sSelection = null;
    private static String carMarkSelection = null;
    
    private boolean sCreditfirst = true;
    private boolean sIs4sfirst = true;
    private boolean sCarMarkfirst = true;

//    private boolean isFirstInSpinner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.carstore);

        initView();
        initValue();
    }

    public void initView() {
        search_button = (ImageView) findViewById(R.id.search_button);
        map_model = (ImageView) findViewById(R.id.map_model);

        txtview_currentAddr = (TextView) findViewById(R.id.txtview_currentAddr);

        spinner_credit = (Spinner) findViewById(R.id.spinner_credit);

        spinner_credit_adapter =
                ArrayAdapter.createFromResource(this, R.array.creditOptions,
                        android.R.layout.simple_spinner_dropdown_item);

        spinner_carMark = (Spinner) findViewById(R.id.spinner_carMark);
        spinner_carMark_adapter =
                ArrayAdapter.createFromResource(this, R.array.car_marks,
                        android.R.layout.simple_spinner_dropdown_item);

        spinner_is4s = (Spinner) findViewById(R.id.spinner_is4s);
        spinner_is4s_adapter =
                ArrayAdapter.createFromResource(this, R.array.is4sOptions,
                        android.R.layout.simple_spinner_dropdown_item);

        list_store_container = (ListView) findViewById(R.id.list_store_container);

        footerView =
                ((LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.base_list_item_loading_footer, null, false);

    }

    public void initValue() {

        txtview_currentAddr.setText(((MyApplication) getApplication()).getCurrentAddr());

        spinner_credit.setSelection(-1);
        spinner_credit_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_credit.setSelection(-1);
        spinner_credit.setAdapter(spinner_credit_adapter);
        spinner_credit.setOnItemSelectedListener(new MyOnCredittItemSelectedListener());
        spinner_credit.setSelection(-1);

        spinner_carMark_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_carMark.setAdapter(spinner_carMark_adapter);
        spinner_carMark.setOnItemSelectedListener(new MyOnCarMarkItemSelectedListener());

        spinner_is4s_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_is4s.setAdapter(spinner_is4s_adapter);
        spinner_is4s.setOnItemSelectedListener(new MyOn4sOptionItemSelectedListener());

        geocoder = new Geocoder(this);
        mDBUtil = DBUtil.getInstance(getApplicationContext());

        // fullFillDB();

        // MyApplication.storeslist = mDBUtil.getStoreItemsByCity("南京",offset);
        pullData();

        // Log.i("StoreArray", sotresArray.size() + "");
        // Toast.makeText(getApplicationContext(), "storearray size = " +
        // sotresArray.size(), Toast.LENGTH_SHORT).show();
        mAdapter = new ItemListAdapter(getApplicationContext());
        list_store_container.setAdapter(mAdapter);

        list_store_container.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });

        list_store_container.setOnScrollListener(this);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE
                && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
            /***
             * In this way I detect if there's been a scroll which has completed
             ***/
            /*** do the work for load more date! ***/
            if (!loadingMore) {
                loadingMore = true;
                offset += Constants.NUM_PER_PAGE;
                new LoadMoreItemsTask(this).execute();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;

        // if(lastItem == totalItemCount) {
        // load more data
        // new LoadMoreItemsTask(MainActivity.this).execute();
        // }
    }

    private class LoadMoreItemsTask extends AsyncTask<Void, Void, List<StoreClass>> {

        private Activity activity;
        private View footer;

        private LoadMoreItemsTask(Activity activity) {
            this.activity = activity;
            loadingMore = true;
            footer =
                    activity.getLayoutInflater().inflate(R.layout.base_list_item_loading_footer,
                            null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_store_container.addFooterView(footer);
            // list_store_container.setAdapter(mAdapter);

        }

        @Override
        protected List<StoreClass> doInBackground(Void... voids) {

            // return mDBUtil.getStoreItemsByCity("南京",offset);
            // return (ArrayList<StoreClass>)
            // MyApplication.storeslist.subList(0, offset-1);
            pullData();
            return MyApplication.storeslistOnShow;
        }

        @Override
        protected void onPostExecute(List<StoreClass> list) {
            if (footer != null) {
                list_store_container.removeFooterView(footer);
            }

            loadingMore = false;
            mAdapter.notifyDataSetChanged();
            /*
             * if (stores.size() > 0) { startIndex = startIndex + stores.size();
             * setItems(listItems); }
             */
            super.onPostExecute(list);
        }

    }

    public static void pullData() {

        int tmpoffset =
                MyApplication.storeslist.size() > offset ? offset - 1 : MyApplication.storeslist
                        .size();

        MyApplication.storeslistOnShow =
                MyApplication.storeslist.subList(0, tmpoffset > 0 ? tmpoffset : 0);
    }

    public void fullFillDB() {

        ArrayList<StoreClass> arrys = mDBUtil.getAllStoreItems();
        Log.i("updateDB", "------arrys size--= " + arrys.size());
        for (int i = 0; i < arrys.size(); i++) {
            String addr = arrys.get(i).addr;
            geoaddr(addr, mDBUtil);
        }

        Log.i("updateDB", "------finished !!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void geoaddr(String addr, DBUtil mutil) {
        try {
            List<Address> addrs = geocoder.getFromLocationName(addr, 3);
            if (addrs != null && addrs.size() > 0) {
                int latE6 = (int) (addrs.get(0).getLatitude() * 1E6);
                int lngE6 = (int) (addrs.get(0).getLongitude() * 1E6);

                Log.i("Geocoder", "lat = " + addrs.get(0).getLatitude() + "  lnt = "
                        + addrs.get(0).getLongitude());
                mutil.addlatlng(addr, latE6, lngE6);

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Geocoder", "------geoFromAddress Error-------");
        }
    }

    public class MyOn4sOptionItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
         
            if(sIs4sfirst){
                sIs4sfirst = false;
            }else{
                if (position == 0) {
                    is4sSelection = null;
                } else {
                    // SelectBy4SOption(parent.getItemAtPosition(position).toString());
                    is4sSelection = parent.getItemAtPosition(position).toString();
                }
                SelectCarStoreItems();
            }
            
            
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
            is4sSelection = null;
        }
    }

    /*
     * public void SelectBy4SOption(String option){ MyApplication.storeslist.clear(); for(int i = 0;
     * i<MyApplication.fullStorelist.size()-1;i++){
     * if(MyApplication.fullStorelist.get(i).is4s.contains(option)){
     * MyApplication.storeslist.add(MyApplication.fullStorelist.get(i)); } }
     * 
     * 
     * Log.i("SelectBy4S", "size = " + MyApplication.storeslist.size()); offset =
     * Constants.NUM_PER_PAGE; pullData(); list_store_container.setAdapter(mAdapter);
     * mAdapter.notifyDataSetChanged(); }
     */

    public class MyOnCredittItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            Toast.makeText(parent.getContext(),
                    "the item you chose is ： " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_LONG).show();
            
            if(sCreditfirst){
                sCreditfirst = false;

            }else{
                
                if (position == 0) {
                    creditSelection = null;
                } else {
                    creditSelection = parent.getItemAtPosition(position).toString();
                }
                SelectCarStoreItems();
                
            }            
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
            creditSelection = null;
        }
    }

    public class MyOnCarMarkItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            Toast.makeText(parent.getContext(),
                    "the item you chose is ： " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_LONG).show();

            if(sCarMarkfirst){
                sCarMarkfirst = false;
            }else{
                if (position == 0) {
                    carMarkSelection = null;
                } else {
                    // SelectByCarMark(parent.getItemAtPosition(position).toString());
                    carMarkSelection = parent.getItemAtPosition(position).toString();
                }
                SelectCarStoreItems();
            }
            
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
            carMarkSelection = null;
        }
    }

    /*
     * public void SelectByCarMark(String carMark){
     * 
     * MyApplication.storeslist.clear(); for(int i = 0; i<MyApplication.fullStorelist.size()-1;i++){
     * if(MyApplication.fullStorelist.get(i).major.equals(carMark)){
     * MyApplication.storeslist.add(MyApplication.fullStorelist.get(i)); } }
     * 
     * 
     * Log.i("SelectByCarMark", "size = " + MyApplication.storeslist.size()); offset =
     * Constants.NUM_PER_PAGE; pullData(); list_store_container.setAdapter(mAdapter);
     * mAdapter.notifyDataSetChanged(); }
     */

    public void SelectCarStoreItems() {

        MyApplication.storeslist.clear();
        Log.i("fullStorelist", "size = " + MyApplication.fullStorelist.size());
        Log.i("fullStorelist", "creditSelection = " + creditSelection + " carMarkSelection = "
                + carMarkSelection + " is4sSelection = " + is4sSelection);
        for (StoreClass store : MyApplication.fullStorelist) {
            boolean verif = true;

            if (creditSelection != null) verif = verif && creditSelection.equals(store.credit);
            if (carMarkSelection != null) verif = verif && carMarkSelection.equals(store.major);
            if (is4sSelection != null) verif = verif && is4sSelection.equals(store.is4s);

            if (verif) MyApplication.storeslist.add(store);
        }

        Log.i("SelectCarStoreIemt", "size = " + MyApplication.storeslist.size());
        offset = Constants.NUM_PER_PAGE;
        pullData();
        list_store_container.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

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

    /**
     * A placeholder fragment containing a simple view.
     */
    /*
     * public static class PlaceholderFragment extends Fragment {
     * 
     * public PlaceholderFragment() { }
     * 
     * @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
     * savedInstanceState) { View rootView = inflater.inflate(R.layout.fragment_main, container,
     * false); return rootView; } }
     */

}
