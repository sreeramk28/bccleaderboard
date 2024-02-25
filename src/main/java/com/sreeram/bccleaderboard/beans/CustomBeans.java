package com.sreeram.bccleaderboard.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.sornerol.chess.pubapi.client.TournamentClient;

@Configuration
public class CustomBeans {
  @Bean
  public TournamentClient tournamentClient() {
    return new TournamentClient();
  }
}
