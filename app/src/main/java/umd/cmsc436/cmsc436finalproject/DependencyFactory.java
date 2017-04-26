package umd.cmsc436.cmsc436finalproject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Clayton on 4/25/2017.
 */

public class DependencyFactory {
    //user id, chatroom id
    private static UserChatroomService userChatroomService;

    public static UserChatroomService getUserChatroomService() {
        if (userChatroomService == null) {
            userChatroomService = new UserChatroomService();
        }
        return userChatroomService;
    }


}
