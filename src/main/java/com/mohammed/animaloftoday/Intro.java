package com.mohammed.animaloftoday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView logo = findViewById(R.id.app_icon);

        // start animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        logo.startAnimation(animation);
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            Intent intent = new Intent(Intro.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 5000);

    }

}