package com.sreeram.bccleaderboard.responses;

import com.sreeram.bccleaderboard.models.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardResponse {
  
  private List<PlayerResponse> topPlayers;

  public void setTopPlayers(Map<String, Player> players) {
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
  
  public List<PlayerResponse> getTopPlayers() {
    return topPlayers;
  }
}
