package umd.cmsc436.cmsc436finalproject;

/**
 * Created by ahmedinibrahim on 4/10/17.
 */

public class Messages {
    private String text;
    private String name;

    Messages(){}
    Messages(String text, String name){
        this.text = text;
        this.name = name;
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
}
