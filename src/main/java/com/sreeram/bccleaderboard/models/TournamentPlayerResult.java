package com.sreeram.bccleaderboard.models;

import lombok.Data;

@Data
public class TournamentPlayerResult {
    private final String username;
    private final String tmtId;
    private Float points;
    private Float tiebreak;
}
