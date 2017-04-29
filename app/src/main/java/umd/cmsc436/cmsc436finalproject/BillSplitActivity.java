package umd.cmsc436.cmsc436finalproject;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BillSplitActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return BillSplitFragment.newInstance();
    }
}
