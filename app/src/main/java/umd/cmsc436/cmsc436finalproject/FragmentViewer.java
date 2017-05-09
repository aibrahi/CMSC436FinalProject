package umd.cmsc436.cmsc436finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Clayton on 4/25/2017.
 */

public class FragmentViewer extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    // constant static variables
    private static final int RC_SIGN_IN = 1;
    public static final int NEW_USER = 2;
    public static final int REMOVE_USER = 3;

    // Other variables
    private String prev_class;
    private HashMap<String, HashMap> taskScenarioData;
    private HashMap<String, HashMap> meta;
    private AHBottomNavigation bottomNavigation;
    private User mUser;
    private String chatRoomID;
    private String chatRoomName;

    // Layout variables
    private TextView userEmailNav;
    private TextView userNameNav;
    private ImageView userImageNav;
    private List<String> membersList = new ArrayList<String>();


    // Firebase Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUserFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_viewer);

        Intent intent = getIntent();
        if (intent.getStringExtra("chatRoomID") == null){
            Log.d("***DEBUG****", "Intent was null");
    }else {
            Log.d("**** DEBUG ***", "Intent OK");
        }
//        String MANEUVER_ID  = intent.getStringExtra("selection"); //Exception points to this line
//        Log.d("*** DEBUG", rec + " " + MANEUVER_ID);
        chatRoomID = intent.getStringExtra("chatRoomID");
        chatRoomName = intent.getStringExtra("chatRoomName");
//        Log.d("USSERR -->", intent.getStringExtra("ChatRoomID"));



        /* NAVIGATION & TOOLBAR */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.setTitle(chatRoomName);



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        // layouts for top nav
        userEmailNav = (TextView) headerLayout.findViewById(R.id.userEmail);
        userNameNav = (TextView) headerLayout.findViewById(R.id.usersName);
        userImageNav = (ImageView) headerLayout.findViewById(R.id.userImage);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);


        // Retrieve an instance of the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize references to views and check the status of the user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    // user is signed in
                    Log.d("USSERRR--->", user.getDisplayName());

                    onSignedInInitialize(user);

                }else{
                    // user is signed out and send to the splash screen
                    onSignedOutClean();
                    Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                    startActivity(intent);
                }
            }
        };

        mFirebaseDatabase.getReference().child("ChatRooms").child(chatRoomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                Log.d("MEMBERS LIST--->", chatRoom.getMembers().keySet().toString());
                membersList.addAll(chatRoom.getMembers().keySet());
//                membersList.remove(mUserId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        //extract hashtable from bundle passed in
//        taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");


        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnTabSelectedListener(this);

//        prev_class = getIntent().getStringExtra("intent");
        this.createNavItems();




    }

    private void createNavItems() {

        //CREATE ITEMS

        AHBottomNavigationItem chatItem = new AHBottomNavigationItem(R.string.chat_label, R.drawable.ic_supervisor_account_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem billItem = new AHBottomNavigationItem(R.string.bills_label, R.drawable.ic_attach_money_black_24dp, R.color.colorAccent);
        //AHBottomNavigationItem homeItem = new AHBottomNavigationItem(R.string.home, R.drawable.home_icon, R.color.colorAccent);
        //AHBottomNavigationItem generalItem=new AHBottomNavigationItem(R.string.general, R.drawable.graph_icon, R.color.colorAccent);
        //AHBottomNavigationItem fitnessItem=new AHBottomNavigationItem(R.string.fitness,R.drawable.run_icon, R.color.colorAccent);
        //AHBottomNavigationItem dietItem=new AHBottomNavigationItem(R.string.diet,R.drawable.diet_icon, R.color.colorAccent);

        //ADD THEM to bar

        bottomNavigation.addItem(chatItem);
        bottomNavigation.addItem(billItem);
        //bottomNavigation.addItem(homeItem);
        //bottomNavigation.addItem(generalItem);
        //bottomNavigation.addItem(fitnessItem);
        //bottomNavigation.addItem(dietItem);

        //set properties
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setCurrentItem(0);

        //set current item
//        if (prev_class != null) {
//            switch (prev_class) {
//                case "diet":
//                    bottomNavigation.setCurrentItem(3);
//                    break;
//                case "general":
//                    bottomNavigation.setCurrentItem(1);
//                    break;
//                case "fitness":
//                    bottomNavigation.setCurrentItem(2);
//                    break;
//            }
//            prev_class = "";
//        }
    }


    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {

        if(bottomNavigation.getCurrentItem() == position)
            return true;
        //show fragment
        switch(position) {
            /* navigate to chatroom */

            case 0:
                System.out.println("case 0");

                UsersChatRoomFragment mainFragment = new UsersChatRoomFragment();
                Bundle mainFragData = new Bundle();

                mainFragData.putString("chatRoomId", chatRoomID);
                mainFragData.putString("chatRoomName", chatRoomName);

                mainFragment.setArguments(mainFragData);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_id, mainFragment)
                        .commit();
                break;

//                Intent intent = new Intent(FragmentViewer.this, MainActivity.class);
//                startActivity(intent);


            /* navigate to bills */
            case 1:
                System.out.println("case 1");

                BillSplitFragment billSplitFragment = new BillSplitFragment();
                Bundle billData = new Bundle();
                billSplitFragment.setArguments(billData);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_id, billSplitFragment)
                        .commit();
                break;

            /* navigate to fitness */
            case 2:
                System.out.println("case 2");

                //FitnessFragment fitnessFragment = new FitnessFragment();
                //Bundle fitnessData = new Bundle();
                //taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");
                //fitnessData.putSerializable("taskScenarioData", taskScenarioData);
                //fitnessFragment.setArguments(fitnessData);

                //getSupportFragmentManager()
                //        .beginTransaction()
                //        .replace(R.id.content_id, fitnessFragment)
                //        .commit();
                break;

            /* navigate to diet */
            case 3:
                System.out.println("case 3");

                //DietFragment dietFragment = new DietFragment();
                //Bundle dietData = new Bundle();
                //taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");
                //dietData.putSerializable("taskScenarioData", taskScenarioData);
                //dietFragment.setArguments(dietData);

                //getSupportFragmentManager()
                //        .beginTransaction()
                //       .replace(R.id.content_id,dietFragment)
                //        .commit();
                break;

        }
        return true;
    }

    private void onSignedOutClean() {

    }

    // Add the user to the user schema database and initialize user
    private void onSignedInInitialize(FirebaseUser user) {
        mUserFb = user;

        userEmailNav.setText(user.getEmail().toString());
        userNameNav.setText(user.getDisplayName().toString());


        // get the image for the user
        mFirebaseDatabase.getReference().child("Users").child(user.getUid()).child("photourl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("URL -->", dataSnapshot.getValue().toString());

                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());


                Glide.with(getApplicationContext() /* context */)
                        .using(new FirebaseImageLoader())
                        .load(imageStorageReference)
                        .into(userImageNav);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){

            }
        }

        if (requestCode == NEW_USER) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // Get new user ID
                String newuser = data.getExtras().getString("SelectedUser");

                // Add the user as a members to the chat room
                mFirebaseDatabase.getReference().child("ChatRooms").child(chatRoomID).child("members").child(newuser).setValue("member");

            }
        }

        if (requestCode == REMOVE_USER) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // Get new user ID
                String selectedUser = data.getExtras().getString("SelectedUser");

                // Add the user as a members to the chat room
                mFirebaseDatabase.getReference().child("ChatRooms").child(chatRoomID).child("members").child(selectedUser).removeValue();

            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if(id == R.id.nav_all_members) {
            // create a new intent to list all the users in the database and return the selected user
            Intent listofMembersIntent = new Intent(getApplicationContext(), ListOfUsersActivity.class);
            listofMembersIntent.putExtra("onlymembers", true);
            listofMembersIntent.putStringArrayListExtra("ListOfMembers", (ArrayList<String>) membersList);
            listofMembersIntent.putExtra("ChatRoomID", chatRoomID);
            startActivity(listofMembersIntent);

            // Adds members to the list
        }else if (id == R.id.nav_add_members) {

            // create a new intent to list all the users in the database and return the selected user
            Intent pickContactIntent = new Intent(getApplicationContext(), ListOfUsersActivity.class);
            pickContactIntent.putStringArrayListExtra("ListOfMembers", (ArrayList<String>) membersList);
            pickContactIntent.putExtra("ChatRoomID", chatRoomID);
            pickContactIntent.putExtra("onlymembers", false);
            startActivityForResult(pickContactIntent, NEW_USER);

            // Removes a member from the group
        }else if(id == R.id.nav_remove_member) {

            // create a new intent to list all the users in the database and return the selected user
            Intent pickContactIntent = new Intent(getApplicationContext(), ListOfUsersActivity.class);
            pickContactIntent.putStringArrayListExtra("ListOfMembers", (ArrayList<String>) membersList);
            pickContactIntent.putExtra("ChatRoomID", chatRoomID);
            pickContactIntent.putExtra("onlymembers", true);
            startActivityForResult(pickContactIntent, REMOVE_USER);


            // Change email, password, etc.
        } else if(id == R.id.nav_setting) {
            Intent intent = new Intent(getApplicationContext(), UpdateProfileActivity.class);
            intent.putExtra("ChatRoomID", chatRoomID);
            startActivity(intent);
        }else if(id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();

            // displays a list of members
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
}
