package com.bussquad.geobus;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jose on 1/22/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataObjectHolder>
{

    private static String LOG_TAG = "MRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
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


    public static class BusStopEventViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView busStopName;
        TextView etaStopName;
        TextView busType;
        ImageView busIcon;

        BusStopEventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.busStopEventcv);
            busStopName = (TextView)itemView.findViewById(R.id.busStop_name);
            etaStopName = (TextView)itemView.findViewById(R.id.next_bus);
            busType = (TextView)itemView.findViewById(R.id.next_bus);
            busIcon = (ImageView)itemView.findViewById(R.id.busStopImage);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }




    public RecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }




    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardfragment, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }




    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
    }




    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }




    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }




    @Override
    public int getItemCount() {
        return mDataset.size();
    }




    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}