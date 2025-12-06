package com.example.lvxo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class ResultActivity extends AppCompatActivity {

    private ImageView ivTrophy;
    private TextView tvWinnerTitle, tvWinnerName, tvPlayerLabel, tvPlayerScore, tvMachineScore, tvDrawScore;
    private MaterialButton btnSaveTournament, btnBackHome;

    private int scorePlayer, scoreMachine, scoreDraw, totalRounds;
    private String playerSymbol;
    private String winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get data from intent
        scorePlayer = getIntent().getIntExtra("SCORE_PLAYER", 0);
        scoreMachine = getIntent().getIntExtra("SCORE_MACHINE", 0);
        scoreDraw = getIntent().getIntExtra("SCORE_DRAW", 0);
        totalRounds = getIntent().getIntExtra("TOTAL_ROUNDS", 0);
        playerSymbol = getIntent().getStringExtra("PLAYER_SYMBOL");

        initViews();
        determineWinner();
        displayResults();
        setupListeners();
    }

    private void initViews() {
        ivTrophy = findViewById(R.id.ivTrophy);
        tvWinnerTitle = findViewById(R.id.tvWinnerTitle);
        tvWinnerName = findViewById(R.id.tvWinnerName);
        tvPlayerLabel = findViewById(R.id.tvPlayerLabel);
        tvPlayerScore = findViewById(R.id.tvPlayerScore);
        tvMachineScore = findViewById(R.id.tvMachineScore);
        tvDrawScore = findViewById(R.id.tvDrawScore);
        btnSaveTournament = findViewById(R.id.btnSaveTournament);
        btnBackHome = findViewById(R.id.btnBackHome);
    }

    private void determineWinner() {
        if (scorePlayer > scoreMachine) {
            winner = "Joueur (" + playerSymbol + ")";
        } else if (scoreMachine > scorePlayer) {
            winner = "Machine";
        } else {
            winner = "Égalité";
        }
    }

    private void displayResults() {
        // Set player label with their symbol
        tvPlayerLabel.setText("Joueur (" + playerSymbol + ")");

        // Set scores
        tvPlayerScore.setText(String.valueOf(scorePlayer));
        tvMachineScore.setText(String.valueOf(scoreMachine));
        tvDrawScore.setText(String.valueOf(scoreDraw));

        // Set winner display
        if (winner.equals("Égalité")) {
            tvWinnerTitle.setText("Match Nul!");
            tvWinnerName.setText("ÉGALITÉ");
            tvWinnerName.setTextColor(getResources().getColor(R.color.text_gray));
        } else if (winner.startsWith("Joueur")) {
            tvWinnerTitle.setText("Félicitations!");
            tvWinnerName.setText("VOUS GAGNEZ!");
            tvWinnerName.setTextColor(getResources().getColor(R.color.accent_purple));
        } else {
            tvWinnerTitle.setText("Partie Terminée");
            tvWinnerName.setText("MACHINE GAGNE");
            tvWinnerName.setTextColor(getResources().getColor(R.color.player_o));
        }

        // Color the scores based on who has more
        if (scorePlayer > scoreMachine) {
            tvPlayerScore.setTextColor(getResources().getColor(R.color.accent_purple));
        } else if (scoreMachine > scorePlayer) {
            tvMachineScore.setTextColor(getResources().getColor(R.color.accent_purple));
        }
    }

    private void setupListeners() {
        btnSaveTournament.setOnClickListener(v -> {
            saveTournament();
            goHome();
        });

        btnBackHome.setOnClickListener(v -> goHome());
    }

    private void saveTournament() {
        try {
            TournamentData data = new TournamentData(scorePlayer, scoreMachine, scoreDraw, totalRounds, winner, playerSymbol);
            FileOutputStream fos = openFileOutput("tournament_data.dat", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
            Toast.makeText(this, "Tournoi sauvegardé avec succès!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private void goHome() {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        goHome();
    }
}