package com.sreeram.bccleaderboard.responses;


import java.util.List;

import com.sreeram.bccleaderboard.models.PlayerActivity;

import lombok.Getter;

@Getter
public class ActivityResponse {
    private List<PlayerActivity> activePlayers;
    
    public ActivityResponse(List<PlayerActivity> activePlayersList) {
        activePlayers = activePlayersList;
        activePlayers.sort((p1, p2) -> {
            return Integer.compare(p2.getNbTournamentsPlayed(), p1.getNbTournamentsPlayed());
        });
    }
}
