package com.sreeram.bccleaderboard.responses;

import com.sreeram.bccleaderboard.models.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class LeaderboardResponse {
  
  private List<PlayerResponse> topPlayers;

  /**
   * prepares Leaderboard response
   * @param players map containing the player username and the aggregate clubscore over
   *                all tournaments started and finished current month
   */
  public LeaderboardResponse(Map<String, Player> players) {
    if(players == null) {
      System.out.println("Players is null");
      return;
    }
    topPlayers = new ArrayList<>();
    for(Map.Entry<String, Player> entry : players.entrySet()) {
      Player player = entry.getValue();
      topPlayers.add(new PlayerResponse(entry.getKey(), player.getClubScoreAggregate()));
    }
    topPlayers.sort(((o1, o2) -> o2.getMonthScore() - o1.getMonthScore()));
  }

}
