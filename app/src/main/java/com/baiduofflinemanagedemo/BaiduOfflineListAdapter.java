package com.baiduofflinemanagedemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.baidu.mapapi.map.offline.MKOfflineMap;

import java.util.ArrayList;

/**
 * Created by ZL on 2018/4/25.
 */

public class BaiduOfflineListAdapter extends BaseAdapter {

    private Context mContext;
    private MKOfflineMap mkOfflineMap;
    private ArrayList<BaiduOfflineMapModule> list;

    public BaiduOfflineListAdapter(Context context, MKOfflineMap mkOfflineMap, ArrayList<BaiduOfflineMapModule> list) {
        this.mContext = context;
        this.mkOfflineMap = mkOfflineMap;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();

            BaiduOfflineChild offLineChild = new BaiduOfflineChild(mContext, mkOfflineMap);
            convertView = offLineChild.getOffLineChildView();
            viewHolder.mOfflineChild = offLineChild;
            convertView.setTag(viewHolder);
        }
        BaiduOfflineMapModule offlineMapCity = list.get(position);
        viewHolder.mOfflineChild.setOffLineCity(offlineMapCity);

        return convertView;
    }

    public final class ViewHolder {
        public BaiduOfflineChild mOfflineChild;
    }
}
