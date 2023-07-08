package com.sreeram.bccleaderboard.responses;

import lombok.Data;

@Data
public class PlayerResponse {
    private final String username;
    private final Integer monthScore;
}
