package com.sreeram.bccleaderboard.client;

import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;

import java.util.List;

public interface IClient {
    List<Tournament> getTournaments(List<String> urls);
    
    List<TournamentPlayerResult> getTopTenPlayers(Tournament tmt);
}
