package com.sreeram.bccleaderboard.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.models.Player;
import com.sreeram.bccleaderboard.models.PlayerTournamentOutcome;
import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import com.sreeram.bccleaderboard.utils.CommonUtility;


@Service
public class ChesscomService implements IService {
    @Autowired
    private IClient chesscomClient;

    @Autowired
    private CommonUtility utility;

    @Override
    public IClient getClient() {
        return chesscomClient;
    }

    @Override
    public LeaderboardResponse getLeaderboard() {
        return null;
    }

    @Override
    public LeaderboardResponse getLeaderboardFromTournamentURLs(List<String> urls) {
        Map<String, List<PlayerTournamentOutcome>> metrics = new HashMap<>();
        List<Tournament> tournaments = getClient().getTournaments(urls);
        tournaments.forEach(t -> {
            utility.computeAndUpdatePlayerLevelMetrics(metrics, getClient().getTopTenPlayers(t));
        });
        List<Player> players = utility.mapMetricsToPlayerData(metrics);
        return new LeaderboardResponse(players);
    }

}
