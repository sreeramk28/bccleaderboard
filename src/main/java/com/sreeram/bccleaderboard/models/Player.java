package com.sreeram.bccleaderboard.models;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Player {
  private final String username;

  // I feel combining the below tournament related details together would be good.
  // For now, I am keeping them as separate fields.
  /**
   * key : value pairs
   * tournament id : points scored in the tournament
   */
  private Map<String, Float> tmtPoints;

  /**
   * key : value pairs
   * tournament id : tie-break score assigned in the tournament
   */
  private Map<String, Float> tiebreakScore;

  /**
   * key : value pairs
   * tournament id : club score assigned in the tournament
   */
  private Map<String, Integer> clubScore;

  public Player(String username) {
    this.username = username;
    tmtPoints = new HashMap<>();
    tiebreakScore = new HashMap<>();
    clubScore = new HashMap<>();
  }

  public void addTournamentResults(TournamentPlayerResult result, Integer clubScoreForTmt) {
    tmtPoints.put(result.getTmtId(), result.getPoints());
    tiebreakScore.put(result.getTmtId(), result.getTiebreak());
    clubScore.put(result.getTmtId(), clubScoreForTmt);
  }

  /**
   * Assumption - We are currently loading the tournaments that happened in the current month only
   * @return the sum of clubScores of all the tournaments participated by the player
   */
  public Integer getClubScoreAggregate() {
    Integer aggregateClubScore = 0;
    for(Map.Entry<String, Integer> entry : clubScore.entrySet()) {
      Integer tmtClubScore = entry.getValue();
      aggregateClubScore += tmtClubScore;
    }
    return aggregateClubScore;
  }

}
