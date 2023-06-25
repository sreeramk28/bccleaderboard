package com.sreeram.bccleaderboard.models;

import java.util.List;

public class TopPlayersResponse {
  
  private List<Player> topPlayers;

  public void setTopPlayers(List<Player> topPlayers) {
    this.topPlayers = topPlayers;
  }
  
  public List<Player> getTopPlayers() {
    return topPlayers;
  }
}
