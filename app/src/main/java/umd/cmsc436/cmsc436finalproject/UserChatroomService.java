package umd.cmsc436.cmsc436finalproject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Clayton on 4/25/2017.
 */

public class UserChatroomService {

    private static HashMap<String, ArrayList<String>> user_chatroom_map;

    public UserChatroomService(){
        user_chatroom_map = new HashMap<String, ArrayList<String>>();
    }

    //returns the chatrooms that the user is currently in
    //if the user is not in any, returns an empty arraylist
    public ArrayList<String> getChatrooms(String uid){
        if (user_chatroom_map.containsKey(uid)) {
            return user_chatroom_map.get(uid);
        } else {
            return new ArrayList<String>();
        }
    }

    //adds the user into the hashmap
    public void addUserToMap(String uid, String chatroom_id) {
        if (user_chatroom_map.containsKey(uid)) {
            if (user_chatroom_map.get(uid) != null) {
                user_chatroom_map.get(uid).add(chatroom_id);
            } else {
                ArrayList<String> chatrooms = new ArrayList<String>();
                chatrooms.add(chatroom_id);
                user_chatroom_map.put(uid, chatrooms);
            }
        }
    }

    public void removeUserFromMap(String uid) {
        if (user_chatroom_map.containsKey(uid)) {
            user_chatroom_map.remove(uid);
        }
    }



}
