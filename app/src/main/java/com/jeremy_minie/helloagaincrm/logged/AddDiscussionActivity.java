package com.jeremy_minie.helloagaincrm.logged;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.entities.User;
import com.jeremy_minie.helloagaincrm.logged.entities.UsersAdapter;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class AddDiscussionActivity extends AppCompatActivity implements FirebaseManager.FirebaseDataListener {

    private static final String TAG = "AddDiscussionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discussion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New discussion");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);

        FirebaseManager.getInstance().getUsersList(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataChanged(DataSnapshot snapshot) {
        System.out.println(snapshot);

        List<User> usersList = new ArrayList<User>();

        System.out.println("There are " + snapshot.getChildrenCount() + " users");
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            User user = postSnapshot.getValue(User.class);
            user.setUid(postSnapshot.getKey());
            usersList.add(user);
        }

        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvUsers);
        // Create adapter passing in the sample user data
        UsersAdapter adapter = new UsersAdapter(usersList);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Snackbar.make(findViewById(R.id.rvUsers), firebaseError.getMessage(), Snackbar.LENGTH_LONG).setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Dismiss");
                    }
                })
                .show();;
    }
}
