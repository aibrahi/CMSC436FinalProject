package umd.cmsc436.cmsc436finalproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private FirebaseUser mUser;
    private EditText mNewChatNameText;
    private Button mCreateNewChatRoomButton;
    private ListView mChatGroupListView;


    // Firebase Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatRoomDatabaseReference;
    private DatabaseReference mUsernameDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progress;
    private Boolean found = false;
    private Intent createStoryIntent;
    private ValueEventListener mMembersListener;


    private String mUsername;
    private ChatRoomAdapter mChatRoomAdapter;
    private ChildEventListener mChildEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Retrieve an instance of the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mChatRoomDatabaseReference = mFirebaseDatabase.getReference().child("ChatRooms");

        // creating the progress bar
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        // Initialize references to views
        mChatGroupListView = (ListView) findViewById(R.id.listOfChats);
        mNewChatNameText = (EditText) findViewById(R.id.newchatName);
        mCreateNewChatRoomButton = (Button) findViewById(R.id.create_newchat_button);


        // Initialize chat room ListView and its adapter
        List<ChatRoom> friendlyMessages = new ArrayList<>();
        mChatRoomAdapter = new ChatRoomAdapter(this, R.layout.item_chatrooms, friendlyMessages);
        mChatGroupListView.setAdapter(mChatRoomAdapter);

        // Initialize references to views and check the status of the user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d("USSERR -->", user.getDisplayName());
                    // user is signed in
                    onSignedInInitialize(user.getDisplayName(), user);

                }else{
                    // user is signed out
                    onSignedOutClean();
                    mChatRoomDatabaseReference.removeEventListener(mMembersListener);
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.umd)
                                    .setTheme(R.style.FullscreenTheme)
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mMembersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("USSERR FOR-->", mUser.getDisplayName());


                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = data.getValue(ChatRoom.class);

                    mChatRoomAdapter.add(chatRoom);

                    // if the user is in a chat group already send him/her to the chat

                    if(chatRoom.getMembers().containsKey(mUser.getUid())){

                        // the user has been found in a group
                        found = true;

                        // Create the intent to send the user to their chat room
                        createStoryIntent = new Intent(MainActivity.this, UsersChatRoom.class);
                        createStoryIntent.putExtra("ChatRoomNAME", chatRoom.getChatRoomName());
                        createStoryIntent.putExtra("ChatRoomID", data.getKey());
                    }

                }
                Log.d("USSERR FOR-->", found.toString());

                if(found) {
                    //   dismiss the dialog
                    progress.dismiss();
                    mChatRoomDatabaseReference.removeEventListener(mMembersListener);

                    // Send the user to their chat room
                    startActivity(createStoryIntent);
                }else {

                    // Once the database changes make appropriate changes
                    updateUI();
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        mChatRoomDatabaseReference.addListenerForSingleValueEvent(mMembersListener);



    }

    private void updateUI() {

            //  dismiss the dialog
            progress.dismiss();
            mChatRoomDatabaseReference.removeEventListener(mMembersListener);


            // Enable Send button when there's text to send
            mNewChatNameText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        mCreateNewChatRoomButton.setEnabled(true);
                    } else {
                        mCreateNewChatRoomButton.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            mNewChatNameText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});


            // Create chat room button creates a new chat room in the database
            mCreateNewChatRoomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.addOwner(mUser);
                    chatRoom.setChatRoomName(mNewChatNameText.getText().toString());

                    String mGroupId = mChatRoomDatabaseReference.push().getKey();
                    mChatRoomDatabaseReference.child(mGroupId).setValue(chatRoom);

                    Intent createStoryIntent = new Intent(MainActivity.this, UsersChatRoom.class);
                    createStoryIntent.putExtra("ChatRoomID", mGroupId);
                    startActivity(createStoryIntent);


                    // Clear input box
                    mNewChatNameText.setText("");
                }
            });

    }


    private void onSignedOutClean() {
        mUsername = ANONYMOUS;
        if(mChildEventListener != null){

            // Stops reading from Firebase
            mChatRoomDatabaseReference.removeEventListener(mChildEventListener);

            mChildEventListener = null;

        }

        mChatRoomDatabaseReference.removeEventListener(mMembersListener);
        mUser = null;
        found = false;
    }

    // Add the user to the user schema database and initialize user
    private void onSignedInInitialize(String username, FirebaseUser user) {
        mUsername = username;
        mUser = user;

        // Add the username to the database
        mUsernameDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mUsernameDatabaseReference.child(user.getUid()).setValue(mUsername);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                // sign out
                FirebaseAuth.getInstance().signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){

                mChatRoomDatabaseReference.addListenerForSingleValueEvent(mMembersListener);

            }else if(resultCode == RESULT_CANCELED){
//                Toast.makeText(MainActivity.this, "Signed in cancelled", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        if(mChildEventListener != null){

            // Stops reading from Firebase
            mChatRoomDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }

        mChatRoomDatabaseReference.removeEventListener(mMembersListener);
        found = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    private class ChatRoomAdapter extends ArrayAdapter<ChatRoom> {

        private ChatRoom chatRoom;

        public ChatRoomAdapter(Context context, int resource, List<ChatRoom> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_chatrooms, parent, false);
            }

            TextView messageTextView = (TextView) convertView.findViewById(R.id.chatRoomTextView);

            chatRoom = getItem(position);

            messageTextView.setText(chatRoom.getChatRoomName());

            return convertView;
        }
    }
}
