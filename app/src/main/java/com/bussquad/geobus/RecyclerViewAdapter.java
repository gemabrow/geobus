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

/**
 * Created by Jose on 1/22/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String LOG_TAG = "MRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private ArrayList<BusStop> mBusStopset;
    private static MyClickListener myClickListener;
    private static final int TYPE_DATAOBJECT = 0;
    private static final int TYPE_BUSSTOP = 2;

    RecyclerViewAdapter(){
        mBusStopset = new ArrayList<BusStop>();
        mDataset = new ArrayList<DataObject>();
    }

    public RecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mBusStopset = new ArrayList<BusStop>();
        mDataset = myDataset;
    }


    // handles data objects
    public static class DataObjectHolder extends ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    // handles bus stop event objects
    public static class BusStopEventViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView busStopName;
        TextView busEta;
        TextView busType;
        ImageView busIcon;

        BusStopEventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.busStopEventcv);
            busStopName = (TextView) itemView.findViewById(R.id.busStop_name);
            busEta = (TextView) itemView.findViewById(R.id.next_bus);
            busType = (TextView) itemView.findViewById(R.id.next_bus);
            busIcon = (ImageView) itemView.findViewById(R.id.busStopImage);
        }

    }


    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }





    public void BusStopViewAdapter(ArrayList<BusStop> busStops) {
        mBusStopset = busStops;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.i("RecyclerViewAdapter","onCreateViewHolder - viewType set: " + viewType);
        System.out.println("RecyclerViewAdapter type set: " + viewType );
        View view;
        switch (viewType) {
            case TYPE_BUSSTOP:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bus_stop_card, parent, false);
                return new BusStopEventViewHolder(view);
            case TYPE_DATAOBJECT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardfragment, parent, false);
                return new DataObjectHolder(view);
        }

        return null;

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.i("RecyclerViewAdapter","onBindViewHolder");

        System.out.println("Binding View Holder" + viewHolder.getItemViewType());
        switch (viewHolder.getItemViewType()) {

            case TYPE_BUSSTOP:
                System.out.println("index: " + i);
                BusStopEventViewHolder bHolder = (BusStopEventViewHolder) viewHolder;
                bHolder.busStopName.setText(mBusStopset.get(i).getTitle());
                bHolder.busType.setText(mBusStopset.get(i).getBusses().get(0));
                break;
            case TYPE_DATAOBJECT:
                System.out.println("object Data size: " + mDataset.size());
                if(mDataset.size() >0){
                    DataObjectHolder dHolder = (DataObjectHolder) viewHolder;
                    dHolder.dateTime.setText(mDataset.get(i).getmText1());
                    dHolder.label.setText(mDataset.get(i).getmText2());
                    System.out.println("index: " + i + mDataset.get(i).getmText1());
                }


        }


    }


    @Override
    public int getItemCount() {
        Log.i("RecyclerViewAdapter ","getItemCount() - size of bustops: " + mBusStopset.size()
        + " size of mDataset " + mDataset.size());
        return (mBusStopset.size() + mDataset.size());
    }


    @Override
    public int getItemViewType(int position) {
        Log.i("RecyclerViewAdapter","getItemViewType() - get Type: " +  position);
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        switch (position){
            case 1:
                System.out.println("getting DataObject " + position);
                return TYPE_DATAOBJECT;
            case 2:
                System.out.println("getting busStopObject " + position);
                return TYPE_BUSSTOP;
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




    public void addBusStopItem(BusStop busStopObj, int index){
        mBusStopset.add(index,busStopObj);
    }


    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }





    // populates the routes available for the bus
    public void addBusStop(){

    }

}

