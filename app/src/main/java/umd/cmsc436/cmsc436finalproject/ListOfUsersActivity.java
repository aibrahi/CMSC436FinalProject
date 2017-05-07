package umd.cmsc436.cmsc436finalproject;

import android.support.v4.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ahmedinibrahim on 4/28/17.
 */

public class ListOfUsersActivity extends SingleFragmentActivity {

    public Fragment createFragment() {

        ArrayList membersList = getIntent().getStringArrayListExtra("ListOfMembers");
        String chatRoomID = getIntent().getStringExtra("ChatRoomID");
        Boolean onlyMembers = getIntent().getBooleanExtra("onlymembers", false);

        return ListOfUsersFragment.newInstance(membersList, chatRoomID, onlyMembers);
    }
}
