package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 4/22/17.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ahmedinibrahim on 4/17/17.
 */

public class ListOfUsersFragment extends Fragment {

    // Firebase Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private List<String> membersList;
    private ArrayList<String> list;
    private ProgressBar mProgressBar;
    private ListView mMessageListView;
    private Boolean onlyMembers;
    private Boolean removeMember;
    private String chatRoomID;

    private FirebaseUser currentUser;

    private UsersAdapter adapter;

    public static Fragment newInstance(ArrayList membersList, String chatRoomID, Boolean onlyMembers, Boolean removeMember) {

        Bundle args = new Bundle();
        args.putString("chatRoomID", chatRoomID);
        args.putBoolean("onlyMembers", onlyMembers);
        args.putStringArrayList("membersList", membersList);
        args.putBoolean("removeMember", removeMember);

        ListOfUsersFragment fragment = new ListOfUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        chatRoomID = getArguments().getString("chatRoomID");
        onlyMembers = getArguments().getBoolean("onlyMembers");
        membersList = getArguments().getStringArrayList("membersList");
        removeMember = getArguments().getBoolean("removeMember");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_listofusers, null);

        // Retrieve an instance of your database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Users");

        mMessageListView = (ListView) view.findViewById(R.id.usersListView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressUsersBar);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Initialize message ListView and its adapter
        ArrayList<User> arrayOfUsers = new ArrayList<User>();
        adapter = new UsersAdapter(getActivity(), R.layout.item_users, arrayOfUsers);
        mMessageListView.setAdapter(adapter);

        mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){

                    if(onlyMembers){
                        if (membersList.contains(data.getKey())){
//                            Log.d("VALUE -->",data.getValue().toString());

                            User user = data.getValue(User.class);

                            // use this object and store it into an ArrayList<Template> to use it further
                            adapter.add(user);
                        }
                    }else{

                        if(!membersList.contains(data.getKey()))
                        {
                            // use this object and store it into an ArrayList<Template> to use it further
                            User user = data.getValue(User.class);
                            adapter.add(user);
                        }

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // removing members
        if(onlyMembers && removeMember){
            mMessageListView.setClickable(true);

        }
        // viewing members only
        else if(onlyMembers && !removeMember){
            mMessageListView.setClickable(false);

        }
        // Adding new members
        else if(!removeMember && !onlyMembers){
            mMessageListView.setClickable(true);

        }

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view.findViewById(R.id.usersTextView);
                User user = (User) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "selected Item Name is " + user.getUid(), Toast.LENGTH_SHORT).show();

                Intent data = new Intent();
                data.putExtra("SelectedUser", user.getUid());
                getActivity().setResult(RESULT_OK, data);
                getActivity().finish();

            }
        });


        return view;
    }



    private class UsersAdapter extends ArrayAdapter<User> {

        private User user;

        public UsersAdapter(Context context, int resource, List<User> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_users, parent, false);
            }

            TextView messageTextView = (TextView) convertView.findViewById(R.id.usersTextView);
            ImageView userImage = (ImageView) convertView.findViewById(R.id.userImage);

            user = getItem(position);

            if(user.getUid().equals(currentUser.getUid())){
                messageTextView.setText("You");
            }else{
                messageTextView.setText(user.getDisplayName());
            }

            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotourl());


            Glide.with(getApplicationContext() /* context */)
                    .using(new FirebaseImageLoader())
                    .load(imageStorageReference)
                    .into(userImage);


            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}




