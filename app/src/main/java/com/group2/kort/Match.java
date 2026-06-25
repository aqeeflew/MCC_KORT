package com.group2.kort;

public class Match {
    private String matchId;
    private String sport;
    private String date;
    private String time;
    private int neededPlayers;

    public Match(String matchId, String sport, String date, String time, int neededPlayers) {
        this.matchId = matchId;
        this.sport = sport;
        this.date = date;
        this.time = time;
        this.neededPlayers = neededPlayers;
    }

    public String getMatchId() { return matchId; }
    public String getSport() { return sport; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getNeededPlayers() { return neededPlayers; }
}
