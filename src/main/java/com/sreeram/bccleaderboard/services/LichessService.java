package com.sreeram.bccleaderboard.services;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.models.Player;
import com.sreeram.bccleaderboard.models.Tournament;
import com.sreeram.bccleaderboard.models.TournamentPlayerResult;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LichessService implements IService {

  @Autowired
  private IClient lichessClient;

  @Override
  public LeaderboardResponse getLeaderboard() {
    LeaderboardResponse response = new LeaderboardResponse();
    List<Tournament> tournaments = getCurrentMonthFinishedTournaments();
    HashMap<String, Player> topPlayers = getTopPlayersOfTheMonth(tournaments);
    response.setTopPlayers(topPlayers);
    return response;
  }

  public HashMap<String, Player> getTopPlayersOfTheMonth(List<Tournament> tournaments) {
    HashMap<String, Player> results = new HashMap<>();
    for (Tournament tmt : tournaments) {
      List<TournamentPlayerResult> top10 = getTopTenFromTournament(tmt);
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
  
  private List<TournamentPlayerResult> getTopTenFromTournament(Tournament tmt) {
    List<TournamentPlayerResult> topTen = getClient().getTopTenPlayers(tmt);
    return topTen;
  }
  
  private List<Tournament> getCurrentMonthFinishedTournaments() {
    List<Tournament> tmtEntries = getClient().getTournaments();

    if(tmtEntries != null && !tmtEntries.isEmpty()) {
      int currentMonth = 
        tmtEntries.stream().findFirst().get().getStartsAt().getMonthValue();
      
      List<Tournament> currentMonthFinishedTmtIds =
        tmtEntries.stream().filter(t -> {
          return 
            t.getStatus().equals("finished") &&
            t.getStartsAt().getMonthValue() == currentMonth;
        })
        .toList();
      return currentMonthFinishedTmtIds;
    }
    else {
      System.out.println("Error fetching last five swiss tournaments: " + tmtEntries);
      return new ArrayList<>();
    }
  }

  @Override
  public IClient getClient() {
    return lichessClient;
  }

}
