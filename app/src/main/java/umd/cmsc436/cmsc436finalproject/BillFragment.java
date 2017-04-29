package umd.cmsc436.cmsc436finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Clayton on 4/25/2017.
 */

public class BillFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    View inflated_view;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private boolean hasChatRoom = false;




    @Override
    public void onCreate(Bundle savedInstancState) {
        super.onCreate(savedInstancState);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflated_view = inflater.inflate(R.layout.fragment_bill, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        System.out.println("Current user:" + user);

        if(user != null){
            // user is signed in
            System.out.println("Display name:" + user.getDisplayName());
            System.out.println("User ID:" + user.getUid());

        }

        System.out.println("what is this:" + mDatabase.child("ChatRooms"));
        DatabaseReference ref = mDatabase.child("ChatRooms");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            ChatRoom chatRoom = data.getValue(ChatRoom.class);
                            //System.out.println(chatRoom.getChatRoomName());

                            if (chatRoom.getMembers().containsKey(user.getUid())){
                                hasChatRoom = true;
                                System.out.println(chatRoom.getChatRoomName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        //System.out.println(mDatabase.child("ChatRooms"));


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



}
