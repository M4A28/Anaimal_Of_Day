package com.mohammed.animaloftoday;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private TextView name, diet, geo, live, type;
    private TextView length, weight, habitat, link;
    private ImageView imageView;

    // this is just for readability i know i can use array of string
    private String Aname, Adiet, Ageo, Alive, Atype, Alength, Aweight, Ahabitate, Alink;

    // this is also for readability
    private String data = "";
    private ProgressDialog progressDialog;
    private AsyncTask mMyTask;
    public SharedPreferences setting = null;
    public SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // verify permissions
        Utility.verifyPermissions(MainActivity.this, this);

        ActionReceiver actionReceiver = new ActionReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(actionReceiver, intentFilter);

        setting = getSharedPreferences(ConstValue.SHARED_PREF, MODE_PRIVATE);
        editor = setting.edit();

        // inflate UI element
        imageView = findViewById(R.id.animal_img);
        name = findViewById(R.id.tv_name);
        live = findViewById(R.id.tv_livespan);
        diet = findViewById(R.id.tv_diet);
        geo = findViewById(R.id.tv_geo);
        type = findViewById(R.id.tv_type);
        length = findViewById(R.id.tv_length);
        weight = findViewById(R.id.tv_weight);
        link = findViewById(R.id.tv_link);
        habitat = findViewById(R.id.tv_habitat);

        // init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("progress");
        progressDialog.setMessage("Downloading Image....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // to generate random animal after launch
        getRandomAnimalAndSaveImage();

        // to record last time app was run
        // TODO: add some sort of sitting to app ;)
        if (!setting.contains(ConstValue.LAST_RUN))
            enableNotification();
        else
            recordeRunTime();

        startService(new Intent(this, CheckRecentRun.class));
    }

    // main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // good method to enable notification
    // TODO: add setting to make app batter
    private void enableNotification() {
        editor.putLong(ConstValue.LAST_RUN, System.currentTimeMillis());
        editor.putBoolean(ConstValue.ENABLED, true);
        editor.commit();
    }

    // record time since last time app used
    private void recordeRunTime() {
        editor.putLong(ConstValue.LAST_RUN, System.currentTimeMillis());
        editor.commit();
    }

    // generate random animal and change image view
    // TODO: optimize this method for the love of god
    // this code not goof for me as Mohammed Mosa
    public void getRandomAnimalAndSaveImage() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        @SuppressLint("DefaultLocale") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                ConstValue.API_URL, null,
                response -> {
                    try {
                        double ava_len = ((Double.parseDouble(response.getString("length_max"))
                                + Double.parseDouble(response.getString("length_min"))) / 2) * 0.305;
                        double ava_w = ((Double.parseDouble(response.getString("weight_min"))
                                + Double.parseDouble(response.getString("weight_max"))) / 2) * 0.454;
                        Aname = response.getString("name");
                        Atype = String.format("Animal type: %4s", response.getString("animal_type"));
                        Adiet = String.format("Diet: %4s", response.getString("diet"));
                        Alive = String.format("Life span: %4s year", response.getString("lifespan"));
                        Ageo = String.format("Geo range: %4s", response.getString("geo_range"));
                        Ahabitate = String.format("Habitat: %4s", response.getString("habitat"));
                        Alength = String.format("Average length: %.2f m ", ava_len);
                        Aweight = String.format("Average weight: %.2f kg", ava_w);
                        Alink = response.getString("image_link");
                        data = Aname + "\n" + Atype + "\n" + Adiet + "\n"
                                + Alive + "\n" + Ageo + "\n" + Ahabitate + "\n"
                                + Alength + "\n" + Aweight + "\n" + "Image Link:" + Alink;
                        // this long code can don by for loop but i
                        name.setText(Aname);
                        type.setText(Atype);
                        diet.setText(Adiet);
                        live.setText(Alive);
                        geo.setText(Ageo);
                        habitat.setText(Ahabitate);
                        length.setText(Alength);
                        weight.setText(Aweight);
                        link.setText(Alink);
                        link.setMovementMethod(LinkMovementMethod.getInstance());
                        mMyTask = new DownloadTask().execute(Alink);

                    } catch (JSONException e) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(MainActivity.this, "No Internet Available", Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }

    // download image from url


    // main menu share current animal info
    public void shareAnimal(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(Intent.createChooser(intent, "Share info using"));
    }

    // main menu share app
    public void shareApp(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, ConstValue.MY_TWITTER);
        startActivity(Intent.createChooser(intent, "Share app  using"));
    }

    // main menu about app
    public void about(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }

    // FAB click method
    @SuppressLint("UseCompatLoadingForDrawables")
    public void getRandomAnimal(View view) {
        imageView.setImageDrawable(getDrawable(R.drawable.ic_no_image));
        getRandomAnimalAndSaveImage();
    }

    // re downloads image
    public void tryToDownload(View view) {
        if (Alink != null) {
            mMyTask = new DownloadTask().execute(Alink);
        }
    }

    // to conferm exit from app
    public void exitDialog(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCancelable(false);
        dialog.show();

        Button exit = dialog.findViewById(R.id.yes_button);
        Button dismiss = dialog.findViewById(R.id.no_button);
        exit.setOnClickListener(view -> finish());
        dismiss.setOnClickListener(view -> dialog.dismiss());

    }
    @Override
    public void onBackPressed() {
        exitDialog();
    }

    // just to download and show progress dialog
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {

            progressDialog.show();
        }

        protected Void doInBackground(String... urls) {
            Utility.downloadImage(MainActivity.this, MainActivity.this, imageView, urls[0]);
            return null;
        }

    }
}
