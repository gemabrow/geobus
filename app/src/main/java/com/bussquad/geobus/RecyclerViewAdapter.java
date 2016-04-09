package com.bussquad.geobus;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jose on 1/22/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String LOG_TAG = "MRecyclerViewAdapter";
    private List<DataObject> mDataset = Collections.emptyList();
    private static MyClickListener myClickListener;
    private static final int TYPE_DATAOBJECT = 0;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<DataObject> data){
        inflater = LayoutInflater.from(context);
        this.mDataset = data;
    }




    public RecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }


    // handles data objects
    public static class DataObjectHolder extends ViewHolder
            implements View
            .OnClickListener {
        CardView cv;
        TextView name;
        TextView mainText;
        TextView additionalInfo;
        ImageView iconImage;


        public DataObjectHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.genCardView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            mainText = (TextView) itemView.findViewById(R.id.item_main_text);
            additionalInfo = (TextView) itemView.findViewById(R.id.item_additional_info);
            iconImage = (ImageView) itemView.findViewById(R.id.item_icon_image);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }




        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }






    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.i("RecyclerViewAdapter","onCreateViewHolder - viewType set: " + viewType);
        View view = inflater.inflate(R.layout.general_card_view,parent,false);

        DataObjectHolder holder = new DataObjectHolder(view);

        return holder;

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.i("RecyclerViewAdapter", "onBindViewHolder");

        DataObjectHolder dHolder = (DataObjectHolder) viewHolder;
        dHolder.iconImage.setImageResource(mDataset.get(i).getIconId());
        dHolder.name.setText(mDataset.get(i).getHeaderText());
        dHolder.mainText.setText(mDataset.get(i).getMainText());
        dHolder.additionalInfo.setText(mDataset.get(i).getSubText());



    }


    @Override
    public int getItemCount() {
        return  mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        Log.i("RecyclerViewAdapter", "getItemViewType() - get Type: " + position);
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        switch (position){
            case 1:
                System.out.println("getting DataObject " + position);
                return TYPE_DATAOBJECT;
        }
        return 2;

    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }


    public void addObjectItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }




    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }



    public void swap(ArrayList<DataObject> newData){
        mDataset.clear();
        mDataset.addAll(newData);
        notifyDataSetChanged();
    }







}

