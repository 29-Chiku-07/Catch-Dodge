package com.example.catchdodge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import
        android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.health.TimerStat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //frame
    private FrameLayout gameFrame;
    private int frameHeight , frameWidth , initialFrameWidth;
    private LinearLayout startLayout;

    //image
    private ImageView box, black, orange , pink;
    private Drawable imageBoxRight , imageBoxLeft;

    //size
    private int boxSize;

    //position
    private float boxX , boxY;
    private float blackX,blackY;
    private float orangeX,orangeY;
    private float pinkX,pinkY;

    //score
    private TextView scoreLabel,highScoreLabel;
    private int score , highScore , timeCount;
    private SharedPreferences settings;

    //class
    private Timer timer;
    private Handler handler = new Handler();

    //status
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean pink_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        box = findViewById(R.id.box);
        black = findViewById(R.id.black);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        imageBoxLeft = getResources().getDrawable(R.drawable.box1);
        imageBoxRight = getResources().getDrawable(R.drawable.box1);

        //high score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE" , 0);
        highScoreLabel.setText("High Score : "+ highScore);

    }

    public void changePos(){

        //timecount
        timeCount += 20;

        //orange
        orangeY+=12;
        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if(hitCheck(orangeCenterX , orangeCenterY)){
            orangeY = frameHeight + 100;
            score += 10;
          //  soundPlayer.playHitOrangeSound();
        }

        if(orangeY > frameHeight){
            orangeY = -100;
            orangeX = (float) Math.floor(Math.random() * (frameWidth - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //pink
        if(!pink_flg && timeCount % 5000 == 0){
            pink_flg = true;
            pinkY = -20;
            pinkX = (float) Math.floor(Math.random() * (frameWidth - pink.getWidth()));
        }

        if(pink_flg) {
            pinkY += 20;

            float pinkCenterX = pinkX + pink.getWidth() / 2;
            float pinkCenterY = pinkY + pink.getHeight() / 2;

            if (hitCheck(pinkCenterX, pinkCenterY)) {
                pinkY = frameHeight + 30;
                score += 30;

                //change frame width
                if(initialFrameWidth > frameWidth * 110/100){
                    frameWidth = frameWidth * 110/100;
                    changeFrameWidth(frameWidth);
                }

               // soundPlayer.playHitPinkSound();

            }

            if (pinkY > frameHeight) pink_flg = false;

            pink.setX(pinkX);
            pink.setY(pinkY);
        }

        //black
        blackY += 18;

        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if(hitCheck(blackCenterX , blackCenterY)){
            blackY = frameHeight + 100;

            //change frame
            frameWidth = frameWidth * 80/100;
            changeFrameWidth(frameWidth);

         //   soundPlayer.playHitBlackSound();

            if(frameWidth <= boxSize){
                //gameover
                gameOver();
            }

        }

        if(blackY > frameHeight){
            blackY = -100;
            blackX = (float) Math.floor(Math.random() * (frameWidth - black.getWidth()));
        }
        black.setX(blackX);
        black.setY(blackY);


        // move box
        if(action_flg) {
            //Touching
            boxX += 14;
            box.setImageDrawable(imageBoxRight);
        } else {
            boxX -= 14;
            box.setImageDrawable(imageBoxLeft);
        }

        //check box position
        if(boxX < 0){
            boxX = 0;
            box.setImageDrawable(imageBoxRight);
        }

        if(frameWidth - boxSize < boxX){
            boxX = frameWidth - boxSize;
            box.setImageDrawable(imageBoxLeft);
        }

        box.setX(boxX);

        scoreLabel.setText("Score : "+score);

    }

    public boolean hitCheck(float x , float y){
        if(boxX <= x && x <= boxX + boxSize &&
            boxY <= y && y <= frameHeight ){
            return true;
        }
        return false;
    }


    public void changeFrameWidth(int frameWidth){
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);

    }

    public void gameOver(){
        //stop timer
        timer.cancel();
        timer = null;
        start_flg = false;

        //before startLayout
        try{
            TimeUnit.SECONDS.sleep(1);
            Toast.makeText(MainActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
        }catch(InterruptedException e){
            e.printStackTrace();
        }



        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        black.setVisibility(View.INVISIBLE);
        orange.setVisibility(View.INVISIBLE);
        pink.setVisibility(View.INVISIBLE);

        //update high score
        if(score > highScore){
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE" , highScore);
            editor.commit();
        }


    }

   @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(start_flg){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;
            }  else if(event.getAction() == MotionEvent.ACTION_UP){
                action_flg = false;
            }
        }

        return true;
    }

    public void startGame(View view){

        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if(frameHeight == 0)
        {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            boxSize  = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidth = initialFrameWidth;

        box.setX(0.0f);
        black.setY(3000.0f);
        orange.setY(3000.0f);
        pink.setY(3000.0f);

        blackY = black.getY();
        orangeY = orange.getY();
        pinkY = pink.getY();

        box.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        pink.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(start_flg){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });

                }
            }
        } , 0 , 20);


    }

    public void quitGame(View view){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            finishAndRemoveTask();
        }  else {
            finish();
        }

    }

}
