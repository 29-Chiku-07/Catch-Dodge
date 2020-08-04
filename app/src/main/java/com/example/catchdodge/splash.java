package com.example.catchdodge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class splash extends AppCompatActivity {

    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    sleep(1700);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent mainIntent = new Intent(splash.this , MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        };
        thread.start();
       // Toast.makeText(splash.this, "...loading", Toast.LENGTH_SHORT).show();



    }



    protected void onPause() {
        super.onPause();
        finish();
    }

}