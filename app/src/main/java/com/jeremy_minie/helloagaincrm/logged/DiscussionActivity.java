package com.jeremy_minie.helloagaincrm.logged;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jeremy_minie.helloagaincrm.R;
import com.jeremy_minie.helloagaincrm.logged.adapters.MessagesAdapter;
import com.jeremy_minie.helloagaincrm.logged.adapters.UsersAdapter;
import com.jeremy_minie.helloagaincrm.logged.entities.Message;
import com.jeremy_minie.helloagaincrm.logged.fragments.DiscussionsFragment;
import com.jeremy_minie.helloagaincrm.util.FirebaseManager;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiscussionActivity extends AppCompatActivity implements FirebaseManager.FirebaseDataListener {

    private String discussionUid;
    private int targetColor;
    private ValueEventListener vel;

    @Bind(R.id.messageInput)
    EditText mMessageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        Intent intent = getIntent();
        discussionUid = intent.getStringExtra(DiscussionsFragment.DISCUSSION_UID);
        targetColor = intent.getIntExtra(DiscussionsFragment.USERCOLOR, 0);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra(DiscussionsFragment.USERNAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseManager.getInstance().markDiscussionAsRead(discussionUid);
        vel = FirebaseManager.getInstance().getMessagesByChannelUid(discussionUid, this);
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
        FirebaseManager.getInstance().removeMessagesListener(discussionUid, vel);
        super.onDestroy();
    }

    @OnClick(R.id.sendMessageBtn)
    void OnClick() {
        FirebaseManager.getInstance().newMessage(discussionUid, mMessageInput.getText().toString());
        mMessageInput.setText("");
    }

    @Override
    public void onDataChanged(DataSnapshot snapshot) {
        List<Message> messageList = new ArrayList<Message>();

        System.out.println("There are " + snapshot.getChildrenCount() + " messages in that channel");
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {

            String align;
            int colorToUse;
            if(((String) postSnapshot.child("author").getValue()).equals(FirebaseManager.getInstance().getUser().getUid())) {
                colorToUse = FirebaseManager.getInstance().getUser().getColor();
                align = "right";
            }
            else {
                colorToUse = targetColor;
                align = "left";
            }

            // Decrypt message
            AesCbcWithIntegrity.SecretKeys keys = FirebaseManager.getInstance().getUserSecrets().getDiscussionsKeys().get(discussionUid);
            // Recreate CipherTextIvMac
            AesCbcWithIntegrity.CipherTextIvMac dataToDecrypt = new AesCbcWithIntegrity.CipherTextIvMac((String) postSnapshot.child("encrypted_content").getValue());

            // Decrypt!
            String decrypted = null;
            try {
                decrypted = AesCbcWithIntegrity.decryptString(dataToDecrypt, keys);
            } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                e.printStackTrace();
            }
            if(decrypted==null) {
                throw new AssertionError();
            }

            Message message = new Message((String) postSnapshot.child("author").getValue(), decrypted, colorToUse);
            message.setAlignment(align);
            messageList.add(message);
        }

        updateAdapter(messageList);
    }

    private void updateAdapter(List<Message> list) {
        // Lookup the recyclerview in activity layout
        RecyclerView rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        // Create adapter passing in the sample user data
        MessagesAdapter adapter = new MessagesAdapter(list);
        //adapter.setOnItemClickListener(this);
        // Attach the adapter to the recyclerview to populate items
        rvMessages.setAdapter(adapter);
        // Set layout manager to position the items

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        rvMessages.setLayoutManager(llm);
        // That's all!
        rvMessages.setHasFixedSize(false);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
