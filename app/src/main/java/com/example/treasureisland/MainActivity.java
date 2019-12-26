package com.example.treasureisland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    GameView gameView;
    boolean isPause = false;
    Menu menu1;
    int bestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = (GameView) findViewById(R.id.gameView);

        SharedPreferences prefs = getSharedPreferences("pref",0);
        this.bestScore = prefs.getInt("bestScore", gameView.score);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("pref",0);
        SharedPreferences.Editor editor = prefs.edit();
        bestScore = gameView.score;
        if(prefs.getInt("bestScore",1000) > bestScore){
            editor.putInt("bestScore",bestScore);
            editor.commit();
        }
    }

    //Option Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu1 = menu;
        menu1.add(0,1,0, "Quit Game");
        menu1.add(0,2,0, "Pause Game");
        menu1.add(0,3,0, "New Game");
        menu1.add(0,4,0, "Home");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:     // 게임 종료
                onStop();
                gameView.StopGame();
                finish();
                break;
            case 2:     // 일시 정지
                isPause = !isPause;
                if(isPause){
                    gameView.PauseGame();
                    item.setTitle("Resume Game");
                } else {
                    gameView.ResumeGame();
                    item.setTitle("Pause Game");
                }
                break;
            case 3:     // 새 게임
                onStop();
                onRestart();
                gameView.RestartGame();
                isPause = false;
                menu1.clear();
                onCreateOptionsMenu(menu1);
                break;
            case 4:     // 홈 화면
                onStop();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                gameView.StopGame();
                finish();
        }
        return true;
    }
}
