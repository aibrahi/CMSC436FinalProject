package umd.cmsc436.cmsc436finalproject;

import android.support.v4.app.Fragment;

/**
 * Created by ahmedinibrahim on 4/28/17.
 */

public class ListOfUsersActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return ListOfUsersFragment.newInstance();
    }
}
