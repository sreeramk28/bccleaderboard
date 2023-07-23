package com.sreeram.bccleaderboard.responses;

import com.sreeram.bccleaderboard.models.Player;
import lombok.Getter;

import java.util.List;

@Getter
public class LeaderboardResponse {
  
  private List<Player> topPlayers;

  /**
   * prepares Leaderboard response
   * @param players list of player objects
   */
  public LeaderboardResponse(List<Player> players) {
    topPlayers = players;
    topPlayers.sort((p1, p2) -> {
      if (p1.getClubScoreMonthAggregate().equals(p2.getClubScoreMonthAggregate())) {
        if (p1.getTournamentPointsMonthAggregate().equals(p2.getTiebreakPointsMonthAggregate())) {
          return Float.compare(p2.getTiebreakPointsMonthAggregate(), p1.getTiebreakPointsMonthAggregate());
        }
        return Float.compare(p2.getTournamentPointsMonthAggregate(), p1.getTournamentPointsMonthAggregate());
      }
      return Integer.compare(p2.getClubScoreMonthAggregate(), p1.getClubScoreMonthAggregate());
    });
  }

}
