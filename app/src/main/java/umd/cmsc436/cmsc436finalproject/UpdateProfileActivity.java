package umd.cmsc436.cmsc436finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by ahmedinibrahim on 5/7/17.
 */

public class UpdateProfileActivity extends SingleFragmentActivity{

    public Fragment createFragment() {


        String chatRoomID = getIntent().getStringExtra("ChatRoomID");


        return UpdateProfileFragment.newInstance(chatRoomID);
    }
}
