package vn.edu.fpt.blackjack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ImageView dealerCard1, dealerCard2, playerCard1, playerCard2;
    private TextView tvScore;
    private LinearLayout playerCardsLayout, dealerCardsLayout;
    private Button btnHit, btnStand, btnRestart;
    private ArrayList<String> deck;
    private ArrayList<String> playerHand, dealerHand;
    private int playerScore, dealerScore;
    private boolean gameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dealerCard1 = findViewById(R.id.dealer_card1);
        dealerCard2 = findViewById(R.id.dealer_card2);
        playerCard1 = findViewById(R.id.player_card1);
        playerCard2 = findViewById(R.id.player_card2);
        tvScore = findViewById(R.id.tvScore);
        btnHit = findViewById(R.id.btnHit);
        btnStand = findViewById(R.id.btnStand);
        btnRestart = findViewById(R.id.btnRestart);
        playerCardsLayout = findViewById(R.id.playerCards);
        dealerCardsLayout = findViewById(R.id.dealerCards);

        btnHit.setOnClickListener(view -> hit());
        btnStand.setOnClickListener(view -> stand());
        btnRestart.setOnClickListener(view -> startGame());

        startGame();
    }

    private void startGame() {
        gameOver = false;
        deck = generateDeck();
        Collections.shuffle(deck);

        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        playerHand.add(deck.remove(0));
        playerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));

        updateUI(false);
    }

    private ArrayList<String> generateDeck() {
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k", "a"};
        ArrayList<String> newDeck = new ArrayList<>();
        for (String suit : suits) {
            for (String rank : ranks) {
                newDeck.add(suit + "_" + rank);
            }
        }
        return newDeck;
    }

    private void updateUI(boolean revealDealer) {
        playerCardsLayout.removeAllViews();
        dealerCardsLayout.removeAllViews();

        int playerCardSize = calculateCardSize(playerHand.size());
        int dealerCardSize = calculateCardSize(dealerHand.size());

        for (String card : playerHand) {
            addCardToLayout(playerCardsLayout, card, playerCardSize);
        }
        for (int i = 0; i < dealerHand.size(); i++) {
            if (i == 0 || revealDealer) {
                addCardToLayout(dealerCardsLayout, dealerHand.get(i), dealerCardSize);
            } else {
                addCardToLayout(dealerCardsLayout, "back_dark", dealerCardSize);
            }
        }

        playerScore = calculateScore(playerHand);
        dealerScore = calculateScore(dealerHand);
        tvScore.setText("Player: " + playerScore + " | Dealer: " + (revealDealer ? dealerScore : "?"));
    }

    private int calculateCardSize(int cardCount) {
        if (cardCount <= 2) return 300;
        return Math.max(100, 600 / cardCount);
    }

    private void addCardToLayout(LinearLayout layout, String cardName, int size) {
        ImageView cardView = new ImageView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(size, (int) (size * 1.5)));
        cardView.setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
        layout.addView(cardView);
    }

    private int calculateScore(ArrayList<String> hand) {
        int score = 0;
        int aces = 0;
        for (String card : hand) {
            String value = card.split("_")[1];
            if (value.equals("j") || value.equals("q") || value.equals("k")) {
                score += 10;
            } else if (value.equals("a")) {
                aces++;
                score += 11;
            } else {
                score += Integer.parseInt(value);
            }
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }

    private void hit() {
        if (!gameOver) {
            playerHand.add(deck.remove(0));
            updateUI(false);
            if (calculateScore(playerHand) > 21) {
                gameOver = true;
                tvScore.setText("Player Bust! Dealer Wins. Player: " + playerScore + " | Dealer: " + dealerScore);
            }
        }
    }

    private void stand() {
        if (!gameOver) {
            while (dealerScore < 17) {
                dealerHand.add(deck.remove(0));
                dealerScore = calculateScore(dealerHand);
            }
            updateUI(true);
            gameOver = true;
            if (dealerScore > 21) {
                tvScore.setText("Dealer Bust! Player Wins. Player: " + playerScore + " | Dealer: " + dealerScore);
            } else if (playerScore > dealerScore) {
                tvScore.setText("Player Wins! Player: " + playerScore + " | Dealer: " + dealerScore);
            } else if (playerScore < dealerScore) {
                tvScore.setText("Dealer Wins! Player: " + playerScore + " | Dealer: " + dealerScore);
            } else {
                tvScore.setText("Push! It's a tie. Player: " + playerScore + " | Dealer: " + dealerScore);
            }
        }
    }
}