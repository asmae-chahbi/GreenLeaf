package com.example.greenleaf.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message implements Comparable<Message>{

    String message;
    String currentDate;
    String currentTime;
    String de;
    boolean vu;

    public Message(){

    }

    public Message(String currentDate, String currentTime) {
        this.message = "";
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }


    public Message(String message, String currentDate, String currentTime, String de, boolean vu) {
        this.message = message;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.de = de;
        this.vu = vu;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public boolean getVu() { return vu; }

    public void setVu(boolean vu) { this.vu = vu; }

    @Override
    public int compareTo(Message lastMessage) {
        SimpleDateFormat lastMesDate = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
        SimpleDateFormat mesDate = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
        Calendar calLastMesDate = Calendar.getInstance();
        Calendar calMesDate = Calendar.getInstance();
        try {
            calLastMesDate.setTime(lastMesDate.parse(lastMessage.getCurrentDate()+" "+lastMessage.getCurrentTime()));
            calMesDate.setTime(mesDate.parse(this.getCurrentDate()+" "+this.getCurrentTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calLastMesDate.compareTo(calMesDate);
    }

    public Boolean isToday(String date) {
        SimpleDateFormat mesDate = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calMesDate = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Calendar c = Calendar.getInstance();
        try {
            calMesDate.setTime(mesDate.parse(this.getCurrentDate()));
            c.setTime(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calMesDate.equals(c);
    }


}
