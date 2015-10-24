package com.jeremy_minie.helloagaincrm.logged;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.fragments.DiscussionsFragment;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiscussionActivity extends AppCompatActivity {

    private String discussionUid;

    @Bind(R.id.messageInput)
    EditText mMessageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        Intent intent = getIntent();
        discussionUid = intent.getStringExtra(DiscussionsFragment.DISCUSSION_UID);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra(DiscussionsFragment.USERNAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseManager.getInstance().markDiscussionAsRead(discussionUid);
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
    protected void onDestroy() {
        FirebaseManager.getInstance().markDiscussionAsRead(discussionUid);
        super.onDestroy();
    }

    @OnClick(R.id.sendMessageBtn)
    void OnClick() {
        FirebaseManager.getInstance().newMessage(discussionUid, mMessageInput.getText().toString());
    }

}
