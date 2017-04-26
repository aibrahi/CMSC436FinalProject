package umd.cmsc436.cmsc436finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Clayton on 4/25/2017.
 */

public class BillFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    View inflated_view;



    @Override
    public void onCreate(Bundle savedInstancState) {
        super.onCreate(savedInstancState);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflated_view = inflater.inflate(R.layout.fragment_bill, container, false);

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
