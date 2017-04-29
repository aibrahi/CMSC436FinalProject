package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 4/22/17.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmedinibrahim on 4/17/17.
 */

public class UsersChatRoomActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        String chatRoomID = getIntent().getStringExtra("ChatRoomID");
        String chatRoomNAME = getIntent().getStringExtra("ChatRoomNAME");
        return UsersChatRoomFragment.newInstance(chatRoomID, chatRoomNAME);
    }



}


