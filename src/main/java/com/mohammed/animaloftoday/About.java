package com.mohammed.animaloftoday;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.database.sqlite.
import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

    }

    public void facebook(View view) {
        try {
            getPackageManager().getPackageInfo(ConstValue.FACEBOOK_PACKAGE, 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstValue.MY_FACEBOOK));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(Intent.EXTRA_TEXT, ConstValue.MY_FACEBOOK);
            startActivity(intent);
        }
    }

    public void twitter(View view) {
        try {
            getPackageManager().getPackageInfo(ConstValue.TWITTER_PACKAGE, 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstValue.MY_TWITTER));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(Intent.EXTRA_TEXT, ConstValue.MY_TWITTER);
            startActivity(intent);
        }
    }

    public void github(View view) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(Intent.EXTRA_TEXT, ConstValue.MY_GITHUB);
        startActivity(intent);
    }
}