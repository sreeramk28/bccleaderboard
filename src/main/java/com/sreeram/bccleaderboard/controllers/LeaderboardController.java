package com.sreeram.bccleaderboard.controllers;

import com.sreeram.bccleaderboard.responses.LeaderboardResponse;
import com.sreeram.bccleaderboard.services.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardController.class);

  @Autowired
  private IService lichessService;
  
  @CrossOrigin
  @GetMapping("/bestPlayers")
  public ResponseEntity<LeaderboardResponse> getBestPlayers() {
    LOGGER.info("Request received");

    long start = System.currentTimeMillis();
    LeaderboardResponse response = lichessService.getLeaderboard();
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
