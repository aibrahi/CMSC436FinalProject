package umd.cmsc436.cmsc436finalproject.model;

import java.io.Serializable;

/**
 * Created by Vincent Ly on 4/25/2017.
 */

public class Bill implements Serializable {

    private String description;
    private Status status;

    public Bill(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public int getStatusPosition() {
        switch (status) {
            case PENDING:
                return 0;
            case DONE:
                return 1;
            default:
                return 0;
        }
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatus(int position) {
        switch (position) {
            case 0:
                this.status = Status.PENDING;
                break;
            case 1:
                this.status = Status.DONE;
                break;
            default:
                this.status = Status.PENDING;
                break;
        }
    }

    @Override
    public String toString() {
        return description + ", " + status.toString();
    }

    public enum Status {
        PENDING, DONE;
    }
}
