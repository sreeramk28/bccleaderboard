package com.sreeram.bccleaderboard.controllers;

import com.sreeram.bccleaderboard.responses.ActivityResponse;
import com.sreeram.bccleaderboard.responses.ArenaLeaderboardResponse;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import com.sreeram.bccleaderboard.services.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class LeaderboardController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardController.class);

  @Autowired
  private IService chesscomService;

  @Autowired
  private IService lichessService;
  
  @CrossOrigin
  @GetMapping("/bestPlayers")
  public ResponseEntity<LeaderboardResponse> getBestPlayers(
    @RequestParam String platform,
    @RequestParam List<String> urls) {
    LOGGER.info("Best players request received");

    long start = System.currentTimeMillis();
    LeaderboardResponse response;
    
    if (urls != null) {
      LOGGER.info("URLs: {}", urls);
    }

    if (platform.equals("lichess")) {
      response = lichessService.getLeaderboardFromTournamentURLs(urls);
    }
    else {
      response = chesscomService.getLeaderboardFromTournamentURLs(urls);
    }
    
    long end = System.currentTimeMillis();
    long duration = end - start;
    LOGGER.info("Response fetched in: {} ms", duration);
    
    if (response == null) {
      return ResponseEntity.internalServerError().build();
    }
    else {
      return ResponseEntity.ok(response);
    }
  }

  @CrossOrigin
  @GetMapping("/activePlayers")
  public ResponseEntity<ActivityResponse> getActivePlayers(
    @RequestParam String platform,
    @RequestParam List<String> urls) {
    LOGGER.info("Active players request received");

    long start = System.currentTimeMillis();
    ActivityResponse response;
    
    if (urls != null) {
      LOGGER.info("URLs: {}", urls);
    }

    if (platform.equals("lichess")) {
      response = lichessService.getActivePlayersFromTournamentURLs(urls);
    }
    else {
      response = chesscomService.getActivePlayersFromTournamentURLs(urls);
    }
    
    long end = System.currentTimeMillis();
    long duration = end - start;
    LOGGER.info("Response fetched in: {} ms", duration);
    
    if (response == null) {
      return ResponseEntity.internalServerError().build();
    }
    else {
      return ResponseEntity.ok(response);
    }
  }

  @CrossOrigin
  @GetMapping("/arenaBestPlayers")
  public ResponseEntity<ArenaLeaderboardResponse> getArenaBestPlayers(
    @RequestParam String platform,
    @RequestParam List<String> urls) {
    LOGGER.info("Arena Best players request received");

    long start = System.currentTimeMillis();
    ArenaLeaderboardResponse response;
    
    if (urls != null) {
      LOGGER.info("URLs: {}", urls);
    }

    if (platform.equals("lichess")) {
      response = lichessService.getArenaLeaderboardFromTournamentURLS(urls);
    }
    else {
      response = chesscomService.getArenaLeaderboardFromTournamentURLS(urls);
    }
    
    long end = System.currentTimeMillis();
    long duration = end - start;
    LOGGER.info("Response fetched in: {} ms", duration);
    
    if (response == null) {
      return ResponseEntity.internalServerError().build();
    }
    else {
      return ResponseEntity.ok(response);
    }
  }
}
