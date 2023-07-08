package com.sreeram.bccleaderboard.client;

import chariot.Client;
import chariot.model.Entries;
import chariot.model.Many;
import chariot.model.Swiss;
import chariot.model.SwissResult;
import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class LichessClient implements IClient {

    private static final Client client = Client.basic();

    @Override
    public List<Tournament> getTournaments() {
        Many<Swiss> lastFiveSwissTournaments =
                client.teams().swissByTeamId("bangalore-chess-club", 5);
        if (lastFiveSwissTournaments instanceof Entries<Swiss> entries) {
            List<Swiss> tmtEntries = entries.stream().toList();
            if (tmtEntries.isEmpty()) {
                return new ArrayList<>();
            }
            return tmtEntries.stream().map(this::convert).toList();
        } else {
            System.out.println("Request to Lichess Server failed");
            return new ArrayList<>();
            // TODO - Explain the reason for failure in the log
        }
    }

    @Override
    public List<TournamentPlayerResult> getTopTenPlayers(Tournament tmt) {
        Many<SwissResult> topTen =
                client.tournaments().resultsBySwissId(tmt.getId(), params -> params.max(10));
        if (topTen instanceof Entries<SwissResult>) {
            return topTen.stream().map(result -> convert(result, tmt)).toList();
        }
        else {
            System.out.println("Error fetching top 10 from tournament: " + topTen);
            return new ArrayList<>();
        }
    }

    private Tournament convert(Swiss lichessTmt) {
        Tournament tournament = new Tournament(lichessTmt.id());
        tournament.setStartsAt(OffsetDateTime.parse(lichessTmt.startsAt()));
        tournament.setStatus(lichessTmt.status());
        return tournament;
    }

    private TournamentPlayerResult convert(SwissResult lichessResult, Tournament tmt) {
        TournamentPlayerResult result = new TournamentPlayerResult(lichessResult.username(), tmt.getId());
        result.setPoints(lichessResult.points());
        result.setTiebreak(lichessResult.tieBreak());
        return result;
    }
}
