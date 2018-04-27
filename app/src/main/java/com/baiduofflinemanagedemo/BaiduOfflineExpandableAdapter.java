package com.baiduofflinemanagedemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOfflineMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZL on 2018/4/20.
 */

public class BaiduOfflineExpandableAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private MKOfflineMap mOffline;
    private List<String> itemsProvince;
    private ArrayList<ArrayList<BaiduOfflineMapModule>> itemsProvinceCity;

    public BaiduOfflineExpandableAdapter(Context context, MKOfflineMap mkOfflineMap, List<String> itemsProvince, ArrayList<ArrayList<BaiduOfflineMapModule>> itemsProvinceCity) {
        this.mContext = context;
        this.mOffline = mkOfflineMap;
        this.itemsProvince = itemsProvince;
        this.itemsProvinceCity = itemsProvinceCity;
    }

    @Override
    public int getGroupCount() {
        return itemsProvince.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemsProvinceCity.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return itemsProvince.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView group_text;
        ImageView group_image;
        if (convertView == null) {
            convertView = RelativeLayout.inflate(
                    mContext, R.layout.offlinemap_group, null);
        }
        group_text = convertView.findViewById(R.id.group_text);
        group_image = convertView
                .findViewById(R.id.group_image);
        group_text.setText(itemsProvince.get(groupPosition));
        if (isExpanded) {
            group_image.setImageDrawable(mContext.getResources().getDrawable(
                    R.drawable.toparrow));
        } else {
            group_image.setImageDrawable(mContext.getResources().getDrawable(
                    R.drawable.downarrow));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            BaiduOfflineChild offLineChild = new BaiduOfflineChild(mContext, mOffline);
            convertView = offLineChild.getOffLineChildView();
            viewHolder.mOfflineChild = offLineChild;
            convertView.setTag(viewHolder);
        }
        viewHolder.mOfflineChild.setOffLineCity(itemsProvinceCity.get(groupPosition).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public final class ViewHolder {
        public BaiduOfflineChild mOfflineChild;
    }
}
