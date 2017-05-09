package umd.cmsc436.cmsc436finalproject.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    private ArrayList<String> ignored;
    private User owner;
    private int year = -1;
    private int month = -1;
    private int day = -1;
    //private Calendar date;


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

    public void setIgnored(ArrayList<String> map) {
        ignored = map;
    }

    public ArrayList<String> getIgnored() {
        return this.ignored;
    }

    public void setOwner(User user) {
        owner = user;
    }

    public User getOwner() {
        return owner;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setMonth (int month) {
        this.month = month;
    }

    public int getMonth(){
        return month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

//    public void setDate(int year, int month, int day){
//        date = Calendar.getInstance();
//        date.set(year, month, day);
//    }
//
//    public long getDate() {
//        return date.getTimeInMillis();
//    }


    public String getDateToString() {
        return month + "/" + day + "/" + year;
    }




}
