package umd.cmsc436.cmsc436finalproject.model;

import java.io.Serializable;

/**
 * Created by Vincent Ly on 4/25/2017.
 */

public class Bill implements Serializable {

    private String description;
    private String status;

    public Bill(){

    }

    public Bill(String description, String status){
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public enum Status {
        PENDING, DONE;
    }
}
