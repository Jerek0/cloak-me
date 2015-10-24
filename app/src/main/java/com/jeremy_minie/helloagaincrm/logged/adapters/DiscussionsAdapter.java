package com.jeremy_minie.helloagaincrm.logged.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.entities.Discussion;
import com.jeremy_minie.helloagaincrm.util.image.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jerek0 on 16/10/15.
 */
public class DiscussionsAdapter extends RecyclerView.Adapter<DiscussionsAdapter.ViewHolder> {

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
        public FrameLayout discussionBackground;
        public TextView lastMessage;

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
            discussionBackground = (FrameLayout) itemView.findViewById(R.id.discussion_background);
            lastMessage = (TextView) itemView.findViewById(R.id.last_message);

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
    private List<Discussion> mDiscussions;

    // Pass in the contact array into the constructor
    public DiscussionsAdapter(List<Discussion> list) {
        mDiscussions = list;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public DiscussionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_discussion, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, this.listener);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(DiscussionsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Discussion discussion = mDiscussions.get(position);

        //viewHolder.setUid(discussion.getUid());

        ImageView userAvatar = viewHolder.userAvatar;
        Picasso.with(viewHolder.itemView.getContext()).load(discussion.getTarget().getAvatar()).transform(new CircleTransform()).into(userAvatar);

        TextView userName = viewHolder.userName;
        userName.setText(discussion.getTarget().getUsername());

        TextView lastMessage = viewHolder.lastMessage;
        lastMessage.setText(discussion.getLastMessage());

        FrameLayout discussionBackground = viewHolder.discussionBackground;
        discussionBackground.setBackgroundColor(discussion.getTarget().getColor());
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mDiscussions.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, String uid);
    }
}
