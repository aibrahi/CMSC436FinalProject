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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

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

    private UsersAdapter adapter;

    public static Fragment newInstance() {
        return new ListOfUsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

//                    if(!membersList.contains(data.getKey())){
                    // use this object and store it into an ArrayList<Template> to use it further
                    adapter.add(new User(data.getKey(), data.getValue().toString()));
//                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view.findViewById(R.id.usersTextView);
                User user = (User) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), "selected Item Name is " + user.getId(), Toast.LENGTH_SHORT).show();

                Intent data = new Intent();
                data.putExtra("SelectedUser", user.getId());
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

            user = getItem(position);

            messageTextView.setText(user.getName());

            return convertView;
        }
    }


    private class User {
        private String id;
        private String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
    }


}




