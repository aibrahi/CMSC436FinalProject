package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 4/22/17.
 */


        import com.firebase.ui.auth.ui.User;
        import com.google.firebase.auth.FirebaseUser;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

/**
 * Created by ahmedinibrahim on 4/17/17.
 */

public class ChatRoom {


    private HashMap<String, String> members;
    private String chatRoomName;
    private String id;


    public ChatRoom(){
        members = new HashMap<String, String>();
        this.chatRoomName = "";
    }


    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public void addMembers(FirebaseUser user) {
        members.put(user.getUid(), "member");
    }

    public void addOwner(FirebaseUser user) {
        members.put(user.getUid(), "owner");
    }

    public void setMembers(HashMap<String, String> map)
    {
        this.members = map;
    }
    public HashMap<String, String> getMembers()
    {
        return this.members;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

