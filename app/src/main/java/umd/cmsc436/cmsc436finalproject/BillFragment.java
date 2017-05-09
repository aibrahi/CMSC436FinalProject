package umd.cmsc436.cmsc436finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import umd.cmsc436.cmsc436finalproject.model.Bill;

/**
 * Created by Clayton on 4/25/2017.
 */

public class BillFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    View inflated_view;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private umd.cmsc436.cmsc436finalproject.User cmsc436_user;
    private boolean hasChatRoom = false;
    private ArrayList<String> chatroom_users = new ArrayList<String>();
    private HashMap<String, Object> user_map = new HashMap<String, Object>();
    private int total = 0;
    private DatabaseReference mBillsDatabaseReference;
    private static final String BILL_ID = "BILL_ID";
    private String bill_id;
    private Bill current_bill;
    private ValueEventListener billlistener;
    private EditText billname;
    private EditText billtotal;
    private TextView due_date;
    private TextView owner_text;
    private ArrayList<String> removed_users = new ArrayList<String>();

    private String curr_user_id;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        bill_id = args.getString(BILL_ID);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        current_bill = new Bill();

        billlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getKey().equals(bill_id)) {
                        current_bill = data.getValue(Bill.class);
                        billname.setText(current_bill.getDescription());
                        billtotal.setText(current_bill.getTotal().toString());
                        if (current_bill.getYear() == -1) {
                            System.out.println("really" + current_bill.getDay());
                            due_date.setText(R.string.no_due_date_set_label);
                        } else {
                            //due_date.setText(current_bill.getMonth() + "/" + current_bill.getDay() + "/" + current_bill.getYear());
                            //due_date.setText(current_bill.getDate().toString());
                            due_date.setText(current_bill.getDateToString());
                        }
                        owner_text = (TextView) inflated_view.findViewById(R.id.owner_field);
                        owner_text.append(" " + current_bill.getOwner().getDisplayName());
                        mBillsDatabaseReference.removeEventListener(billlistener);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        //create_refs();
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
            curr_user_id = user.getUid();
        }


        System.out.println("what is this:" + mDatabase.child("ChatRooms"));

        billname = (EditText)inflated_view.findViewById(R.id.bill_name);
        billtotal = (EditText)inflated_view.findViewById(R.id.total_edittext);

        due_date = (TextView) inflated_view.findViewById(R.id.calendar_date);


        create_refs();

        Button save_button = (Button) inflated_view.findViewById(R.id.bill_save_button);
        save_button.setOnClickListener(this);

        Button cancel_button = (Button) inflated_view.findViewById(R.id.bill_cancel_button);
        cancel_button.setOnClickListener(this);

        Button remove_button = (Button) inflated_view.findViewById(R.id.bill_remove_button);
        remove_button.setOnClickListener(this);

        Button close_button = (Button) inflated_view.findViewById(R.id.bill_close_button);
        close_button.setOnClickListener(this);

        Button calendar_button = (Button) inflated_view.findViewById(R.id.calendar_button);
        calendar_button.setOnClickListener(this);



        return inflated_view;
    }

    @Override
    public void onClick(View v) {
        TableLayout users_bill_table = (TableLayout) inflated_view.findViewById(R.id.users_bill_table);
        Button close_button = (Button) inflated_view.findViewById(R.id.bill_close_button);
        switch(v.getId()) {
            case R.id.calendar_button:
                DialogFragment calendarFragment = new DatePickerFragment();
                calendarFragment.show(getFragmentManager(), "CalendarFragment");
                break;
            case R.id.bill_cancel_button:
                getActivity().finish();
                break;
            case R.id.bill_remove_button:
                for (int index = 0; index < users_bill_table.getChildCount(); index++) {
                    View table_row = users_bill_table.getChildAt(index);
                    if (table_row instanceof TableRow) {
                        TableRow user_row = (TableRow) table_row;
                        Button remove_button = (Button) user_row.getChildAt(0);
                        remove_button.setVisibility(View.VISIBLE);
                    }
                }
                close_button.setVisibility(View.VISIBLE);
                break;
            case R.id.bill_close_button:
                for (int index = 0; index < users_bill_table.getChildCount(); index++) {
                    View table_row = users_bill_table.getChildAt(index);
                    if (table_row instanceof TableRow) {
                        TableRow user_row = (TableRow) table_row;
                        Button remove_button = (Button) user_row.getChildAt(0);
                        remove_button.setVisibility(View.GONE);
                    }
                }
                close_button.setVisibility(View.GONE);
                break;
            case R.id.bill_save_button:
                EditText total_edittext = (EditText) inflated_view.findViewById(R.id.total_edittext);
                double running_total = 0;
                int number_of_paid = 0;
                TextView paid_text;
                HashMap<String, String> paid_map = current_bill.getPaid();
                HashMap<String, Double> payments = current_bill.getPayments();
                ArrayList<String> ignored = current_bill.getIgnored();

                if (payments == null) {
                    payments = new HashMap<String, Double>();
                }

                if (paid_map == null) {
                    paid_map = new HashMap<String, String>();
                }

                if (ignored == null) {
                    ignored = new ArrayList<String>();
                }

                //iterates through the table to see if each user has an amount listed for them

                for (String user_id: removed_users) {
                    if (!ignored.contains(user_id)) {
                        ignored.add(user_id);
                        payments.remove(user_id);
                        paid_map.remove(user_id);
                    }
                }

                int total_members = users_bill_table.getChildCount();
                for (int index = 0; index < users_bill_table.getChildCount(); index++) {
                    View table_row = users_bill_table.getChildAt(index);
                    if (table_row instanceof TableRow) {
                        TableRow curr_row = (TableRow) table_row;
                        TextView user_id = (TextView) curr_row.getChildAt(1);
                        TextView user_name = (TextView) curr_row.getChildAt(2);
                        EditText bill_amount = (EditText) curr_row.getChildAt(3);

                        //if the user is not in the ignore list
                        if (!ignored.contains(user_id.getText().toString())) {

                            if (user_name.getText().toString().equals(user.getDisplayName())) {
                                Switch user_paid_switch = (Switch) curr_row.getChildAt(4);
                                if (user_paid_switch.isChecked()) {
                                    //user has paid
                                    System.out.println("yeah u right");
                                }
                            }

                            if (user_name.getText().toString().equals(user.getDisplayName())) {
                                paid_text = (TextView) curr_row.getChildAt(5);

                                //user is already inside the paidmap
                                if (paid_map.containsKey(user.getUid())) {

                                    paid_map.remove(user.getUid());
                                    paid_map.put(user.getUid(), paid_text.getText().toString());
                                    //user is not in the paid map
                                } else {
                                    paid_map.put(user.getUid(), paid_text.getText().toString());
                                }
                            } else {
                                paid_text = (TextView) curr_row.getChildAt(4);

                            }

                            if (paid_text.getText().toString().equals("Paid")) {
                                number_of_paid++;
                            }

                            if (bill_amount.getText().toString().isEmpty()) {
                                AlertDialog empty_bill_amount = create_dialog_box("Empty bill amount for " + user_name.getText(), "Please re-check the money values for: " + user_name.getText());
                                empty_bill_amount.show();
                                break;
                            } else {

                                if (payments.containsKey(user_id)) {
                                    payments.remove(user_id);
                                    payments.put(user_id.getText().toString(), Double.parseDouble(bill_amount.getText().toString()));
                                } else {
                                    payments.put(user_id.getText().toString(), Double.parseDouble(bill_amount.getText().toString()));
                                }
                                running_total += Double.parseDouble(bill_amount.getText().toString());
                            }
                        }
                    }
                }

                //if the user input a total then create or edit a bill for them
                if (total_edittext.getText() != null && total_edittext.getText().length() > 0) {

                    if (running_total != Double.parseDouble(total_edittext.getText().toString())) {
                        AlertDialog incorrect_total = create_dialog_box("Incorrect Total", "Please re-check values");
                        incorrect_total.show();
                        break;
                    }

                    TextView calendar_date = (TextView) inflated_view.findViewById(R.id.calendar_date);
                    if (!calendar_date.getText().toString().equals(R.string.no_due_date_set_label)) {
                        String[] calendar_values = calendar_date.getText().toString().split("/");
                        current_bill.setMonth(Integer.parseInt(calendar_values[0]));
                        current_bill.setDay(Integer.parseInt(calendar_values[1]));
                        current_bill.setYear(Integer.parseInt(calendar_values[2]));
                        //current_bill.setDate(Integer.parseInt(calendar_values[2]), Integer.parseInt(calendar_values[1]), Integer.parseInt(calendar_values[0]));
                    }

                    current_bill.setDescription(((EditText)inflated_view.findViewById(R.id.bill_name)).getText().toString());
                    current_bill.setPaid(paid_map);
                    current_bill.setPayments(payments);
                    current_bill.setIgnored(ignored);

                    if (number_of_paid == total_members) {
                        current_bill.setStatus("PAID");
                    } else {
                        current_bill.setStatus("PENDING");
                    }

                    current_bill.setTotal(Double.parseDouble(((EditText)inflated_view.findViewById(R.id.total_edittext)).getText().toString()));

                    if(bill_id.equals("")) {
                        mBillsDatabaseReference.push().setValue(current_bill);
                    } else
                        mBillsDatabaseReference.child(bill_id).setValue(current_bill);
                    getActivity().finish();

                    //otherwise tell them that the bill is incomplete
                } else {
                    AlertDialog missing_dialog = create_dialog_box("Incomplete Bill", "Please enter values.");
                    missing_dialog.show();
                }

                break;
        }
    }

    public AlertDialog create_dialog_box(String title, String message) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View dialogView = li.inflate(R.layout.dialog_incorrect_total, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        // set custom dialog icon

        //alertDialogBuilder.setIcon(android.R.drawable.ic_input_add);

        // set custom_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        return alertDialog;
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

                                //System.out.println("helloooooo");
                                //System.out.println(chatRoom.getChatRoomName());

                                for (String a: chatRoom.getMembers().keySet()){
                                    if (a != null) {
                                        Log.d("MEMBERS--->", a);
                                        chatroom_users.add(a);
                                        //System.out.println("lololaoaoaoa" + a);
                                    }
                                }

                                mBillsDatabaseReference = mDatabase.child("ChatRooms").child(data.getKey()).child("bills");
                                mBillsDatabaseReference.addListenerForSingleValueEvent(billlistener);
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
                    umd.cmsc436.cmsc436finalproject.User user = data.getValue(umd.cmsc436.cmsc436finalproject.User.class);
                    cmsc436_user = user;

                    if (cmsc436_user.getUid().equals(curr_user_id)) {
                        if (current_bill.getOwner() == null) {
                            current_bill.setOwner(cmsc436_user);
                            TextView owner_textView = (TextView) inflated_view.findViewById(R.id.owner_field);
                            owner_textView.append(" " + cmsc436_user.getDisplayName());
                        }
                    }

                    user_map.put(user.getUid(), user.getDisplayName());
                }


                TableLayout users_bill_table = (TableLayout) inflated_view.findViewById(R.id.users_bill_table);
                HashMap<String, String> paid_map = current_bill.getPaid();
                HashMap<String, Double> payments = current_bill.getPayments();
                ArrayList<String> ignored = current_bill.getIgnored();
                Button remove_button = (Button) inflated_view.findViewById(R.id.bill_remove_button);

                if (payments == null) {
                    payments = new HashMap<String, Double>();
                }

                if (ignored == null) {
                    ignored = new ArrayList<String>();
                }


                for (String a: chatroom_users) {
                    //user_map.get(a);

                    //System.out.println("????" + user_map.get(a) + "####" + a);
                    if (!ignored.contains(a)) {

                        final TableRow new_row = new TableRow(getActivity());
                        TextView user_textview = new TextView(getActivity());
                        TextView user_id_textview = new TextView(getActivity());
                        Button remove_user = new Button(getActivity());

                        //final ArrayList<String> removed_users = new ArrayList<String>();

                        remove_user.setVisibility(View.GONE);
                        remove_user.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
                        remove_user.setOnClickListener(
                                new View.OnClickListener(){
                                    public void onClick(View v) {
                                        //remove button gone means it is really gone
                                        //if (remove_button.getVisibility() != View.GONE) {
                                        TableLayout users_bill_table = (TableLayout) inflated_view.findViewById(R.id.users_bill_table);
                                        for (int index = 0; index < users_bill_table.getChildCount(); index++) {
                                            View table_row = users_bill_table.getChildAt(index);
                                            if (table_row == new_row) {
                                                TableRow user_row = new_row;
                                                TextView user_id = (TextView) user_row.getChildAt(1);
                                                removed_users.add(user_id.getText().toString());
                                                users_bill_table.removeView(user_row);
                                            }
                                        }
                                        //food_table.removeView(new_row);
                                        //}
                                    }
                                });
                        user_id_textview.setText(a);

                        user_textview.setText((String) user_map.get(a));
                        user_id_textview.setVisibility(View.GONE);
                        //user_textview.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

                        EditText bill_edittext = new EditText(getActivity());
                        bill_edittext.setHint("1000.00");
                        bill_edittext.setInputType(InputType.TYPE_CLASS_NUMBER);

                        if (payments.containsKey(a)) {
                            bill_edittext.setText(String.valueOf(payments.get(a)));
                        }

                        if (!current_bill.getOwner().getUid().equals(curr_user_id)) {
                            //System.out.println("what:" + current_bill.getOwner().getUid() + ":huh:" + curr_user_id);
                            bill_edittext.setFocusable(false);
                            remove_button.setEnabled(false);

                        }
                        //bill_edittext.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

                        new_row.addView(remove_user);
                        new_row.addView(user_id_textview);
                        new_row.addView(user_textview);
                        new_row.addView(bill_edittext);

                        //if this is the current user, then we can allow him to say if he paid or is unpaid
                        if (a.equals(user.getUid())) {
                            Switch paid_switch = new Switch(getActivity());
                            final TextView paid_textview = new TextView(getActivity());

                            if (paid_map == null) {
                                paid_textview.setText(R.string.unpaid_label);
                            } else {
                                if (paid_map.get(a) == null) {
                                    paid_textview.setText(R.string.unpaid_label);
                                } else {
                                    paid_textview.setText(paid_map.get(a));
                                }
                            }

                            if (paid_textview.getText().toString().equals("Paid")) {
                                paid_switch.setChecked(true);
                            }

                            paid_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

                            if (paid_map != null && paid_map.get(a) != null) {
                                paid_textview.setText(paid_map.get(a));
                            } else {
                                paid_textview.setText(R.string.unpaid_label);
                            }


                            new_row.addView(paid_textview);
                        }

                        users_bill_table.addView(new_row);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static BillFragment newInstance(String bill_id) {
        Bundle b = new Bundle();
        b.putString(BILL_ID, bill_id);
        BillFragment bf = new BillFragment();
        bf.setArguments(b);
        return bf;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            LayoutInflater li = LayoutInflater.from(getContext());
            TextView calendar_date = (TextView) getActivity().findViewById(R.id.calendar_date);
            calendar_date.setText((month+1) + "/" + day + "/" + year);
        }
    }

}
