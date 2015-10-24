package com.jeremy_minie.helloagaincrm.logged.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.entities.Message;
import com.jeremy_minie.helloagaincrm.logged.entities.User;
import com.jeremy_minie.helloagaincrm.util.image.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jerek0 on 16/10/15.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private OnItemClickListener listener;

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "UsersAdapter.ViewHolder";

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public FrameLayout userColor;
        public FrameLayout targetColor;
        private TextView messageContent;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView, OnItemClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            userColor = (FrameLayout) itemView.findViewById(R.id.user_color);
            targetColor = (FrameLayout) itemView.findViewById(R.id.target_color);
            messageContent = (TextView) itemView.findViewById(R.id.messageContent);
        }
    }

    // Store a member variable for the contacts
    private List<Message> mMessages;

    // Pass in the contact array into the constructor
    public MessagesAdapter(List<Message> value) {
        mMessages = value;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View messageView = inflater.inflate(R.layout.item_message, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(messageView, this.listener);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Message message = mMessages.get(position);

        // Set item views based on the data model
        TextView textView = viewHolder.messageContent;
        textView.setText(message.getContent());

        FrameLayout userColor = viewHolder.userColor;
        FrameLayout targetColor = viewHolder.targetColor;
        userColor.setBackgroundColor(message.getColor());
        targetColor.setBackgroundColor(message.getColor());

        if(message.getAlignment().equals("right")) {
            targetColor.setAlpha(0f);
            userColor.setAlpha(1f);
            textView.setGravity(Gravity.RIGHT);
        } else {
            targetColor.setAlpha(1f);
            userColor.setAlpha(0f);
            textView.setGravity(Gravity.LEFT);
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, String uid);
    }
}
