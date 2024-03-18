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

@Component
public class ChesscomClient implements IClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChesscomClient.class);
    private static final String CHESSCOM = "Chesscom";

    @Autowired
    private TournamentClient client;

    @Override
    public List<Tournament> getTournaments(List<String> urls) {
        List<String> urlIds = urls.stream().map(url -> {
            int indexOfOblique = url.lastIndexOf("/");
            String urlId = url.substring(indexOfOblique + 1);
            return urlId;
        }).toList();
        
        List<Tournament> tournaments = new ArrayList<>(); 
        for (String id: urlIds) {
            try {
                io.github.sornerol.chess.pubapi.domain.tournament.Tournament chessComTournament = client.getTournamentByUrlId(id);
                Tournament t = convert(chessComTournament, id);
                tournaments.add(t);
            }
            catch(IOException e) {
                LOGGER.error("IOException: Failed to fetch {} tournament ID {}", CHESSCOM, id);
                e.printStackTrace();
                return null;
            }
            catch (ChessComPubApiException e) {
                LOGGER.error("ChessComPubApiException: Failed to fetch {} tournament ID {}", CHESSCOM, id);
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
            return mutableAllPlayerResults.subList(0, Integer.min(20, mutableAllPlayerResults.size()));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch (ChessComPubApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getPlayersByTournamentId(String id, String type) {
        try {
            io.github.sornerol.chess.pubapi.domain.tournament.Tournament chessComTournament = client.getTournamentByUrlId(id);
            return chessComTournament.getPlayers().stream().map(player -> player.getUsername()).toList();
        }
        catch(IOException e) {
            LOGGER.error("IOException: Failed to fetch {} tournament ID {}", CHESSCOM, id);
            e.printStackTrace();
            return null;
        }
        catch (ChessComPubApiException e) {
            LOGGER.error("ChessComPubApiException: Failed to fetch {} tournament ID {}", CHESSCOM, id);
            e.printStackTrace();
            return null;
        }
    }

    private Tournament convert(io.github.sornerol.chess.pubapi.domain.tournament.Tournament chessComTournament, String id) {
        LOGGER.debug("Converting {} tournament type -> Tournament, tmtId {}", CHESSCOM, id);
        Tournament tournament = new Tournament(id);
        tournament.setNbRounds(chessComTournament.getSettings().getTotalRounds());
        return tournament;
    }

    private TournamentPlayerResult convert(TournamentPlayer playerResult, Tournament tmt) {
        TournamentPlayerResult result = new TournamentPlayerResult(playerResult.getUsername(), tmt.getId());
        LOGGER.debug("Converting {} player result -> TournamentPlayerResult, tmtId {}, player {}", CHESSCOM, tmt.getId(), playerResult.getUsername());
        result.setPoints(playerResult.getPoints().floatValue());
        // Tie break playerResult.getTiebreak().floatValue() is coming as null. Compute it using buccholz cut 1
        result.setTiebreak(0F);
        return result;
    }
}
