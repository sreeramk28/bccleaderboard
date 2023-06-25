package com.sreeram.bccleaderboard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sreeram.bccleaderboard.models.TopPlayersResponse;
import com.sreeram.bccleaderboard.services.LeaderboardService;

@RestController
public class LeaderboardController {
  @Autowired
  LeaderboardService service;
  
  @CrossOrigin
  @GetMapping("/bestPlayers")
  public ResponseEntity<TopPlayersResponse> getBestPlayers() {
    System.out.println("Request received");

    long start = System.currentTimeMillis();
    TopPlayersResponse response = service.createTopPlayersResponse();
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
