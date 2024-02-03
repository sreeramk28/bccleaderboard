package com.sreeram.bccleaderboard.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sreeram.bccleaderboard.client.IClient;
import com.sreeram.bccleaderboard.responses.LeaderboardResponse;

@Service
public class ChesscomService implements IService {
    @Autowired
    private IClient chesscomClient;

    @Override
    public IClient getClient() {
        return chesscomClient;
    }

    @Override
    public LeaderboardResponse getLeaderboard() {
        return null;
    }

    @Override
    public LeaderboardResponse getLeaderboardFromTournamentURLs(List<String> urls) {
        testChessComLib(urls);
        return null;
    }

    private void testChessComLib(List<String> urls) {
        var lt = getClient().getTournaments(urls);
    }
}
