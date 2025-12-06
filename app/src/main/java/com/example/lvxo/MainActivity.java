package com.example.lvxo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {

    private RadioGroup symbolRadioGroup;
    private RadioButton radioX, radioO;
    private Spinner roundsSpinner;
    private MaterialButton btnPlay, btnRules, btnLastTournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        symbolRadioGroup = findViewById(R.id.symbolRadioGroup);
        radioX = findViewById(R.id.radioX);
        radioO = findViewById(R.id.radioO);
        roundsSpinner = findViewById(R.id.roundsSpinner);
        btnPlay = findViewById(R.id.btnPlay);
        btnRules = findViewById(R.id.btnRules);
        btnLastTournament = findViewById(R.id.btnLastTournament);
    }

    private void setupSpinner() {
        String[] rounds = {"5 parties", "10 parties", "15 parties"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, rounds) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((android.widget.TextView) view).setTextColor(getResources().getColor(R.color.text_white));
                ((android.widget.TextView) view).setTextSize(16);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((android.widget.TextView) view).setTextColor(getResources().getColor(R.color.text_white));
                ((android.widget.TextView) view).setTextSize(16);
                view.setBackgroundColor(getResources().getColor(R.color.secondary_dark));
                view.setPadding(32, 24, 32, 24);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roundsSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        btnPlay.setOnClickListener(v -> startGame());
        btnRules.setOnClickListener(v -> showGameRules());
        btnLastTournament.setOnClickListener(v -> showLastTournament());
    }

    private void startGame() {
        String playerSymbol = radioX.isChecked() ? "X" : "O";
        String selectedRound = roundsSpinner.getSelectedItem().toString();
        int totalRounds = Integer.parseInt(selectedRound.split(" ")[0]);

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("PLAYER_SYMBOL", playerSymbol);
        intent.putExtra("TOTAL_ROUNDS", totalRounds);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void showGameRules() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Principe du jeu X-O");
        builder.setMessage("Le jeu X-O se joue sur une grille de 3 × 3 cases.\n\n" +
                "Vous jouez contre la machine. À tour de rôle, chaque joueur place son symbole dans une case vide.\n\n" +
                "Une partie se termine lorsqu'un joueur aligne trois symboles identiques sur une ligne, " +
                "une colonne ou une diagonale, ou lorsque toutes les cases sont remplies sans qu'aucun " +
                "joueur n'ait gagné, ce qui entraîne une partie nulle.\n\n" +
                "Dans le cadre du tournoi, plusieurs parties se succèdent, et les scores sont cumulés " +
                "automatiquement pour vous et la machine ainsi que pour les parties nulles.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showLastTournament() {
        try {
            FileInputStream fis = openFileInput("tournament_data.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TournamentData data = (TournamentData) ois.readObject();
            ois.close();
            fis.close();

            String message = "Score Joueur (" + data.getPlayerSymbol() + "): " + data.getScorePlayer() + "\n" +
                    "Score Machine: " + data.getScoreMachine() + "\n" +
                    "Parties nulles: " + data.getScoreDraw() + "\n" +
                    "Total de parties: " + data.getTotalRounds() + "\n" +
                    "Vainqueur: " + data.getWinner();

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Dernier Tournoi");
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.show();

        } catch (Exception e) {
            Toast.makeText(this, "Aucun tournoi sauvegardé", Toast.LENGTH_SHORT).show();
        }
    }
}