package com.example.lvxo;

import java.io.Serializable;

public class TournamentData implements Serializable {
    private int scorePlayer;
    private int scoreMachine;
    private int scoreDraw;
    private int totalRounds;
    private String winner;
    private String playerSymbol;

    public TournamentData(int scorePlayer, int scoreMachine, int scoreDraw, int totalRounds, String winner, String playerSymbol) {
        this.scorePlayer = scorePlayer;
        this.scoreMachine = scoreMachine;
        this.scoreDraw = scoreDraw;
        this.totalRounds = totalRounds;
        this.winner = winner;
        this.playerSymbol = playerSymbol;
    }

    public int getScorePlayer() {
        return scorePlayer;
    }

    public int getScoreMachine() {
        return scoreMachine;
    }

    public int getScoreDraw() {
        return scoreDraw;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public String getWinner() {
        return winner;
    }

    public String getPlayerSymbol() {
        return playerSymbol;
    }
}