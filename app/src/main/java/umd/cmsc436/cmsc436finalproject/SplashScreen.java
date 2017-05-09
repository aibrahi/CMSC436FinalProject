package umd.cmsc436.cmsc436finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

/**
 * Created by ahmedinibrahim on 5/5/17.
 */

public class SplashScreen extends AppCompatActivity {

    // constant static variables
    private static final int RC_SIGN_IN = 1;


    private ProgressBar mProgressBar;

    // Firebase Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ValueEventListener mMembersListener;
    private FirebaseUser mUser;

    private Boolean found = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        // creating the progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.progressUsersBar);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        // Retrieve an instance of the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize references to views and check the status of the user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    mUser = user;
                    Log.d("USSERR -->", mUser.getDisplayName());

                    // user is signed in
                    // Add the user to the database if he/she exist
                    mFirebaseDatabase.getReference().child("Users").orderByKey().equalTo(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                // Key does not exist
                                User user = new User(mUser.getUid(), mUser.getDisplayName(), mUser.getEmail(), "https://firebasestorage.googleapis.com/v0/b/cmsc436finalproject.appspot.com/o/ac-lloyd.jpg?alt=media&token=3eade212-fe2d-4f36-8f9a-5bbf5a4b7e1d");

                                mFirebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(user);
                                Log.d("USER_EXIST", "NO");
                            } else {
                                // Key exists
                                Log.d("USER_EXIST", dataSnapshot.toString());

                            }
                        }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mMembersListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            Log.d("USSERR FOR-->", mUser.getDisplayName());


                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                ChatRoom chatRoom = data.getValue(ChatRoom.class);

                                // if the user is in a chat group already send him/her to the chat

                                if(chatRoom.getMembers().containsKey(mUser.getUid())){

                                    // the user has been found in a group
                                    found = true;

                                    // Create the intent to send the user to their chat room
                                    Intent createStoryIntent = new Intent(getApplicationContext(), FragmentViewer.class);
                                    createStoryIntent.putExtra("chatRoomName", chatRoom.getChatRoomName());

                                    createStoryIntent.putExtra("chatRoomID", data.getKey());
                                    startActivity(createStoryIntent);

                                }

                            }

                            if(!found) {
                                // Once the database changes make appropriate changes
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                // Initialize progress bar
                                mProgressBar.setVisibility(ProgressBar.GONE);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            // ...
                        }
                    };
                    mFirebaseDatabase.getReference().child("ChatRooms").addListenerForSingleValueEvent(mMembersListener);

                }else{
                    // user is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.roomie)
                                    .setTheme(R.style.FullscreenTheme)
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        found = false;
    }
}
