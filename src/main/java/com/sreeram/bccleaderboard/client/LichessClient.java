package com.sreeram.bccleaderboard.client;

import chariot.Client;
import chariot.model.Entries;
import chariot.model.Many;
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
    private static final Client client = Client.basic();

    private static final String LICHESS = "Lichess";
    @Override
    public List<Tournament> getTournaments(String club, int count) {
        Many<Swiss> lastFiveSwissTournaments =
                client.teams().swissByTeamId(club, count);
        if (lastFiveSwissTournaments instanceof Entries<Swiss> entries) {
            LOGGER.info("{} Request success : Club {}, Count {}, response {}", LICHESS, club, count, lastFiveSwissTournaments);
            List<Swiss> tmtEntries = entries.stream().toList();
            if (tmtEntries.isEmpty()) {
                LOGGER.warn("Empty tournament list");
                return new ArrayList<>();
            }
            return tmtEntries.stream().map(this::convert).toList();
        } else {
            LOGGER.error("{} Request failed : Club {}, Count {}", LICHESS, club, count);
            return null;
        }
    }

    @Override
    public List<TournamentPlayerResult> getTopTenPlayers(Tournament tmt) {
        Many<SwissResult> topTen =
                client.tournaments().resultsBySwissId(tmt.getId(), params -> params.max(10));
        if (topTen instanceof Entries<SwissResult>) {
            LOGGER.info("{} Request success : Tournament {}, response {}", LICHESS, tmt, topTen);
            return topTen.stream().map(result -> convert(result, tmt)).toList();
        }
        else {
            LOGGER.error("{} Request failed : top 10 players - Tournament {}, failure message {}", LICHESS, tmt.getId(), topTen);
            return new ArrayList<>();
        }
    }

    private Tournament convert(Swiss lichessTmt) {
        LOGGER.debug("Converting Swiss -> Tournament, tmtId {}", lichessTmt.id());
        Tournament tournament = new Tournament(lichessTmt.id());
        tournament.setStartsAt(OffsetDateTime.parse(lichessTmt.startsAt()));
        tournament.setStatus(lichessTmt.status());
        return tournament;
    }

    private TournamentPlayerResult convert(SwissResult lichessResult, Tournament tmt) {
        TournamentPlayerResult result = new TournamentPlayerResult(lichessResult.username(), tmt.getId());
        LOGGER.debug("Converting Swiss Result -> TournamentPlayerResult, tmtId {}, player {}", tmt.getId(), lichessResult.username());
        result.setPoints(lichessResult.points());
        result.setTiebreak(lichessResult.tieBreak());
        return result;
    }
}
