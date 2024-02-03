package com.sreeram.bccleaderboard.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sreeram.bccleaderboard.models.Player;
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
        if (previousPlayerResult.getPoints().equals(r.getPoints()) &&
            previousPlayerResult.getTiebreak().equals(r.getTiebreak())) {
              clubScore = previousClubScore;
            } 
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

  private void updatePlayerWiseMetrics(Map<String, List<PlayerTournamentOutcome>> metrics,
                               TournamentPlayerResult result, Integer clubScore) {
    PlayerTournamentOutcome outcome = new PlayerTournamentOutcome(result.getTmtId(), result.getPoints(), result.getTiebreak(), clubScore);
    metrics.computeIfAbsent(result.getUsername(), u -> new ArrayList<>()).add(outcome);
  }
}
