package com.sreeram.bccleaderboard.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;

import io.github.sornerol.chess.pubapi.client.TournamentClient;
import io.github.sornerol.chess.pubapi.domain.tournament.TournamentPlayer;
import io.github.sornerol.chess.pubapi.domain.tournament.TournamentRoundGroup;
import io.github.sornerol.chess.pubapi.exception.ChessComPubApiException;

// TODO - ChesscomClient
@Component
public class ChesscomClient implements IClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LichessClient.class);

    @Autowired
    private TournamentClient client;

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
        List<Tournament> tournaments = new ArrayList<>(); 
        for (String id: urlIds) {
            try {
                io.github.sornerol.chess.pubapi.domain.tournament.Tournament chessComTournament = client.getTournamentByUrlId(id);
                
                System.out.println("IDD = " + chessComTournament.getUrl());
                System.out.println("Roundss = " + chessComTournament.getSettings().getTotalRounds());
                //return tournaments;
                Tournament t = convert(chessComTournament, id);
                tournaments.add(t);
            }
            catch(IOException e) {
                e.printStackTrace();
                return null;
            }
            catch (ChessComPubApiException e) {
                e.printStackTrace();
                return null;
            }
        }
        return tournaments;
    }

    @Override
    public List<TournamentPlayerResult> getTopTenPlayers(Tournament tmt) {
        try {
            TournamentRoundGroup results = client.getTournamentRoundGroup(tmt.getId(), tmt.getNbRounds(), 1);
            LOGGER.info("Resultss {}", results);
            List<TournamentPlayerResult> allPlayerResults =
                results.getPlayers().stream().map(
                    playerResult -> convert(playerResult, tmt)
                ).toList();
            List<TournamentPlayerResult> mutableAllPlayerResults = new ArrayList<>(allPlayerResults);
            mutableAllPlayerResults.sort((p1, p2) -> {
                if (p1.getPoints().equals(p2.getPoints())) {
                    return Float.compare(p2.getTiebreak(), p1.getTiebreak());
                }
                return Float.compare(p2.getPoints(), p1.getPoints());
            });
            return mutableAllPlayerResults.subList(0, 20);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch (ChessComPubApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TournamentPlayerResult convert(TournamentPlayer playerResult, Tournament tmt) {
        TournamentPlayerResult result = new TournamentPlayerResult(playerResult.getUsername(), tmt.getId());
        LOGGER.debug("Converting Chesscom  player result -> TournamentPlayerResult, tmtId {}, player {}", tmt.getId(), playerResult.getUsername());
        result.setPoints(playerResult.getPoints().floatValue());
        
        // Tie break playerResult.getTiebreak().floatValue() is coming as null. Compute it using buccholz cut 1
        result.setTiebreak(0F);
        return result;
    }

    private Tournament convert(io.github.sornerol.chess.pubapi.domain.tournament.Tournament chessComTournament, String id) {
        LOGGER.debug("Converting Chesscom tournament type -> Tournament, tmtId {}", id);
        Tournament tournament = new Tournament(id);
        tournament.setNbRounds(chessComTournament.getSettings().getTotalRounds());
        return tournament;
    }
}
