package umd.cmsc436.cmsc436finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ahmedinibrahim on 4/28/17.
 */

public class UsersChatRoomFragment extends Fragment {

    private static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int NEW_USER = 2;
    private static final String ARG_CHATROOM_ID = "chatRoomId";
    private static final String ARG_CHATROOM_NAME = "chatRoomName";

    private ChatRoom chatRoom;

    private String mUsername;
    private String mUserId;
    private EditText mMessageEditText;
    private Button mSendButton;
    private ProgressBar mProgressBar;
    private ListView mMessageListView;
    private FirebaseListAdapter mMessageAdapter;
    private ChildEventListener mChildEventListener;
    private String chatRoomID;
    private String chatRoomNAME;
    private List<String> membersList = new ArrayList<String>();


    // Firebase Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public static Fragment newInstance(String chatRoomNAME, String chatRoomID) {
        Bundle args = new Bundle();
        args.putString(ARG_CHATROOM_ID, chatRoomID);
        args.putString(ARG_CHATROOM_NAME, chatRoomNAME);

        UsersChatRoomFragment fragment = new UsersChatRoomFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatRoomID = getArguments().getString(ARG_CHATROOM_ID);
        chatRoomNAME = getArguments().getString(ARG_CHATROOM_NAME);

        // Hide keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userschatroom_fragment, null);

        // Retrieve an instance of your database

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("ChatRooms").child(chatRoomID);

        // Initialize references to views
        mMessageEditText = (EditText) view.findViewById(R.id.messageEditText);
        mSendButton = (Button) view.findViewById(R.id.sendButton);
        mMessageListView = (ListView) view.findViewById(R.id.messageListView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);


        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Initialize message ListView and its adapter

         mMessageAdapter = new FirebaseListAdapter<Messages>(getActivity(), Messages.class, R.layout.item_message, mMessagesDatabaseReference.child("messages"))  {

            @Override
            protected void populateView(View v, Messages message, int position) {
                TextView messageTextView = (TextView) v.findViewById(R.id.messageTextView);
                TextView authorTextView = (TextView) v.findViewById(R.id.nameTextView);

                messageTextView.setText(message.getText());
                authorTextView.setText(message.getName());

                // create a new layout to the message TextView
                LinearLayout.LayoutParams messageXML = (LinearLayout.LayoutParams) messageTextView.getLayoutParams();
                LinearLayout.LayoutParams nameXML = (LinearLayout.LayoutParams) authorTextView.getLayoutParams();

                // if the message is mine, keep the message to the left
                if(message.getUid().equals(mUserId)) {
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
            }
        };
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize references to views and check the status of the user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d("USSERR -->", user.getDisplayName());
                    // user is signed in
                    onSignedInInitialize(user.getDisplayName(), user.getUid());

                }else{
                    // user is signed out
                    onSignedOutClean();
                    Intent intent = new Intent(getActivity(), SplashScreen.class);
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

                Messages friendlyMessages = new Messages(mMessageEditText.getText().toString(), mUsername, mUserId);
                mMessagesDatabaseReference.child("messages").push().setValue(friendlyMessages);


                // Clear input box
                mMessageEditText.setText("");
            }
        });


        return view;
    }

    // Clean the adapters and signout the user
    private void onSignedOutClean() {
        mUsername = ANONYMOUS;
        mMessageAdapter.cleanup();
    }

    // Initialize user and get all the messages
    private void onSignedInInitialize(String username, String userID) {
        mUsername = username;
        mUserId = userID;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        mMessageAdapter.cleanup();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_chatroom, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

}
