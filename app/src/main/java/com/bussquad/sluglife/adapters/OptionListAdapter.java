/*
    this class is used to populate menus in the application. It can populate popupwindow menus
    or options menus. It uses the baseListAdapter class
 */


package com.bussquad.sluglife.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bussquad.sluglife.MapMenuItem;
import com.bussquad.sluglife.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 9/10/2016.
 */
public class OptionListAdapter extends ArrayAdapter<MapMenuItem> {

    Context mContext;
    List<MapMenuItem> menuItems = new ArrayList<>();
    int selected  = -1;
    int resource = -1;

    public OptionListAdapter(Context mContext, int resource, List<MapMenuItem> items) {
        super(mContext, resource, items);
        this.mContext = mContext;
        this.menuItems = items;
        this.resource = resource;
    }


    // get the number of items in the navigation drawer
    @Override
    public int getCount() {
        return menuItems.size();
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final MapMenuItem item = menuItems.get(position);
        if(item != null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = inflater.inflate(resource,null);

        }
        switch (resource){
            case R.layout.list_menu:
                TextView txtTitle = (TextView)v.findViewById(R.id.txt_menu_item);
                txtTitle.setText(item.getTitle());
                if(position == selected){

                    if(Build.VERSION.SDK_INT >= 23){
                        txtTitle.setBackgroundColor(ContextCompat.getColor(mContext,R.color.mdx_blue_powder));
                    } else {
                        txtTitle.setBackgroundColor(mContext.getResources().getColor(R.color.mdx_blue_powder));
                    }

                }
                break;
        }

        return v;
    }


    public void setSelectedItem(int selected){
        this.selected =  selected;
    }
}