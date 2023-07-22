package com.sreeram.bccleaderboard.services;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.models.Player;
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
    List<Tournament> tournaments = getCurrentMonthFinishedTournaments();
    HashMap<String, Player> topPlayers = getTopPlayersOfTheMonth(tournaments);
    return new LeaderboardResponse(topPlayers);
  }

  public HashMap<String, Player> getTopPlayersOfTheMonth(List<Tournament> tournaments) {
    HashMap<String, Player> results = new HashMap<>();
    for (Tournament tmt : tournaments) {
      List<TournamentPlayerResult> top10 = getClient().getTopTenPlayers(tmt);
      if(top10 == null) {
        LOGGER.warn("Top ten players list from tournament {} is null" , tmt);
        continue;
      }
      int listSize = top10.size();
      int score = 0;
      for (TournamentPlayerResult playerResult: top10) {
        String username = playerResult.getUsername();
        Player player = results.getOrDefault(username, new Player(username));
        int clubScore = listSize - score;
        // TODO - Update club score according to the points and tiebreak scored in the tournamentPlayerResult
        player.addTournamentResults(playerResult, clubScore);
        results.put(username, player);
        score++;
      }
    }
    return results;
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
