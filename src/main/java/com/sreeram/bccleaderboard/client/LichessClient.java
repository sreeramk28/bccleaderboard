package com.sreeram.bccleaderboard.client;

import chariot.Client;
import chariot.model.Arena;
import chariot.model.ArenaResult;
import chariot.model.Entries;
import chariot.model.Many;
import chariot.model.One;
import chariot.model.Swiss;
import chariot.model.SwissResult;
import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class LichessClient implements IClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LichessClient.class);
    private static final String LICHESS = "Lichess";
    
    private static final Client client = Client.basic();

    @Override
    public List<Tournament> getTournaments(List<String> urls) {
        List<String> urlIds = urls.stream().map(url -> {
            int indexOfOblique = url.lastIndexOf("/");
            String urlId = url.substring(indexOfOblique + 1);
            return urlId;
        }).toList();

        List<Tournament> tournaments = new ArrayList<>();
        for (String id: urlIds) {
            One<Swiss> lichessTournament = client.tournaments().swissById(id);
            if (lichessTournament.isPresent()) {
                Tournament t = convert(lichessTournament.get());
                tournaments.add(t);
            } else {
                LOGGER.error("Failed to fetch {} tournament ID {}", LICHESS, id);
                return null;
            }
        }
        return tournaments;
    }

    @Override
    public List<TournamentPlayerResult> getTopTenPlayers(Tournament tmt) {
        Many<SwissResult> topTen = client.tournaments().resultsBySwissId(tmt.getId(), params -> params.max(20));
        if (topTen instanceof Entries<SwissResult>) {
            List<TournamentPlayerResult> topTenList = topTen.stream().map(result -> convert(result, tmt)).toList();
            LOGGER.info("{} Request success : Tournament {}, response {}", LICHESS, tmt, topTenList);
            return topTenList;
        } else {
            LOGGER.error("{} Request failed : top 10 players - Tournament {}, failure message {}", LICHESS, tmt.getId(),
                    topTen);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tournament> getArenaTournaments(List<String> urls) {
        List<String> urlIds = urls.stream().map(url -> {
            int indexOfOblique = url.lastIndexOf("/");
            String urlId = url.substring(indexOfOblique + 1);
            return urlId;
        }).toList();

        List<Tournament> tournaments = new ArrayList<>();
        for (String id: urlIds) {
            One<Arena> lichessArenaTournament = client.tournaments().arenaById(id);
            if (lichessArenaTournament.isPresent()) {
                Tournament t = convert(lichessArenaTournament.get());
                tournaments.add(t);
            } else {
                LOGGER.error("Failed to fetch {} tournament ID {}", LICHESS, id);
                return null;
            }
        }
        return tournaments;
    }

    @Override
    public List<TournamentPlayerResult> getTopTenArenaPlayers(Tournament tmt) {
        Many<ArenaResult> topTen = client.tournaments().resultsByArenaId(tmt.getId(), params -> params.max(20));
        if (topTen instanceof Entries<ArenaResult>) {
            List<TournamentPlayerResult> topTenList = topTen.stream().map(result -> convert(result, tmt)).toList();
            LOGGER.info("{} Request success : Tournament {}, response {}", LICHESS, tmt, topTenList);
            return topTenList;
        } else {
            LOGGER.error("{} Request failed : top 10 players - Tournament {}, failure message {}", LICHESS, tmt.getId(),
                    topTen);
            return new ArrayList<>();
        }
    }


    @Override
    public List<String> getPlayersByTournamentId(String id, String type) {
        if (type.equals("arena")) {
            Many<ArenaResult> standings = client.tournaments().resultsByArenaId(id, params -> params.max(500));
            if (standings instanceof Entries<ArenaResult>) {
                return standings.stream().map(player -> player.username()).toList();
            }
            else {
                LOGGER.error("{} Failed to get tournament standings - Tournament {}, failure message {}", LICHESS, id, standings);
                return null;
            }
        }
        else {
            Many<SwissResult> standings = client.tournaments().resultsBySwissId(id, params -> params.max(500));
            if (standings instanceof Entries<SwissResult>) {
                return standings.stream().map(player -> player.username()).toList();
            }
            else {
                LOGGER.error("{} Failed to get tournament standings - Tournament {}, failure message {}", LICHESS, id, standings);
                return null;
            }
        }
    }

    private Tournament convert(Swiss lichessTmt) {
        LOGGER.debug("Converting {} Swiss -> Tournament, tmtId {}", LICHESS, lichessTmt.id());
        Tournament tournament = new Tournament(lichessTmt.id());
        tournament.setStartsAt(OffsetDateTime.parse(lichessTmt.startsAt()));
        tournament.setStatus(lichessTmt.status());
        return tournament;
    }

    private Tournament convert(Arena lichessTmt) {
        LOGGER.debug("Converting {} Arena -> Tournament, tmtId {}", LICHESS, lichessTmt.id());
        Tournament tournament = new Tournament(lichessTmt.id());
        tournament.setStartsAt(OffsetDateTime.parse(lichessTmt.startsAt()));
        tournament.setStatus("finished"); // Revisit
        return tournament;
    }

    private TournamentPlayerResult convert(SwissResult lichessResult, Tournament tmt) {
        TournamentPlayerResult result = new TournamentPlayerResult(lichessResult.username(), tmt.getId());
        LOGGER.debug("Converting {} Swiss Result -> TournamentPlayerResult, tmtId {}, player {}", LICHESS, tmt.getId(), lichessResult.username());
        result.setPoints(lichessResult.points());
        result.setTiebreak(lichessResult.tieBreak());
        return result;
    }

    private TournamentPlayerResult convert(ArenaResult lichessArenaResult, Tournament tmt) {
        TournamentPlayerResult result = new TournamentPlayerResult(lichessArenaResult.username(), tmt.getId());
        LOGGER.debug("Converting {} Arena Result -> TournamentPlayerResult, tmtId {}, player {}", LICHESS, tmt.getId(), lichessArenaResult.username());
        result.setPoints(lichessArenaResult.score() * 1.0F);
        result.setTiebreak(lichessArenaResult.performance() != null ? lichessArenaResult.performance() * 1.0F : 0.0F);
        return result;
    }
}
