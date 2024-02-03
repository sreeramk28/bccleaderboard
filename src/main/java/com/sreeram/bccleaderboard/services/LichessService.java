package com.sreeram.bccleaderboard.services;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.models.Player;
import com.sreeram.bccleaderboard.models.PlayerTournamentOutcome;
import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class LichessService implements IService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LichessService.class);

  @Autowired
  private IClient lichessClient;

  @Value("${lichess.club.name}")
  private String clubName;

  @Value("${lichess.club.query-tournament-count}")
  private int tournamentCount;


  @Override
  public LeaderboardResponse getLeaderboard() {
    Map<String, List<PlayerTournamentOutcome>> metrics = new HashMap<>();
    List<Tournament> tournaments = getCurrentMonthFinishedTournaments();
    tournaments.forEach(t -> {
      computeAndUpdatePlayerLevelMetrics(metrics, getTournamentTopTenResults(t));
    });
    List<Player> players = mapMetricsToPlayerData(metrics);
    return new LeaderboardResponse(players);
  }

  @Override
  public LeaderboardResponse getLeaderboardFromTournamentURLs(List<String> urls) {
    return null;
  }


  private void computeAndUpdatePlayerLevelMetrics(Map<String, List<PlayerTournamentOutcome>> metrics, List<TournamentPlayerResult> topTenResults) {
    TournamentPlayerResult previousPlayerResult = null;
    int previousClubScore = 10, rank = 1;
    for (TournamentPlayerResult r: topTenResults) {
      int clubScore = 10 - rank + 1;
      if (previousPlayerResult != null) {
        if (previousPlayerResult.getPoints().equals(r.getPoints()) &&
            previousPlayerResult.getTiebreak().equals(r.getTiebreak())) {
              clubScore = previousClubScore;
            } 
      }
      updatePlayerWiseMetrics(metrics, r, clubScore);
      previousPlayerResult = r;
      previousClubScore = clubScore;
      rank++;
    }
  }

  private List<Player> mapMetricsToPlayerData(Map<String, List<PlayerTournamentOutcome>> metrics) {
    List<Player> players = new ArrayList<>();
    metrics.forEach((username, outcomes) -> {
      Player player = new Player(username);
      player.setOutcomes(outcomes);
      player.computeMonthAggregates();
      players.add(player);
    });
    return players;
  }

  private void updatePlayerWiseMetrics(Map<String, List<PlayerTournamentOutcome>> metrics,
                               TournamentPlayerResult result, Integer clubScore) {
    PlayerTournamentOutcome outcome = new PlayerTournamentOutcome(result.getTmtId(), result.getPoints(), result.getTiebreak(), clubScore);
    metrics.computeIfAbsent(result.getUsername(), u -> new ArrayList<>()).add(outcome);
  }
  
  private List<TournamentPlayerResult> getTournamentTopTenResults(Tournament t) {
    return getClient().getTopTenPlayers(t);
  }

  private List<Tournament> getCurrentMonthFinishedTournaments() {
    List<Tournament> tmtEntries = getClient().getTournaments(clubName, tournamentCount);

    if(tmtEntries != null && !tmtEntries.isEmpty()) {
      int currentMonth = 
        tmtEntries.stream().findFirst().get().getStartsAt().getMonthValue();
      LOGGER.info("Selecting finished tournaments that started in the current month {}", currentMonth);
      List<Tournament> currentMonthFinishedTmtList =
        tmtEntries.stream().filter(t -> {
          return
            t.getStatus().equals("finished") &&
            t.getStartsAt().getMonthValue() == currentMonth;
        })
        .toList();
      LOGGER.debug("Filtered tournament list {}", currentMonthFinishedTmtList);
      return currentMonthFinishedTmtList;
    }
    else {
      LOGGER.error("Error fetching last five swiss tournaments" +
              " club : {}, count : {}, response : {}", clubName, tournamentCount, tmtEntries);
      return new ArrayList<>();
      // Is it better to return null / or empty list here ?
    }
  }

  @Override
  public IClient getClient() {
    return lichessClient;
  }

}
