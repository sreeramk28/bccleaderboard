package com.sreeram.bccleaderboard.services;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.models.Player;
import com.sreeram.bccleaderboard.models.PlayerActivity;
import com.sreeram.bccleaderboard.models.PlayerTournamentOutcome;
import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.responses.ActivityResponse;
import com.sreeram.bccleaderboard.responses.ArenaLeaderboardResponse;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import com.sreeram.bccleaderboard.utils.CommonUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class LichessService implements IService {
  @Autowired
  private IClient lichessClient;

  @Autowired
  private CommonUtility utility;

  @Override
  public IClient getClient() {
    return lichessClient;
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

  @Override
  public ActivityResponse getActivePlayersFromTournamentURLs(List<String> urls) {
    Map<String, Integer> activity = new HashMap<>();
    urls.forEach(url -> {
        List<String> tDetail = utility.getIdAndTypeFromTournamentUrl(url, "lichess");
        List<String> players = getClient().getPlayersByTournamentId(tDetail.get(0), tDetail.get(1));
        utility.updatePlayerActivity(activity, players);
    });
    List<PlayerActivity> activePlayers = utility.mapActivityToPlayerActivity(activity);
    return new ActivityResponse(activePlayers);
  }

  @Override
  public ArenaLeaderboardResponse getArenaLeaderboardFromTournamentURLS(List<String> urls) {
    List<Tournament> tournaments = getClient().getArenaTournaments(urls);
    Map<String, List<PlayerTournamentOutcome>> metrics = new HashMap<>();
    tournaments.forEach(t -> {
      utility.computeAndUpdatePlayerLevelMetrics(metrics, getClient().getTopTenArenaPlayers(t));
    });
    List<Player> players = utility.mapMetricsToPlayerData(metrics);
    return new ArenaLeaderboardResponse(players);
  }

}
