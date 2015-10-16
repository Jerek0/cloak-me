package com.jeremy_minie.helloagaincrm.logged.entities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.util.image.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jerek0 on 16/10/15.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private OnItemClickListener listener;

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "UsersAdapter.ViewHolder";
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView userAvatar;
        public TextView userName;
        public FrameLayout userColor;

        private String uid;
        private OnItemClickListener mListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView, OnItemClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mListener = listener;
            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userColor = (FrameLayout) itemView.findViewById(R.id.user_color);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!= null)
                        mListener.onItemClick(itemView, uid);
                }
            });
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }

    // Store a member variable for the contacts
    private List<User> mUsers;

    // Pass in the contact array into the constructor
    public UsersAdapter(List<User> users) {
        mUsers = users;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_user, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, this.listener);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        User user = mUsers.get(position);

        viewHolder.setUid(user.getUid());

        ImageView userAvatar = viewHolder.userAvatar;
        Picasso.with(viewHolder.itemView.getContext()).load(user.getAvatar()).transform(new CircleTransform()).into(userAvatar);

        // Set item views based on the data model
        TextView textView = viewHolder.userName;
        textView.setText(user.getUsername());

        FrameLayout userColor = viewHolder.userColor;
        userColor.setBackgroundColor(user.getColor());
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, String uid);
    }
}
