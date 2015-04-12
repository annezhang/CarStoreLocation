package com.example.locationcarstore;

import java.text.DecimalFormat;
import com.example.locationcarstore.MyApplication;
import com.example.locationcarstore.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemListAdapter extends BaseAdapter {

    // private ArrayList<StoreClass> stores;
    private Context mContext;
    private LayoutInflater mInflater;
    private int lastPosition;

    // private HashMap<String,Double> storesMap;

    public ItemListAdapter(Context mContext) { // ,ArrayList<StoreClass> stores
        this.mContext = mContext;
        // this.stores = stores;
        this.mInflater = LayoutInflater.from(mContext);
        this.lastPosition = -1;
        // this.storesMap = new HashMap<String,Double>();

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        Log.i("ItemListAdapter", "storesSize = " + MyApplication.storeslistOnShow.size());
        // return stores.size();
        return MyApplication.storeslistOnShow.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        // return stores.get(position);
        return MyApplication.storeslistOnShow.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        lastPosition = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.i("ItemListAdapter", "getView start ");
        ViewHolder holder;

        if (convertView == null) {

            Log.i("ItemListAdapter", "convertview == null ");

            convertView = mInflater.inflate(R.layout.carstore_list_item, null);
            holder = new ViewHolder();

            holder.txtview_storeName = (TextView) convertView.findViewById(R.id.txtview_storeName);
            holder.txtview_qualification_value =
                    (TextView) convertView.findViewById(R.id.txtview_qualification_value);
            holder.txtview_4s_value = (TextView) convertView.findViewById(R.id.txtview_4s_value);
            holder.txtview_major_value =
                    (TextView) convertView.findViewById(R.id.txtview_major_value);
            holder.txtview_address = (TextView) convertView.findViewById(R.id.txtview_address);
            holder.txtview_credit = (TextView) convertView.findViewById(R.id.txtview_credit);
            holder.txtview_distance = (TextView) convertView.findViewById(R.id.txtview_distance);
            convertView.setTag(holder);// 绑定ViewHolder对象
        } else {

            Log.i("ItemListAdapter", "convertview != null ");
            holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
        }
        /* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
        holder.txtview_storeName.setText(MyApplication.storeslistOnShow.get(position).name);
        holder.txtview_qualification_value
                .setText(MyApplication.storeslistOnShow.get(position).qualifi);
        holder.txtview_4s_value.setText(MyApplication.storeslistOnShow.get(position).is4s);
        holder.txtview_major_value.setText(MyApplication.storeslistOnShow.get(position).major);
        holder.txtview_address.setText(MyApplication.storeslistOnShow.get(position).addr);
        holder.txtview_credit.setText(MyApplication.storeslistOnShow.get(position).credit);

        DecimalFormat df = new DecimalFormat("0.00");
        Double distance = MyApplication.storeslistOnShow.get(position).distance;
        holder.txtview_distance.setText(df.format(distance) + " km");

        Animation animation =
                AnimationUtils.loadAnimation(mContext, (position > lastPosition)
                        ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    public final class ViewHolder {
        public TextView txtview_storeName;
        public TextView txtview_qualification_value;
        public TextView txtview_4s_value;
        public TextView txtview_major_value;
        public TextView txtview_address;
        public TextView txtview_credit;
        public TextView txtview_distance;

    }

    /*
     * public void updateData(ArrayList<StoreClass> stores){ this.stores = stores; }
     */

}
