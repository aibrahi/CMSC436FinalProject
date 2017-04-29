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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import umd.cmsc436.cmsc436finalproject.model.Bill;

import static android.app.Activity.RESULT_OK;


public class BillSplitFragment extends android.support.v4.app.Fragment {

    private static final int REQUEST_CODE_CREATE_BILL = 1;
    private RecyclerView billRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_split, null);

        billRecyclerView = (RecyclerView)view.findViewById(R.id.bill_recycler_view);
        billRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_bill_split, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_item_add_bill:
                //intent= new Intent(getActivity().getApplicationContext(), FragmentViewer.class);
                //startActivityForResult(intent, REQUEST_CODE_CREATE_BILL);
                break;
            default: return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
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
            status.setText(bill.getStatus().toString());
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
}
