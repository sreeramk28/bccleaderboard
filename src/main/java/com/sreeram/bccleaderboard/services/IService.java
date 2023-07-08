package com.sreeram.bccleaderboard.services;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;

public interface IService {

    IClient getClient();

    LeaderboardResponse getLeaderboard();

}
