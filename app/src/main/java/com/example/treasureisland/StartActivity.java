package com.example.treasureisland;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity {
    GameView gameView;
    TextView textView;
    public int bestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        findViewById(R.id.ImageView01).setOnClickListener(ButtonClick);
        findViewById(R.id.ImageView02).setOnClickListener(ButtonClick);

        gameView = (GameView) findViewById(R.id.gameView);
        textView = (TextView) findViewById(R.id.TextView01);

        SharedPreferences prefs = getSharedPreferences("pref",0);
        bestScore = prefs.getInt("bestScore", 1000);

        textView.setText("BEST SCORE : " + bestScore);
    }

    Button.OnClickListener ButtonClick = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ImageView01:      // 게임 시작 버튼
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.ImageView02:      // 게임 종료 버튼
                    gameView.StopGame();
                    finish();
            }
        }
    };
}
