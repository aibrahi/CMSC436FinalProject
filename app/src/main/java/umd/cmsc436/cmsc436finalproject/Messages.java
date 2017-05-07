package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 4/10/17.
 */


/**
 * Created by ahmedinibrahim on 4/10/17.
 */

public class Messages {
    private String text;
    private String name;
    private String uid;

    Messages(){}
    Messages(String text, String name, String uid){
        this.text = text;
        this.name = name;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
