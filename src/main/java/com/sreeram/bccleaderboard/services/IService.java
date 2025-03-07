package com.sreeram.bccleaderboard.services;

import java.util.List;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.responses.ActivityResponse;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import com.sreeram.bccleaderboard.responses.ArenaLeaderboardResponse;

public interface IService {

    /** LichessClient / ChesscomClient
     * @return the client to use for the service
     */
    IClient getClient();

    /**
     * @return leaderboard response from the tournament (urls) considered
     */
    LeaderboardResponse getLeaderboardFromTournamentURLs(List<String> urls);

    ActivityResponse getActivePlayersFromTournamentURLs(List<String> urls);

    ArenaLeaderboardResponse getArenaLeaderboardFromTournamentURLS(List<String> urls);
}
