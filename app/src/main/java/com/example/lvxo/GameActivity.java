package com.example.lvxo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private GridLayout gameBoard;
    private TextView tvRoundInfo, tvScorePlayer, tvScoreMachine, tvScoreDraw, tvCurrentTurn;
    private MaterialButton btnReset;
    private ImageView btnBack;

    private Button[][] buttons = new Button[3][3];
    private String playerSymbol = "X";
    private String machineSymbol = "O";
    private String currentPlayer;
    private int currentRound = 1;
    private int totalRounds;
    private int scorePlayer = 0;
    private int scoreMachine = 0;
    private int scoreDraw = 0;
    private int roundMoves = 0;
    private boolean gameActive = true;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        totalRounds = getIntent().getIntExtra("TOTAL_ROUNDS", 5);
        playerSymbol = getIntent().getStringExtra("PLAYER_SYMBOL");
        machineSymbol = playerSymbol.equals("X") ? "O" : "X";
        currentPlayer = playerSymbol; // Player always starts

        initViews();
        createGameBoard();
        updateUI();
        setupListeners();
    }

    private void initViews() {
        gameBoard = findViewById(R.id.gameBoard);
        tvRoundInfo = findViewById(R.id.tvRoundInfo);
        tvScorePlayer = findViewById(R.id.tvScoreX);
        tvScoreMachine = findViewById(R.id.tvScoreO);
        tvScoreDraw = findViewById(R.id.tvScoreDraw);
        tvCurrentTurn = findViewById(R.id.tvCurrentTurn);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);
    }

    private void createGameBoard() {
        int buttonSize = (getResources().getDisplayMetrics().widthPixels - 160) / 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(this);
                button.setTextSize(32);
                button.setTextColor(Color.WHITE);
                button.setBackgroundColor(getResources().getColor(R.color.card_bg));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSize;
                params.height = buttonSize;
                params.setMargins(4, 4, 4, 4);
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                button.setLayoutParams(params);

                final int row = i;
                final int col = j;
                button.setOnClickListener(v -> onCellClick(row, col));

                buttons[i][j] = button;
                gameBoard.addView(button);
            }
        }
    }

    private void onCellClick(int row, int col) {
        if (!gameActive || !buttons[row][col].getText().toString().isEmpty() || !currentPlayer.equals(playerSymbol)) {
            return;
        }

        makeMove(row, col, playerSymbol);

        if (gameActive) {
            currentPlayer = machineSymbol;
            updateUI();
            // Machine plays after a short delay
            new Handler().postDelayed(() -> machineMove(), 500);
        }
    }

    private void makeMove(int row, int col, String symbol) {
        buttons[row][col].setText(symbol);
        int color = symbol.equals(playerSymbol) ?
                getResources().getColor(R.color.player_x) :
                getResources().getColor(R.color.player_o);
        buttons[row][col].setTextColor(color);

        roundMoves++;

        if (checkWinner()) {
            gameActive = false;
            if (symbol.equals(playerSymbol)) {
                scorePlayer++;
            } else {
                scoreMachine++;
            }
            updateUI();
            String winnerText = symbol.equals(playerSymbol) ? "Vous avez gagné!" : "La machine a gagné!";
            showRoundResult(winnerText);
        } else if (roundMoves == 9) {
            gameActive = false;
            scoreDraw++;
            updateUI();
            showRoundResult("Partie nulle!");
        }
    }

    private void machineMove() {
        if (!gameActive) return;

        // Simple AI: Try to win, then block, then random
        int[] move = findBestMove();

        if (move != null) {
            makeMove(move[0], move[1], machineSymbol);
            if (gameActive) {
                currentPlayer = playerSymbol;
                updateUI();
            }
        }
    }

    private int[] findBestMove() {
        // 1. Try to win
        int[] winMove = findWinningMove(machineSymbol);
        if (winMove != null) return winMove;

        // 2. Block player from winning
        int[] blockMove = findWinningMove(playerSymbol);
        if (blockMove != null) return blockMove;

        // 3. Take center if available
        if (buttons[1][1].getText().toString().isEmpty()) {
            return new int[]{1, 1};
        }

        // 4. Take a corner
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        ArrayList<int[]> availableCorners = new ArrayList<>();
        for (int[] corner : corners) {
            if (buttons[corner[0]][corner[1]].getText().toString().isEmpty()) {
                availableCorners.add(corner);
            }
        }
        if (!availableCorners.isEmpty()) {
            return availableCorners.get(random.nextInt(availableCorners.size()));
        }

        // 5. Take any available space
        ArrayList<int[]> availableMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().isEmpty()) {
                    availableMoves.add(new int[]{i, j});
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            return availableMoves.get(random.nextInt(availableMoves.size()));
        }

        return null;
    }

    private int[] findWinningMove(String symbol) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (canWinLine(buttons[i][0], buttons[i][1], buttons[i][2], symbol)) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().toString().isEmpty()) {
                        return new int[]{i, j};
                    }
                }
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (canWinLine(buttons[0][i], buttons[1][i], buttons[2][i], symbol)) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[j][i].getText().toString().isEmpty()) {
                        return new int[]{j, i};
                    }
                }
            }
        }

        // Check diagonals
        if (canWinLine(buttons[0][0], buttons[1][1], buttons[2][2], symbol)) {
            for (int i = 0; i < 3; i++) {
                if (buttons[i][i].getText().toString().isEmpty()) {
                    return new int[]{i, i};
                }
            }
        }

        if (canWinLine(buttons[0][2], buttons[1][1], buttons[2][0], symbol)) {
            if (buttons[0][2].getText().toString().isEmpty()) return new int[]{0, 2};
            if (buttons[1][1].getText().toString().isEmpty()) return new int[]{1, 1};
            if (buttons[2][0].getText().toString().isEmpty()) return new int[]{2, 0};
        }

        return null;
    }

    private boolean canWinLine(Button b1, Button b2, Button b3, String symbol) {
        String s1 = b1.getText().toString();
        String s2 = b2.getText().toString();
        String s3 = b3.getText().toString();

        int symbolCount = 0;
        int emptyCount = 0;

        if (s1.equals(symbol)) symbolCount++;
        else if (s1.isEmpty()) emptyCount++;

        if (s2.equals(symbol)) symbolCount++;
        else if (s2.isEmpty()) emptyCount++;

        if (s3.equals(symbol)) symbolCount++;
        else if (s3.isEmpty()) emptyCount++;

        return symbolCount == 2 && emptyCount == 1;
    }

    private boolean checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i][0], buttons[i][1], buttons[i][2])) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[0][i], buttons[1][i], buttons[2][i])) {
                return true;
            }
        }

        // Check diagonals
        if (checkLine(buttons[0][0], buttons[1][1], buttons[2][2])) {
            return true;
        }
        if (checkLine(buttons[0][2], buttons[1][1], buttons[2][0])) {
            return true;
        }

        return false;
    }

    private boolean checkLine(Button b1, Button b2, Button b3) {
        String s1 = b1.getText().toString();
        String s2 = b2.getText().toString();
        String s3 = b3.getText().toString();
        return !s1.isEmpty() && s1.equals(s2) && s2.equals(s3);
    }

    private void showRoundResult(String result) {
        new Handler().postDelayed(() -> {
            if (currentRound < totalRounds) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                currentRound++;
                resetBoard();
                updateUI();
            } else {
                showTournamentResult();
            }
        }, 1000);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setTextColor(Color.WHITE);
            }
        }
        roundMoves = 0;
        gameActive = true;
        currentPlayer = playerSymbol; // Player always starts new round
    }

    private void updateUI() {
        tvRoundInfo.setText("Partie " + currentRound + " / " + totalRounds);
        tvScorePlayer.setText(String.valueOf(scorePlayer));
        tvScoreMachine.setText(String.valueOf(scoreMachine));
        tvScoreDraw.setText(String.valueOf(scoreDraw));

        if (currentPlayer.equals(playerSymbol)) {
            tvCurrentTurn.setText("Votre tour");
            tvCurrentTurn.setTextColor(getResources().getColor(R.color.player_x));
        } else {
            tvCurrentTurn.setText("Tour de la Machine");
            tvCurrentTurn.setTextColor(getResources().getColor(R.color.player_o));
        }
    }

    private void showTournamentResult() {
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra("SCORE_PLAYER", scorePlayer);
        intent.putExtra("SCORE_MACHINE", scoreMachine);
        intent.putExtra("SCORE_DRAW", scoreDraw);
        intent.putExtra("TOTAL_ROUNDS", totalRounds);
        intent.putExtra("PLAYER_SYMBOL", playerSymbol);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupListeners() {
        btnReset.setOnClickListener(v -> {
            resetBoard();
            updateUI();
        });

        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle("Quitter?")
                    .setMessage("Voulez-vous vraiment quitter? Le tournoi ne sera pas sauvegardé.")
                    .setPositiveButton("Oui", (dialog, which) -> finish())
                    .setNegativeButton("Non", null)
                    .show();
        });
    }
}