package umd.cmsc436.cmsc436finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
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
    private int total = 0;




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
                TableLayout users_bill_table = (TableLayout) inflated_view.findViewById(R.id.users_bill_table);
                for (int index = 0; index < users_bill_table.getChildCount(); index++) {
                    View table_row = users_bill_table.getChildAt(index);
                    if (table_row instanceof TableRow) {
                        TableRow curr_row = (TableRow) table_row;
                        TextView user_name = (TextView) curr_row.getChildAt(0);
                        //EditText bill_amount = (EditText) curr_row.getChildAt(1);
                        if (user_name.getText().toString().equals(user.getDisplayName())){

                        }
                    }
                }
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

                            //chatroom_users = new ArrayList<String>();

                            if (chatRoom.getMembers().containsKey(user.getUid())){
                                hasChatRoom = true;
                                //System.out.println(chatRoom.getChatRoomName());
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


                TableLayout users_bill_table = (TableLayout) inflated_view.findViewById(R.id.users_bill_table);
                for (String a: chatroom_users) {
                    //user_map.get(a);

                    TableRow new_row = new TableRow(getActivity());
                    TextView user_textview = new TextView(getActivity());
                    user_textview.setText((String) user_map.get(a));
                    //user_textview.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

                    EditText bill_edittext = new EditText(getActivity());
                    bill_edittext.setHint("1000.00");
                    bill_edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                    //bill_edittext.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

                    new_row.addView(user_textview);
                    new_row.addView(bill_edittext);

                    //if this is the current user, then we can allow him to say if he paid or is unpaid
                    if (a.equals(user.getUid())) {
                        Switch paid_switch = new Switch(getActivity());
                        final TextView paid_textview = new TextView(getActivity());
                        paid_textview.setText(R.string.unpaid_label);
                        paid_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                //switch is toggled to paid
                                if (isChecked) {
                                    paid_textview.setText(R.string.paid_label);
                                } else {
                                    paid_textview.setText(R.string.unpaid_label);
                                }

                            }

                        });

                        new_row.addView(paid_switch);
                        new_row.addView(paid_textview);
                    } else {
                        //otherwise, for other users, only show their status of paid or unpaid
                        TextView paid_textview = new TextView(getActivity());
                        paid_textview.setText(R.string.unpaid_label);
                        new_row.addView(paid_textview);
                    }

                    users_bill_table.addView(new_row);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
