package com.sreeram.bccleaderboard.models;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Tournament {
    private final String id;
    private OffsetDateTime startsAt;
    private String status;
}
