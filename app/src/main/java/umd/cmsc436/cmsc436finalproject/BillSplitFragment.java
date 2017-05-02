package umd.cmsc436.cmsc436finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import umd.cmsc436.cmsc436finalproject.model.Bill;

import static android.app.Activity.RESULT_OK;


public class BillSplitFragment extends android.support.v4.app.Fragment {

    private static final int REQUEST_CODE_CREATE_BILL = 1;
    private RecyclerView billRecyclerView;
    private FirebaseDatabase mFireBaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mChatroomDatabaseReference;
    private DatabaseReference mBillsDatabaseReference;
    private FirebaseUser user;
    private String chatRoomID;
    private String chatRoomNAME;
    private static final String ARG_CHATROOM_ID = "chatRoomId";
    private static final String ARG_CHATROOM_NAME = "chatRoomName";
    private ValueEventListener chatlistener;
    private ValueEventListener billlistener;
    private BillAdapter billAdapter;
    private ArrayList<Bill> bills;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        mChatroomDatabaseReference = mFireBaseDatabase.getReference().child("ChatRooms");
        // Read from the database

        bills = new ArrayList<Bill>();

        billlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bills.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    System.out.println("A bill!");
                    Bill abill = data.getValue(Bill.class);

                    // if the user is in a chat group already send him/her to the chat
                    bills.add(abill);
                }
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        chatlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean found = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = data.getValue(ChatRoom.class);
                    // if the user is in a chat group already send him/her to the chat

                    if(chatRoom.getMembers().containsKey(user.getUid())){
                        // the user has been found in a group
                        found = true;
                        chatRoomID = data.getKey();
                        mBillsDatabaseReference = mFireBaseDatabase.getReference().child("ChatRooms").child(chatRoomID).child("bills");
                        mBillsDatabaseReference.addValueEventListener(billlistener);
                        System.out.println(chatRoom.getChatRoomName());
                        break;
                    }
                }
                if(found) {
                    mChatroomDatabaseReference.removeEventListener(chatlistener);
                }else {

                    // Once the database changes make appropriate changes
                    //updateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_split, null);

        billRecyclerView = (RecyclerView)view.findViewById(R.id.bill_recycler_view);
        billRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mChatroomDatabaseReference.addListenerForSingleValueEvent(chatlistener);

        ((Button)view.findViewById(R.id.add_bill)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bill dummyBill = new Bill();
                dummyBill.setDescription("testbill");
                dummyBill.setStatus(Bill.Status.PENDING.toString());
                mBillsDatabaseReference.push().setValue(dummyBill);
            }
        });

        updateUI();


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CREATE_BILL) {
            if (resultCode == RESULT_OK) {
                //do something
            }
        }
    }

    public static Fragment newInstance() {
        return new BillSplitFragment();
    }

    private class BillHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Bill bill;
        TextView description;
        TextView status;


        public BillHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            description = (TextView) v.findViewById(R.id.list_bill_desc);
            status = (TextView) v.findViewById(R.id.list_bill_status);
        }

        public void bindBill(Bill b) {
            bill = b;
            description.setText(bill.getDescription());
            status.setText(bill.getStatus());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity().getApplicationContext(), bill.toString(), Toast.LENGTH_SHORT).show();
            //intent fragmentviewer
        }
    }

    private class BillAdapter extends RecyclerView.Adapter<BillHolder> {

        private List<Bill> bills;

        public BillAdapter(List<Bill> b) {
            bills = b;
        }

        public void setBills(List<Bill> b) {
            bills = b;
        }

        @Override
        public BillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_bill, null);
            return new BillHolder(v);
        }

        @Override
        public void onBindViewHolder(BillHolder holder, int position) {
            holder.bindBill(bills.get(position));
        }

        @Override
        public int getItemCount() {
            return bills.size();
        }
    }

    private void updateUI() {
        if(billAdapter == null) {
            billAdapter = new BillAdapter(bills);
            billRecyclerView.setAdapter(billAdapter);
        }
        else {
            billAdapter.setBills(bills);
            billAdapter.notifyDataSetChanged();
        }
    }
}
