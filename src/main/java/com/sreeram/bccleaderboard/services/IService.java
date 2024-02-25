package com.sreeram.bccleaderboard.services;

import java.util.List;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;

public interface IService {

    /** LichessClient / ChesscomClient
     * @return the client to use for the service
     */
    IClient getClient();

    /**
     * @return leaderboard response of the last 5 tournaments in BCC
     */
    LeaderboardResponse getLeaderboard();

    LeaderboardResponse getLeaderboardFromTournamentURLs(List<String> urls);

}
