package com.sreeram.bccleaderboard.client;

import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;

import io.github.sornerol.chess.pubapi.client.TournamentClient;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

// TODO - ChesscomClient
@Component
public class ChesscomClient implements IClient {
    @Override
    public List<Tournament> getTournaments(String club, int count) {
        return null;
    }

    @Override
    public List<Tournament> getTournaments(List<String> urls) {
        List<String> urlIds = urls.stream().map(url -> {
            int indexOfOblique = url.lastIndexOf("/");
            System.out.println("Indx: "+ indexOfOblique);
            String urlId = url.substring(indexOfOblique + 1);
            return urlId;
        }).toList();
        
        System.out.println("IDs = " + urlIds.toString());
        TournamentClient client = new TournamentClient();
        urlIds.forEach(id -> {
            try {
                io.github.sornerol.chess.pubapi.domain.tournament.Tournament t = client.getTournamentByUrlId(id);
                System.out.println("IDD = " + t.getUrl());
                System.out.println("Roundss = " + t.getSettings().getTotalRounds());
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            catch (ChessComPubApiException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    public List<TournamentPlayerResult> getTopTenPlayers(Tournament tmt) {
        return null;
    }
}
