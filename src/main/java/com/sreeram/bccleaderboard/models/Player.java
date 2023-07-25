package com.sreeram.bccleaderboard.models;

import lombok.Data;

import java.util.List;

@Data
public class Player {
  
  private final String username;

  // outcomes of the player in each tournament he participated in the month
  private List<PlayerTournamentOutcome> outcomes;
  
  private Integer clubScoreMonthAggregate;

  private Float tournamentPointsMonthAggregate;

  private Float tiebreakPointsMonthAggregate;

  public void computeMonthAggregates() {
    clubScoreMonthAggregate = outcomes.stream().map(o -> o.getClubScore()).reduce(0, Integer::sum);
    tournamentPointsMonthAggregate = outcomes.stream().map(o -> o.getPoints()).reduce(0f, Float::sum);
    tiebreakPointsMonthAggregate = outcomes.stream().map(o -> o.getTiebreakPoints()).reduce(0f, Float::sum);
  }

}
