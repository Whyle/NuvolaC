package com.github.whyle.bluetooth_chat;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private ArrayList<Message> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final View sender;
        private final View receiver;
        // each data item is just a string in this case

        public MyViewHolder(View layout) {
            super(layout);
            sender = layout.findViewById(R.id.itemSender);
            receiver = layout.findViewById(R.id.itemReceiver);



        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatAdapter(ArrayList<Message> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view

        View layout = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);

        MyViewHolder vh = new MyViewHolder(layout);
        return vh;
    }

    String hour = "";
    String minute = "";

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that eleme
        TextView icon, textView, time;

       if (mDataset.get(position).getName()=="Y")
       {
           icon = holder.sender.findViewById(R.id.item_icon);
           textView = (TextView) holder.sender.findViewById(R.id.item_msg);
           time = holder.sender.findViewById(R.id.item_time);
           holder.sender.setVisibility(View.VISIBLE);

           ((GradientDrawable)icon.getBackground()).setColor(Color.GREEN);
       } else {
           holder.receiver.setVisibility(View.VISIBLE);
           icon = holder.receiver.findViewById(R.id.item_icon);
           textView = (TextView) holder.receiver.findViewById(R.id.item_msg);
           time = holder.receiver.findViewById(R.id.item_time);
           ((GradientDrawable)icon.getBackground()).setColor(Color.GRAY);
       }
        textView.setText(mDataset.get(position).getMsg());
        icon.setText(mDataset.get(position).getName());
        time.setText(mDataset.get(position).getTime());

        String[] times = mDataset.get(position).getTime().split(":");
        //00;00
        Log.i("aaa032.21.aaaaaaa","Curr msg time:" + mDataset.get(position).getTime());
        Log.i("aaa032.21.aaaaaaa","last msg time:" + hour + ":" + minute);

        if(times[0].equals( hour) && times[1].equals (minute))
        {
            time.setVisibility(View.VISIBLE);
            Log.i("aaa032.21.aaaaaaa","last msg time != curr msg time");
        }

        hour = times[0];
        minute = times[1];

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
