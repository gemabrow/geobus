package com.bussquad.sluglife.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bussquad.sluglife.R;

import java.util.ArrayList;

/**
 * Created by Jose on 6/14/2016.
 */
public class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;



    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }



    // get the number of items in the navigation drawer
    @Override
    public int getCount() {
        return mNavItems.size();
    }




    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }





    @Override
    public long getItemId(int position) {
        return 0;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = 0;
        final NavItem item = mNavItems.get(position);
        if (item != null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(mNavItems.get(position).isSection()){

                convertView = inflater.inflate(R.layout.nav_subheader, null);
                convertView.setOnClickListener(null);
                convertView.setOnLongClickListener(null);
                convertView.setLongClickable(false);
                viewType = R.layout.nav_subheader;
            } else if(mNavItems.get(position).isDivider()){

                convertView = inflater.inflate(R.layout.nav_divider, null);
                convertView.setOnClickListener(null);
                convertView.setOnLongClickListener(null);
                convertView.setLongClickable(false);
                convertView.setClickable(false);
                viewType = R.layout.nav_divider;

            } else if(mNavItems.get(position).isProfile()){

                convertView = inflater.inflate(R.layout.nav_profile, null);
                convertView.setLongClickable(false);
                viewType = R.layout.nav_profile;

            }else {
                convertView = inflater.inflate(R.layout.nav_list_item, null);
                viewType = R.layout.nav_list_item;
            }

        }



        switch (viewType){

            case R.layout.nav_profile:
                TextView profileName = (TextView)convertView.findViewById(R.id.profile_name);
                TextView profileEmail = (TextView)convertView.findViewById(R.id.profile_email);
                ImageView profileImage = (ImageView)convertView.findViewById(R.id.lrg_profile_pic);
                profileEmail.setText(mNavItems.get(position).getProfileEmail());
                profileName.setText(mNavItems.get(position).getProfileName());
                profileImage.setImageResource(mNavItems.get(position).getIcon());
                break;
            case R.layout.nav_list_item:
                TextView titleView = (TextView) convertView.findViewById(R.id.item_title);
                titleView.setText( mNavItems.get(position).mTitle );
                ImageView iconView = (ImageView) convertView.findViewById(R.id.item_icon);
                iconView.setImageResource(mNavItems.get(position).mIcon);
                break;
            case R.layout.nav_divider:
                break;

            case R.layout.nav_subheader:
                TextView titleView2 = (TextView) convertView.findViewById(R.id.item_title);
                titleView2.setText( mNavItems.get(position).mTitle );
                break;
            default:
                break;
        }

        return convertView;
    }
}