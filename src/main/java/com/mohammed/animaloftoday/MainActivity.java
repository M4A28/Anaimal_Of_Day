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
    Button getImgBtn;
    URL url;
    TextView info;
    public static final String ANIMAL_INFO = "info";
    ImageView imageView;
    ProgressDialog progressDialog;
    String data;
    SharedPreferences sharedPreferences = null;
    AsyncTask mMyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.animal_img);
        getImgBtn = findViewById(R.id.getImage_btn);
        info = findViewById(R.id.data_tv);
        sharedPreferences = getBaseContext().getSharedPreferences(ANIMAL_INFO, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("progress");
        progressDialog.setMessage("Downlodaing....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

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

                                    mMyTask = new DownloadTask().execute(stringToURL(response.getString("image_link")));
                                    downloadImage(response.getString("image_link"));
                                    double ava_len = ((Double.parseDouble( response.getString("length_max"))
                                            +  Double.parseDouble(response.getString("length_min") )) /2) * 0.305;
                                    double ava_w = ((Double.parseDouble( response.getString("weight_min"))
                                            +  Double.parseDouble(response.getString("weight_max") )) /2) * 0.454;

                                    data = String.format("Name: %8s\n", response.getString("name"))
                                            + String.format("Latin name: %8s\n", response.getString("latin_name"))
                                            + String.format("Animal type: %8s\n", response.getString("animal_type"))
                                            + String.format("Diet: %8s\n", response.getString("diet"))
                                            + String.format("Geo range: %8s\n", response.getString("geo_range"))
                                            + String.format("Habitat: %8s\n", response.getString("habitat"))
                                            + ""+ String.format("Average length: %.2f m \n", ava_len)
                                            + ""+ String.format("Average weight: %.2f kg\n", ava_w)
                                            + String.format("Link: %8s\n", response.getString("image_link"));

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ANIMAL_INFO, data);
                                    editor.commit();
                                    info.setText(data);
                                    String imgUrl = response.getString("image_link");
                                    Glide.with(MainActivity.this).load(imgUrl).into(imageView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        info.setText(sharedPreferences.getString(ANIMAL_INFO,""));
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

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/";

        final File dir = new File(dirPath);

        final String fileName = imageURL.substring(imageURL.lastIndexOf('/') + 1);

        Glide.with(this)
                .load(imageURL)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
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

        boolean successDirCreated = false;
        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir();
        }
        else
            successDirCreated = true;

        if (successDirCreated) {
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

        }else{
            Toast.makeText(this, "Failed to make folder!", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadTask extends AsyncTask<URL,Void,Bitmap> {
        protected void onPreExecute(){
            progressDialog.show();
        }
        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        // When all async task done
        protected void onPostExecute(Bitmap result){
            // Hide the progress dialog
            progressDialog.dismiss();
        }
    }
    protected URL stringToURL(String data) {
        try {
            url = new URL(data);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
