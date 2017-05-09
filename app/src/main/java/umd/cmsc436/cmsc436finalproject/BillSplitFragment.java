package umd.cmsc436.cmsc436finalproject;

import android.app.Notification;
import android.app.NotificationManager;
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
import java.util.Date;
import java.util.List;

import umd.cmsc436.cmsc436finalproject.model.Bill;

import static android.app.Activity.RESULT_OK;


public class BillSplitFragment extends android.support.v4.app.Fragment {

    private static final int REQUEST_CODE_CREATE_BILL = 1;
    private static final int NOTIFICATION_BILL_ID = 2;
    private RecyclerView billRecyclerView;
    private FirebaseDatabase mFireBaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mChatroomDatabaseReference;
    private DatabaseReference mBillsDatabaseReference;
    private FirebaseUser user;
    private String chatRoomID;
    private ValueEventListener chatlistener;
    private ValueEventListener billlistener;
    private BillAdapter billAdapter;
    private ArrayList<Bill> bills;
    private ArrayList<String> billkeys;
    private boolean first;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        mChatroomDatabaseReference = mFireBaseDatabase.getReference().child("ChatRooms");

        bills = new ArrayList<Bill>();
        billkeys = new ArrayList<String>();
        first = true;

        billlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sz = bills.size();
                bills.clear();
                billkeys.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Bill abill = data.getValue(Bill.class);
                    bills.add(abill);
                    billkeys.add(data.getKey());
                }
                if(first)
                    first = false;
                else if(bills.size() > sz) {
                    Toast.makeText(getActivity(), "New bills added.", Toast.LENGTH_SHORT).show();
                    Notification.Builder nb = new Notification.Builder(getActivity().getApplicationContext())
                            .setTicker("Roomie")
                            .setSmallIcon(android.R.drawable.ic_menu_compass)
                            .setContentTitle("Roomie")
                            .setContentText("There are new bills!")
                            //.setContentIntent(sIntent)
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_BILL_ID, nb.build());
                }

                updateUI();//send notification about new bills
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        chatlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = data.getValue(ChatRoom.class);

                    if(chatRoom.getMembers().containsKey(user.getUid())){
                        chatRoomID = data.getKey();
                        mBillsDatabaseReference = mFireBaseDatabase.getReference().child("ChatRooms").child(chatRoomID).child("bills");
                        mBillsDatabaseReference.addValueEventListener(billlistener);
                        System.out.println(chatRoom.getChatRoomName());
                        mChatroomDatabaseReference.removeEventListener(chatlistener);
                        break;
                    }
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
                Intent billfrag = BillActivity.newIntent(getActivity(), "");
                startActivity(billfrag);
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
        TextView user_status;
        TextView status;
        TextView date;
        String key;


        public BillHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            description = (TextView) v.findViewById(R.id.list_bill_desc);
            user_status = (TextView) v.findViewById(R.id.list_bill_user_status);
            status = (TextView) v.findViewById(R.id.list_bill_status);
            date = (TextView) v.findViewById(R.id.list_bill_date);
        }

        public void bindBill(Bill b, String key) {
            bill = b;
            this.key = key;
            description.setText(bill.getDescription());
            if(bill.getPaid() == null || (bill.getPaid().get(user.getUid())) == null || (bill.getPaid().get(user.getUid())).equals("Unpaid"))
                user_status.setText("You have not paid!");
            else if((bill.getPaid().get(user.getUid())).equals("Paid"))
                user_status.setText("You have paid!");
            if(bill.getStatus().equals("PENDING"))
                status.setText("Bill in progress!");
            else if (bill.getStatus().equals("PAID"))
                status.setText("Bill is finished!");
            if(bill.getYear() == -1 || bill.getMonth() == -1 || bill.getDay() == -1)
                date.setText("Date: None");
            else
                date.setText("Date: " + bill.getMonth() + "/" + bill.getDay() + "/" + bill.getYear());
        }

        @Override
        public void onClick(View v) {
            Intent billfrag = BillActivity.newIntent(getActivity(), key);
            startActivity(billfrag);
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
            holder.bindBill(bills.get(position), billkeys.get(position));
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
