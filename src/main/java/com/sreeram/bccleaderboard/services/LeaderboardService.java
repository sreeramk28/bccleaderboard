package com.sreeram.bccleaderboard.services;

import org.springframework.stereotype.Component;

import com.sreeram.bccleaderboard.models.Player;
import com.sreeram.bccleaderboard.models.TopPlayersResponse;

import chariot.Client;
import chariot.model.Entries;
import chariot.model.Swiss;
import chariot.model.SwissResult;
import chariot.model.Many;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class LeaderboardService {
  private static final Client client = Client.basic();

  public TopPlayersResponse createTopPlayersResponse() {
    TopPlayersResponse response = new TopPlayersResponse();
    List<Player> topPlayers = new ArrayList<>();
    HashMap<String, Integer> results = getTopPlayersOfTheMonth();
    for (String username : results.keySet()) {
      Player player = new Player();
      player.setUsername(username);
      player.setMonthScore(results.get(username).toString());
      topPlayers.add(player);
    }
    response.setTopPlayers(topPlayers);
    return response;
  }

  public HashMap<String, Integer> getTopPlayersOfTheMonth() {
    HashMap<String, Integer> results = new HashMap<>();
    List<String> tournamentIds = getCurrentMonthFinishedTournamentIds();
    for (String id : tournamentIds) {
      List<String> top10 = getTopTenFromTournament(id);
      int listSize = top10.size();
      int score = 0;
      for (String player: top10) {
        int playerScore = listSize - score;
        if (results.containsKey(player)) {
          results.put(player, results.get(player) + playerScore);
        }
        else {
          results.put(player, playerScore);
        }
        score++;
      }
    }
    return results;
  }
  
  private List<String> getTopTenFromTournament(String id) {
    Many<SwissResult> topTen = 
      client.tournaments().resultsBySwissId(id, params -> params.max(10));
    if (topTen instanceof Entries<SwissResult> entry) {
      return topTen.stream().map(player -> player.username()).toList();
    }
    else {
      System.out.println("Error fetching top 10 from tournament: " + topTen);
      return new ArrayList<String>();
    }
  }
  
  private List<String> getCurrentMonthFinishedTournamentIds() {
    Many<Swiss> lastFiveSwissTournaments = 
      client.teams().swissByTeamId("bangalore-chess-club", 5);
    
    if (lastFiveSwissTournaments instanceof Entries<Swiss> entries) {
      List<Swiss> tmtEntries = entries.stream().toList();
      if (tmtEntries.isEmpty()) {
        return new ArrayList<String>();
      }

      int currentMonth = 
        OffsetDateTime.parse(tmtEntries.stream().findFirst().get().startsAt()).getMonthValue();
      
      List<String> currentMonthFinishedTmtIds = 
        tmtEntries.stream().filter(t -> {
          return 
            t.status().equals("finished") && 
            OffsetDateTime.parse(t.startsAt()).getMonthValue() == currentMonth;
        })
        .map(t -> t.id()).toList();
      return currentMonthFinishedTmtIds;
    }
    else {
      System.out.println("Error fetching last five swiss tournaments: " + lastFiveSwissTournaments);
      return new ArrayList<String>();
    }
  }

}
