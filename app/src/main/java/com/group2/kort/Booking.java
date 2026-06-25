package com.group2.kort;

public class Booking {
    private String sport;
    private String court;
    private String date;
    private String time;

    public Booking(String sport, String court, String date, String time) {
        this.sport = sport;
        this.court = court;
        this.date = date;
        this.time = time;
    }

    public String getSport() { return sport; }
    public String getCourt() { return court; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
