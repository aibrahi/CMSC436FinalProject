package umd.cmsc436.cmsc436finalproject.model;

import java.io.Serializable;
import java.util.HashMap;

import umd.cmsc436.cmsc436finalproject.User;

/**
 * Created by Vincent Ly on 4/25/2017.
 */

public class Bill implements Serializable {

    private String description;
    private String status;
    private Double total;
    private HashMap<String, Double> payments;
    private HashMap<String, String> paid;
    private User owner;

    public Bill(){

    }

    public Bill(String description, String status, HashMap<String, Double> payments, HashMap<String, String> paid, Double total){
        this.description = description;
        this.status = status;
        this.payments = payments;
        this.paid = paid;
        this.total = total;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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

    public void setPayments(HashMap<String, Double> payments)
    {
        this.payments = payments;
    }

    public HashMap<String, Double> getPayments()
    {
        return this.payments;
    }

    public void setPaid(HashMap<String, String> map) {
        paid = map;
    }

    public HashMap<String, String> getPaid() {return this.paid;}

    public void setOwner(User user) {
        owner = user;
    }

    public User getOwner() {
        return owner;
    }


}
