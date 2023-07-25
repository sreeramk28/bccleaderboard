package com.sreeram.bccleaderboard.models;

import lombok.Data;

/*
 * The data fetched from an external API client is transformed 
 * to this business-useful format
 */
@Data
public class TournamentPlayerResult {
    private final String username;
    private final String tmtId;
    private Float points;
    private Float tiebreak;
}
