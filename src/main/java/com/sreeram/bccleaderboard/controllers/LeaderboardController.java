package com.sreeram.bccleaderboard.controllers;

import com.sreeram.bccleaderboard.services.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sreeram.bccleaderboard.responses.LeaderboardResponse;

@RestController
public class LeaderboardController {
  @Autowired
  private IService lichessService;
  
  @CrossOrigin
  @GetMapping("/bestPlayers")
  public ResponseEntity<LeaderboardResponse> getBestPlayers() {
    System.out.println("Request received");

    long start = System.currentTimeMillis();
    LeaderboardResponse response = lichessService.getLeaderboard();
    long end = System.currentTimeMillis();
    
    System.out.println("Response fetched in: " + (end - start) + " ms");
    
    if (response == null) {
      return ResponseEntity.internalServerError().build();
    }
    else {
      return ResponseEntity.ok(response);
    }
  }
}
