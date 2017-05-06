package umd.cmsc436.cmsc436finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Vincent Ly on 5/3/2017.
 */

public class BillActivity extends SingleFragmentActivity{
    private static final String BILL_ID = "BILL_ID";
    public Fragment createFragment() {
        return BillFragment.newInstance(getIntent().getStringExtra(BILL_ID));
    }

    public static Intent newIntent(Context c, String b) {
        Intent data = new Intent(c, BillActivity.class);
        data.putExtra(BILL_ID, b);
        return data;
    }
}
