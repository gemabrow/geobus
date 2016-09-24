package com.bussquad.sluglife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bussquad.sluglife.activity.AppController;
import com.bussquad.sluglife.activity.EventActivity;
import com.bussquad.sluglife.utilities.VolleySingleton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jose on 1/22/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private static String LOG_TAG = "MRecyclerViewAdapter";
    private List<DataObject> mDataset = Collections.emptyList();
    private String[] mDataSet;
    private List<Integer> mDataSetTypes;
    private static final int GENERAL = 0;
    private static final int EVENT = 1;
    private static final int OPERS = 2;
    private static final int BUSSCHEDULEITEM = 3;
    private static final int NOBUSCEDULE = 4;
    private LayoutInflater inflater;
    private AppController dataController;
    private ImageLoader imageLoader;
    OnItemClickListener itemClickListener;
    Context context;

    public RecyclerViewAdapter(Context context, List<DataObject> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataset = data;
        dataController = AppController.getInstance();
        imageLoader = dataController.getImageLoader();
    }




    public RecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }


    // handles data objects
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);

        }




        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }





    public class GeneralViewHolder extends ViewHolder {
        CardView cv;
        TextView name;
        TextView mainText;
        TextView additionalInfo;
        ImageView iconImage;
        private final Context context;
        public GeneralViewHolder(View v){
            super(v);
            this.context = v.getContext();
            this.cv = (CardView)v.findViewById(R.id.genCardView);
            this.name = (TextView) v.findViewById(R.id.item_name);
            this.mainText = (TextView) v.findViewById(R.id.next_bus);
            this.additionalInfo = (TextView) v.findViewById(R.id.item_additional_info);
            this.iconImage = (ImageView) v.findViewById(R.id.imgIcon);
        }

    }

    public class OpersViewHolder extends ViewHolder {

        TextView title;
        TextView subtext1;
        TextView subtext2;
        NetworkImageView displayImage;
        private final Context context;

        public OpersViewHolder(View v){
            super(v);
            this.context = v.getContext();
            this.displayImage = (NetworkImageView) v.findViewById(R.id.displayImage);
            this.title = (TextView) v.findViewById(R.id.title);
            this.subtext1 = (TextView) v.findViewById(R.id.subtext1);
            this.subtext2 = (TextView) v.findViewById(R.id.subtext2);



        }

    }

    public class BusStopViewHolder extends ViewHolder {

        TextView title;
        TextView subtext1;
        TextView subtext2;
        NetworkImageView displayImage;
        private final Context context;

        public BusStopViewHolder(View v){
            super(v);
            this.context = v.getContext();
            this.displayImage = (NetworkImageView) v.findViewById(R.id.displayImage);
            this.title = (TextView) v.findViewById(R.id.title);
            this.subtext1 = (TextView) v.findViewById(R.id.subtext1);
            this.subtext2 = (TextView) v.findViewById(R.id.subtext2);



        }

    }



    public class BusScheduleViewHolder extends ViewHolder implements  View.OnClickListener{

        TextView schedule_Time;
        ImageView notification_button;
        int stopID;
        String route;


        public BusScheduleViewHolder(View v, int type) {
            super(v);
            context = v.getContext();
            if(type == 1){

            this.schedule_Time = (TextView) v.findViewById(R.id.txt_schedule_time);
            this.notification_button = (ImageView) v.findViewById(R.id.btn_notification);
            this.notification_button.setOnClickListener(this);
            } else {
                this.schedule_Time = (TextView) v.findViewById(R.id.txt_nobuschedule);
            }


        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.btn_notification:
                //    startBusNotificationActivity();
                    System.out.println("Notification Button clicked");
                    break;

            }

        }

        private void startBusNotificationActivity(){
            Intent myIntent = new Intent(context,Bus.class);
            myIntent.putExtra("BUSSTOPID",this.stopID);
            myIntent.putExtra("ROUTE", this.route);
            context.startActivity(myIntent);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_left,R.anim.fade_out_in_place);
            System.out.println("Starting activity");
        }
    }


    // contains
    public class EventViewHolder extends ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView contentType;
        TextView name;
        TextView mainText;
        ImageView thumbNailImage;
        Button actionButton;
        String location;
        String eventID;
        private final Context context;


        public EventViewHolder(View v){
            super(v);
            context = v.getContext();
            this.cv = (CardView)v.findViewById(R.id.eventCardView);
            this.contentType = (TextView) v.findViewById(R.id.content_type);
            this.name = (TextView) v.findViewById(R.id.item_name);
            this.mainText = (TextView) v.findViewById(R.id.card_text);
            this.thumbNailImage = (ImageView) v.findViewById(R.id.thumbNail);
            this.actionButton = (Button) v.findViewById(R.id.action_button);
            this.actionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()){
                case R.id.action_button:
                    startEventActivity();
                    break;

            }

        }

        private void startEventActivity(){
            Intent myIntent = new Intent(context,EventActivity.class);
            myIntent.putExtra("EVENTID",this.eventID);
            context.startActivity(myIntent);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_left,R.anim.fade_out_in_place);
        }
    }





    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        View view = inflater.inflate(R.layout.general_card_view,parent,false);
//        DataObjectHolder holder = new DataObjectHolder(view);
//        return holder;
        View view;
        switch (viewType){
            case EVENT:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.campus_event_card, parent, false);
                return new EventViewHolder(view);
            case OPERS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.half_imganddesciption, parent, false);
                return new OpersViewHolder(view);
            case BUSSCHEDULEITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bus_schedule_item, parent, false);
                return new BusScheduleViewHolder(view,1);
            case NOBUSCEDULE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.no_busschedule, parent, false);
                return new BusScheduleViewHolder(view,2);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.general_card_view, parent, false);
                return new GeneralViewHolder(view);
        }
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case EVENT:
                final EventViewHolder holder = (EventViewHolder)viewHolder;
                holder.eventID = mDataset.get(position).getObjectid();
                holder.name.setText(mDataset.get(position).getHeaderText());
                holder.mainText.setText(mDataset.get(position).getMainText());
                holder.contentType.setText("Event");
                String urlThumbNail = mDataset.get(position).getImageUrl();

                if(urlThumbNail != null){
                    imageLoader.get(urlThumbNail, new ImageLoader.ImageListener() {



                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                            if(response.getBitmap() != null){
                                holder.thumbNailImage.setImageBitmap(getResizedBitmap(response.getBitmap(),240,240));
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            holder.thumbNailImage.setImageResource(R.drawable.place_holder_thumbnail);
                        }
                    },551,331);
                }else{
                    System.out.println("url thumbnail is null");
                }
                break;
            case OPERS:
                final OpersViewHolder oholder = (OpersViewHolder) viewHolder;
                oholder.title.setText(mDataset.get(position).getHeaderText());
                oholder.subtext1.setText(mDataset.get(position).getMainText());
                oholder.subtext2.setText(mDataset.get(position).getSubText());
                urlThumbNail = mDataset.get(position).getImageUrl();
                imageLoader = VolleySingleton.getInstance(context).getImageLoader();
                oholder.displayImage.setImageUrl(urlThumbNail,imageLoader);
                oholder.displayImage.setDefaultImageResId(R.drawable.half_view_img);
                oholder.displayImage.setErrorImageResId(R.drawable.half_view_img);
                break;
            case BUSSCHEDULEITEM:
                final BusScheduleViewHolder holder3 = (BusScheduleViewHolder)viewHolder;
                holder3.schedule_Time.setText(mDataset.get(position).getMainText());
                break;
            case NOBUSCEDULE:
                final BusScheduleViewHolder holder4 = (BusScheduleViewHolder)viewHolder;
                holder4.schedule_Time.setText(mDataset.get(position).getMainText());
                break;

            case GENERAL:
                final GeneralViewHolder dHolder = (GeneralViewHolder) viewHolder;
                dHolder.iconImage.setImageResource(mDataset.get(position).getIconId());
                dHolder.name.setText(mDataset.get(position).getHeaderText());
                dHolder.mainText.setText(mDataset.get(position).getMainText());
                dHolder.additionalInfo.setText(mDataset.get(position).getSubText());
                break;
            default:
                break;

        }



    }


    @Override
    public int getItemCount() {
        return  mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return mDataSetTypes.get(position);

    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




    public interface OnItemClickListener {
        public void onItemClick(int position, View v);
    }


    public void setOnItemClickListener(final OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    public void addObjectItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }



    // adds data and the type that goes with that data
    public void addDataSetType(int type){
        mDataSetTypes.add(type);
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



    public void swapTypeList(ArrayList<Integer> newTypes){
        mDataSetTypes.clear();
        mDataSetTypes.addAll(newTypes);

    }

    public  void setDataSetTypes(ArrayList<Integer> listOfTypes){
        mDataSetTypes = new ArrayList<>(listOfTypes);
    }



    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }



}

