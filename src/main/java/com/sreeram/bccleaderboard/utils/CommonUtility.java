package com.sreeram.bccleaderboard.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sreeram.bccleaderboard.models.Player;
import com.sreeram.bccleaderboard.models.PlayerActivity;
import com.sreeram.bccleaderboard.models.PlayerTournamentOutcome;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;

@Component
public class CommonUtility {
  
  public void computeAndUpdatePlayerLevelMetrics(Map<String, List<PlayerTournamentOutcome>> metrics, List<TournamentPlayerResult> topTenResults) {
    TournamentPlayerResult previousPlayerResult = null;
    int previousClubScore = 10, rank = 1;
    for (TournamentPlayerResult r: topTenResults) {
      int clubScore = 10 - rank + 1;
      if (previousPlayerResult != null) {
        if (previousPlayerResult.getPoints().equals(r.getPoints())) {
              clubScore = previousClubScore;
            } 
      }
      if (clubScore <= 0) {
        break;
      }
      updatePlayerWiseMetrics(metrics, r, clubScore);
      previousPlayerResult = r;
      previousClubScore = clubScore;
      rank++;
    }
  }

  public List<Player> mapMetricsToPlayerData(Map<String, List<PlayerTournamentOutcome>> metrics) {
    List<Player> players = new ArrayList<>();
    metrics.forEach((username, outcomes) -> {
      Player player = new Player(username);
      player.setOutcomes(outcomes);
      player.computeMonthAggregates();
      players.add(player);
    });
    return players;
  }

  public List<String> getIdAndTypeFromTournamentUrl(String url, String platform) {
    int indexOfOblique = url.lastIndexOf("/");
    String id = url.substring(indexOfOblique + 1);
    String type;
    if (platform.equals("lichess")) {
      type = url.contains("/swiss") ? "swiss" : "arena";
    }
    else {
      type = url.contains("/arena") ? "arena" : "swiss";
    }
    return new ArrayList<String>(Arrays.asList(id, type));
  }

  public void updatePlayerActivity(Map<String, Integer> activity, List<String> players) {
    players.forEach(p -> activity.compute(p, (k, v) -> (v == null) ? 1 : v + 1));
  }

  public List<PlayerActivity> mapActivityToPlayerActivity(Map<String, Integer> activity) {
    List<PlayerActivity> activePlayers = new ArrayList<>();
    activity.forEach((k, v) -> {
      PlayerActivity playerActivity = new PlayerActivity();
      playerActivity.setUsername(k);
      playerActivity.setNbTournamentsPlayed(v);
      activePlayers.add(playerActivity);
    });
    return activePlayers;
  }

  private void updatePlayerWiseMetrics(Map<String, List<PlayerTournamentOutcome>> metrics,
                               TournamentPlayerResult result, Integer clubScore) {
    PlayerTournamentOutcome outcome = new PlayerTournamentOutcome(result.getTmtId(), result.getPoints(), result.getTiebreak(), clubScore);
    metrics.computeIfAbsent(result.getUsername(), u -> new ArrayList<>()).add(outcome);
  }
}
