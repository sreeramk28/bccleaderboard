package com.sreeram.bccleaderboard.models;

import lombok.Data;

@Data
public class PlayerTournamentOutcome {
  private final String tournamentId;
  private Float points;
  private Float tiebreakPoints;
  private Integer clubScore;
  
  public PlayerTournamentOutcome(final String tournamentId, Float points, Float tiebreakPoints, Integer clubScore) {
    this.tournamentId = tournamentId;
    this.points = points;
    this.tiebreakPoints = tiebreakPoints;
    this.clubScore = clubScore;
  }
}
