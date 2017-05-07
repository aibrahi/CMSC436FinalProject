package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 5/5/17.
 */
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.internal.zzbmn;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.List;

/**
 * Created by ahmedinibrahim on 5/4/17.
 */

public class User {
    private String uid;
    private String displayName;
    private String email;
    private String photourl;

    public User(){}

    public User(String Uid, String name, String email, String photourl){
        this.uid = Uid;
        this.displayName = name;
        this.email = email;
        this.photourl = photourl;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
