package com.mohammed.animaloftoday;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button getImgBtn;
    URL url;
    TextView info;
    ImageView imageView;
    ProgressDialog progressDialog;
    String data;
    AsyncTask mMyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.animal_img);
        getImgBtn = findViewById(R.id.getImage_btn);
        info = findViewById(R.id.data_tv);
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
                                    mMyTask = new DownloadTask().execute(stringToURL(response.getString("image_link")));
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
                                            + String.format("Average length: %0.2f m \n", ava_len)
                                            + String.format("Average weight: %0.2f kg\n", ava_w)
                                            + String.format("Link: %8s\n", response.getString("image_link"));
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

                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });
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
