package com.mohammed.animaloftoday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String ANIMAL_INFO = "info";
    Button getImgBtn;
    TextView name;
    TextView live;
    TextView dite;
    TextView geo;
    TextView type;
    TextView lenth;
    TextView weight;
    TextView haptit;
    TextView link;
    ImageView imageView;
    ProgressDialog progressDialog;
    AsyncTask mMyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyPermissions();
        imageView = findViewById(R.id.animal_img);
        getImgBtn = findViewById(R.id.getImage_btn);
        name = findViewById(R.id.tv_name);
        live = findViewById(R.id.tv_livespin);
        dite = findViewById(R.id.tv_dite);
        geo = findViewById(R.id.tv_geo);
        type = findViewById(R.id.tv_type);
        lenth = findViewById(R.id.tv_lenght);
        weight = findViewById(R.id.tv_weight);
        link = findViewById(R.id.tv_link);
        haptit = findViewById(R.id.tv_hiptit);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("progress");
        progressDialog.setMessage("Downlodaing....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        getImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String Link = "https://zoo-animal-api.herokuapp.com/animals/rand/";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        Link, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    double ava_len = ((Double.parseDouble( response.getString("length_max"))
                                            +  Double.parseDouble(response.getString("length_min") )) /2) * 0.305;
                                    double ava_w = ((Double.parseDouble( response.getString("weight_min"))
                                            +  Double.parseDouble(response.getString("weight_max") )) /2) * 0.454;

                                    mMyTask = new DownloadTask().execute(response.getString("image_link"));
                                    name.setText(response.getString("name"));
                                    type.setText(String.format("Animal type: %4s", response.getString("animal_type")));
                                    dite.setText(String.format("Diet: %4s", response.getString("diet")));
                                    live.setText(String.format("Life span: %4s year", response.getString("lifespan")));
                                    geo.setText(String.format("Geo range: %4s", response.getString("geo_range")));
                                    haptit.setText(String.format("Habitat: %4s", response.getString("habitat")));
                                    lenth.setText(String.format("Average length: %.2f m ", ava_len));
                                    weight.setText(String.format("Average weight: %.2f kg", ava_w));
                                    link.setText(response.getString("image_link"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "No Internet Avalable", Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });


    }

    private void downloadImage(String imageURL){

        if (!verifyPermissions()) {
           return;
        }

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Animal_Image" + "/";

        File dir = new File(dirPath);

        String fileName = imageURL.substring(imageURL.lastIndexOf('/') + 1);

        Glide.with(this)
                .load(imageURL)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        final  Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                        Toast.makeText(MainActivity.this, "Saving Image...", Toast.LENGTH_SHORT).show();
                        saveImage(bitmap, dir, fileName);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Toast.makeText(MainActivity.this, "Failed to Download Image! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Boolean verifyPermissions() {

        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1);
            return false;
        }
        return true;

    }

    private void saveImage(Bitmap image, File storageDir, String imageFileName) {

        if (!storageDir.exists()) {
           storageDir.mkdir();
        }


            File imageFile = new File(storageDir, imageFileName);
            String savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
                Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error while saving image!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
    }

    private class DownloadTask extends AsyncTask<String,Void,Boolean> {
        protected void onPreExecute(){
            progressDialog.show();
        }
        protected Boolean doInBackground(String...urls){
            try {
                downloadImage(urls[0]);
                return true;
            }
            catch (Exception e){
                return false;
            }
        }
        // When all async task done
        protected void onPostExecute(Boolean result){
            // Hide the progress dialog
            if(result)
                progressDialog.dismiss();

        }
    }
}
