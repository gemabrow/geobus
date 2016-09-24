package com.bussquad.sluglife.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.sluglife.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerFilterDialog extends DialogFragment {

    private int itemListId = 0;
    private ArrayList<Integer> mSelectedItems;

    public MarkerFilterDialog(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        System.out.println("Resource ID being set " + getArrayResourceID());
        builder.setTitle(R.string.filter_options_dialog)

                .setPositiveButton(R.string.show_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).setMultiChoiceItems(getArrayResourceID(), null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int item, boolean isChecked) {

                        if(isChecked){
                            // if the user check the item, add it to the selected items
                            mSelectedItems.add(item);
                        } else if (mSelectedItems.contains(item)){
                            // if item is unchecked remove it from the list of selected items
                            mSelectedItems.remove(item);
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    public void setArrayResourceID(int itemListId){

        this.itemListId = itemListId;

    }


    public int getArrayResourceID(){
        return itemListId;
    }
}
