package com.bussquad.sluglife.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bussquad.sluglife.R;
import com.androidmapsextensions.GoogleMap.InfoWindowAdapter;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jose on 4/26/2016.
 */
public class MapInfoWindowAdapter implements InfoWindowAdapter {


    private View contentView;
    private LayoutInflater inflater;
    private int imgResourceID = R.drawable.ic_map_black_24dp;
    private String mainInfo;
    private String additionalInfo;
    private int color;

    public MapInfoWindowAdapter(Context context){
        inflater = LayoutInflater.from(context);

        contentView = inflater.inflate(R.layout.general_info_window, null);
    }


    public void setMainInfo(String mainInfo){
        this.mainInfo = mainInfo;
    }


    @Override
    public View getInfoContents(com.androidmapsextensions.Marker marker) {
        ImageView iconImage = (ImageView)contentView.findViewById(R.id.imgIcon);
        iconImage.setImageResource(imgResourceID);

        TextView name = (TextView)contentView.findViewById(R.id.item_name);
        name.setText(marker.getTitle());

        TextView mainInfo = (TextView)contentView.findViewById(R.id.next_bus);
        mainInfo.setText(marker.getSnippet());
        TextView details = (TextView)contentView.findViewById(R.id.item_additional_info);
        details.setText(this.mainInfo);
        return contentView;

    }

    @Override
    public View getInfoWindow(com.androidmapsextensions.Marker marker) {
        ImageView iconImage = (ImageView)contentView.findViewById(R.id.imgIcon);
        iconImage.setImageResource(imgResourceID);

        TextView name = (TextView)contentView.findViewById(R.id.item_name);
        name.setText(marker.getTitle());

        TextView mainInfo = (TextView)contentView.findViewById(R.id.next_bus);
        mainInfo.setText(marker.getSnippet());
        TextView details = (TextView)contentView.findViewById(R.id.item_additional_info);
        details.setText(this.mainInfo);
        return contentView;
    }

    public void setImageResource(int resourceId){
        this.imgResourceID = resourceId;
    }









}
