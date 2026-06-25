package com.group2.kort;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MatchmakingApi {

    class MatchResponse {
        public List<MatchData> matches;
    }

    class MatchData {
        public String matchId;
        public String sport;
        public String date;
        public String time;
        public int neededPlayers;
    }

    class JoinRequest {
        public String matchId;
        public String joinUserId;

        public JoinRequest(String matchId, String joinUserId) {
            this.matchId = matchId;
            this.joinUserId = joinUserId;
        }
    }

    class BroadcastRequest {
        public String bookingId;
        public String hostUserId;
        public String sport;
        public String court;
        public int neededPlayers;
        public String date;
        public String time;

        public BroadcastRequest(String bookingId, String hostUserId, String sport, String court, int neededPlayers, String date, String time) {
            this.bookingId = bookingId;
            this.hostUserId = hostUserId;
            this.sport = sport;
            this.court = court;
            this.neededPlayers = neededPlayers;
            this.date = date;
            this.time = time;
        }
    }

    @GET("/api/matchmaking/available")
    Call<MatchResponse> getAvailableMatches();

    @POST("/api/matchmaking/join")
    Call<Void> joinMatch(@Body JoinRequest body);

    @POST("/api/matchmaking/broadcast")
    Call<Void> broadcastMatch(@Body BroadcastRequest body);
}
