package com.example.zorg.androidsnakepoc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.zorg.androidsnakepoc.SnakeApp;

public class SnakeScreen extends Activity {

    private Game game;
    private FrameLayout frameView;
    private FrameLayout accFrame;
    private TextView score;
    private TextView xAccView;
    private TextView yAccView;
    private Activity mActivity;
    private AlertDialog.Builder alertWin;
    private double threshold;
    private boolean gameEnd;
    private AccManager accManager;
    private SoundManager soundManager;
    private double range;
    private GameAnalytics gameAnal;
    private List<GameAnalytics> games;

    // acc visualizer
    private AccVisualizer accView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_snake_screen);

            // analytics init
            gameAnal = new GameAnalytics();
            games = gameAnal.getGames();

            // game init
            mActivity = this;
            score = (TextView) findViewById(R.id.score);
            game = new Game(this, this, score, gameAnal);
            frameView = (FrameLayout) findViewById(R.id.gameFrame);
            frameView.addView(game);

            // accView init
            accView = new AccVisualizer(this);
            accFrame = (FrameLayout) findViewById(R.id.accFrame);
            accFrame.addView(accView);

            // x/ y text view init
            xAccView = (TextView) findViewById(R.id.xTextView);
            yAccView = (TextView) findViewById(R.id.yTextView);

            // sound manager init
            soundManager = new SoundManager(this);

            // configuration init
            threshold = SnakeApp.getThreshold();
            range = SnakeApp.getRange();

            // acc manager init
            accManager = new AccManager(this, threshold,
                    (SensorManager) getSystemService(SENSOR_SERVICE));

            soundManager.playBackground();

        } catch (Exception e) {
            Log.println(0, "ERROR", e.toString());
        }
    }

    public void move(float x, float y) {

        try {
            if (!game.snake.stopped) {
                if (y < -threshold && (x > -range && x < range)) {
                    game.snake.turnUp();
                } else if (y > threshold && (x > -range && x < range)) {
                    game.snake.turnDown();
                } else if (x < -threshold && (y > -range && y < range)) {
                    game.snake.turnRight();
                } else if (x > threshold && (y > -range && y < range)) {
                    game.snake.turnLeft();
                }
            } else if (gameEnd) {
                if (x < -threshold) {
                    // creating new game analytics
                    gameAnal = new GameAnalytics();
                    game.setup();
                    game.invalidate();
                    gameEnd = false;
                } else if (x > threshold) {
                    // writing games to xml before closing app
                    gameAnal.writeGamesToXML(games);
                    soundManager.releaseAll();
                    accManager.unregisterListener();
                    mActivity.finish();
                }
            }
        } catch (Exception e) {

            Log.println(0, "ERROR", e.toString());
        }
    }

    public void gameOver() {

        // setting game analytics values
        //Time tmp = new Time();
        //tmp.setToNow();
        //gameAnal.setEndTime(tmp);

        Date endDate = new Date(System.currentTimeMillis());
        gameAnal.setEndDate(endDate);
        gameAnal.seteReasonOfDeathFromInt(Game.reasonOfDeath);
        gameAnal.setScore(game.getScore());
        // adding game to games list
        games.add(gameAnal);

        game.snake.stopped = true;
        soundManager.playDie();
        accManager.unregisterListener();
        soundManager.playPlayAgain();
        gameEnd = true;
        accManager.registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (game.gameOver)
            return;

        game.snake.stopped = true;

        final CharSequence[] items = { "Continue", "Start Over", "Close" };
        alertWin = new AlertDialog.Builder(this);
        alertWin.setTitle("Game Paused");
        alertWin.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    // New Game
                    case 1:
                        gameAnal = new GameAnalytics();
                        game.setup();
                        game.invalidate();
                        break;

                    // close
                    case 2:
                        // writing games to xml before closing app
                        gameAnal.writeGamesToXML(games);
                        soundManager.releaseAll();
                        mActivity.finish();

                        // Continue
                    default:
                        game.snake.stopped = false;
                        game.invalidate();
                }
            }
        });

        alertWin.setCancelable(false);
        alertWin.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.playBackground();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK)
                && event.getRepeatCount() == 0)
            onPause();

        return true;
    }

    public void writeValuesToViews(float x, float y) {

        // send to accView set acc
        accView.setAcceleration(-x, y);
        // write values to x y view
        xAccView.setText("X: " + Float.toString(x));
        yAccView.setText("Y: " + Float.toString(y));
    }
}
