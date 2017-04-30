package umd.cmsc436.cmsc436finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.ui.auth.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Clayton on 4/25/2017.
 */

public class BillFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    View inflated_view;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private boolean hasChatRoom = false;
    private ArrayList<String> chatroom_users = new ArrayList<String>();
    private HashMap<String, Object> user_map = new HashMap<String, Object>();




    @Override
    public void onCreate(Bundle savedInstancState) {
        super.onCreate(savedInstancState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        create_refs();
        //create_users_ref();

    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflated_view = inflater.inflate(R.layout.fragment_bill, container, false);


        System.out.println("Current user:" + user);

        if(user != null){
            // user is signed in
            System.out.println("Display name:" + user.getDisplayName());
            System.out.println("User ID:" + user.getUid());

        }


        System.out.println("what is this:" + mDatabase.child("ChatRooms"));

        Button save_button = (Button) inflated_view.findViewById(R.id.bill_save_button);
        save_button.setOnClickListener(this);

        Button cancel_button = (Button) inflated_view.findViewById(R.id.bill_cancel_button);
        cancel_button.setOnClickListener(this);




        return inflated_view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bill_cancel_button:
                break;
            case R.id.bill_save_button:
                break;
        }
    }

    public void create_bill_participants(){


    }

    public void create_refs() {
        DatabaseReference ref = mDatabase.child("ChatRooms");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            ChatRoom chatRoom = data.getValue(ChatRoom.class);

                            if (chatRoom.getMembers().containsKey(user.getUid())){
                                hasChatRoom = true;
                                System.out.println(chatRoom.getChatRoomName());
                                for (String a: chatRoom.getMembers().keySet()){
                                    if (a != null) {
                                        chatroom_users.add(a);
                                    }
                                }


                                create_users_ref();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void create_users_ref() {
        DatabaseReference user_ref = mDatabase.child("Users");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    user_map.put(data.getKey(), data.getValue());
                }

                System.out.println(user_map.toString());

                TableLayout users_bill_table = (TableLayout) inflated_view.findViewById(R.id.users_bill_table);
                for (String a: chatroom_users) {
                    //user_map.get(a);
                    TableRow new_row = new TableRow(getActivity());
                    TextView user_textview = new TextView(getActivity());
                    user_textview.setText((String) user_map.get(a));
                    new_row.addView(user_textview);
                    users_bill_table.addView(new_row);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
