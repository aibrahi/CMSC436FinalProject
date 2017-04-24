package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 4/22/17.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ahmedinibrahim on 4/17/17.
 */

public class UsersChatRoom extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int NEW_USER = 2;
    private ChatRoom chatRoom;

    private String mUsername;
    private String mUserId;
    private EditText mMessageEditText;
    private Button mSendButton;
    private ProgressBar mProgressBar;
    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ChildEventListener mChildEventListener;
    private String chatRoomID;
    private String chatRoomNAME;
    private List<String> membersList = new ArrayList<String>();


    // Firebase Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // Retrieve an instance of your database
        Intent intent = getIntent();
        chatRoomID = intent.getStringExtra("ChatRoomID");
        chatRoomNAME = intent.getStringExtra("ChatRoomNAME");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("ChatRooms").child(chatRoomID);

        this.setTitle(chatRoomNAME);

        // Initialize references to views
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);


        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Initialize message ListView and its adapter
        List<Messages> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.activity_chatroom, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize references to views and check the status of the user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    // user is signed in
                    onSignedInInitialize(user.getDisplayName(), user.getUid());

                }else{
                    // user is signed out
                    onSignedOutClean();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);


                }
            }
        };




        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});


        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Messages friendlyMessages = new Messages(mMessageEditText.getText().toString(), mUsername);
                mMessagesDatabaseReference.child("messages").push().setValue(friendlyMessages);


                // Clear input box
                mMessageEditText.setText("");
            }
        });

    }

    public class MessageAdapter extends ArrayAdapter<Messages> {
        public MessageAdapter(Context context, int resource, List<Messages> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Messages message = getItem(position);

            if (convertView == null) {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
            }

            TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
            TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message.getText());
            authorTextView.setText(message.getName());

            // create a new layout to the message TextView
            LinearLayout.LayoutParams messageXML = (LinearLayout.LayoutParams) messageTextView.getLayoutParams();
            LinearLayout.LayoutParams nameXML = (LinearLayout.LayoutParams) authorTextView.getLayoutParams();

            // if the message is mine, keep the message to the left
            if(message.getName().equals(mUsername)) {
                messageXML.gravity = Gravity.LEFT;
                nameXML.gravity = Gravity.LEFT;

            }

            // else keep the message to the right
            else {
                messageXML.gravity = Gravity.RIGHT;
                nameXML.gravity = Gravity.RIGHT;
            }

            // create a new layout to the message TextView
            messageTextView.setLayoutParams(messageXML);
            authorTextView.setLayoutParams(nameXML);

            return convertView;
        }
    }

    // Clean the adapters and signout the user
    private void onSignedOutClean() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        if(mChildEventListener != null){

            // Stops reading from Firebase
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    // Initialize user and get all the messages
    private void onSignedInInitialize(String username, String userID) {
        mUsername = username;
        mUserId = userID;

        if(mChildEventListener == null){

            // Reads from Firebase database and displays messages
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    mMessageAdapter.add(messages);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mMessagesDatabaseReference.child("messages").addChildEventListener(mChildEventListener);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_USER) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // Get new user ID
                String newuser = data.getExtras().getString("SelectedUser");

                // Add the user as a members to the chat room
                mMessagesDatabaseReference.child("members").child(newuser).setValue("member");

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:

                // sign out
                
                FirebaseAuth.getInstance().signOut();

//                AuthUI.getInstance().signOut(this);
                return true;

            case R.id.add_user:

                // create a new intent to list all the users in the database and return the selected user
                Intent pickContactIntent = new Intent(UsersChatRoom.this, ListOfUsers.class);
                pickContactIntent.putStringArrayListExtra("ListOfMembers", (ArrayList<String>) membersList);
                startActivityForResult(pickContactIntent, NEW_USER);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        if(mChildEventListener != null){

            // Stops reading from Firebase
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        mMessageAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_chatroom, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }


}

