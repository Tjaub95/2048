package com.tjewell.csse2048;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Tyler_Jewell on 4/13/17.
 */

public class TwoZeroFourEightActivity extends Activity implements View.OnClickListener {
    private GridView gridView;
    private Button restart;
    private ImageView left;
    private ImageView up;
    private ImageView down;
    private ImageView right;
    private TextView score;
    private TextView highScore;
    private TwoZeroFourEightFragment frag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_zero_four_eight);

//        gridView = new GridView(getApplicationContext());
        restart = (Button) findViewById(R.id.restart);
        restart.setOnClickListener(this);
        left = (ImageView) findViewById(R.id.imageLeft);
        left.setOnClickListener(this);
        right = (ImageView) findViewById(R.id.imageRight);
        right.setOnClickListener(this);
        up = (ImageView) findViewById(R.id.imageUp);
        up.setOnClickListener(this);
        down = (ImageView) findViewById(R.id.imageDown);
        down.setOnClickListener(this);

        highScore = (TextView) findViewById(R.id.high_score);
        score = (TextView) findViewById(R.id.score);

        TwoZeroFourEightFragment twoZeroFourEightFragment = new TwoZeroFourEightFragment();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.grid_twofourzeroeight, twoZeroFourEightFragment)
                .commit();

        load();
    }

    private void load() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        score.setText("SCORE: " + settings.getLong("score", 0));
        highScore.setText("HIGH SCORE: " + settings.getLong("high score temp", 0));
    }

    @Override
    public void onClick(View view) {
        frag = (TwoZeroFourEightFragment) getFragmentManager().findFragmentById(R.id.grid_twofourzeroeight);
        // 0: up, 1: right, 2: down, 3: left
        switch (view.getId()) {
            case R.id.restart:
                frag.getGridView().game.newGame();
                score.setText("SCORE: " + frag.getGridView().game.score);
                highScore.setText("HIGH SCORE: " + frag.getGridView().game.highScore);
                break;
            case R.id.imageDown:
                frag.getGridView().game.move(2);
                score.setText("SCORE: " + frag.getGridView().game.score);
                highScore.setText("HIGH SCORE: " + frag.getGridView().game.highScore);
                break;
            case R.id.imageUp:
                frag.getGridView().game.move(0);
                score.setText("SCORE: " + frag.getGridView().game.score);
                highScore.setText("HIGH SCORE: " + frag.getGridView().game.highScore);
                break;
            case R.id.imageLeft:
                frag.getGridView().game.move(3);
                score.setText("SCORE: " + frag.getGridView().game.score);
                highScore.setText("HIGH SCORE: " + frag.getGridView().game.highScore);
                break;
            case R.id.imageRight:
                frag.getGridView().game.move(1);
                score.setText("SCORE: " + frag.getGridView().game.score);
                highScore.setText("HIGH SCORE: " + frag.getGridView().game.highScore);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
