package com.mohammed.animaloftoday;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Utility {

    // Utility
    // show notification in big style ;)
    public static void showNotification(Context context, String title, String data) {
        NotificationChannel notificationChannel;
        Intent notificationIntent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent contentIntent;
        contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE);
        NotificationManager mNotificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(ConstValue.CHANNEL_ID, ConstValue.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Notification.BigTextStyle bigText = new Notification.BigTextStyle();
        bigText.bigText(data);
        Notification.Builder NotificationBuilder;

        // check Android API and do as needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationBuilder = new Notification.Builder(context, ConstValue.CHANNEL_ID);
        } else {
            NotificationBuilder = new Notification.Builder(context);
        }

        Notification.Builder mBuilder = NotificationBuilder;
        mBuilder.setSmallIcon(R.drawable.ic_verified);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(data);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder.setChannelId(ConstValue.CHANNEL_ID);
        }
        mNotificationManager.notify(ConstValue.NOTIFICATION_ID, mBuilder.build());
    }

    // show notification normal way
    public static void showSmallNotification(Context context, String title, String data) {
        NotificationChannel notificationChannel;
        Intent notificationIntent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent contentIntent;
        contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE);
        NotificationManager mNotificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(ConstValue.CHANNEL_ID, ConstValue.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Notification.Builder NotificationBuilder;

        // check Android API and do as needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationBuilder = new Notification.Builder(context, ConstValue.CHANNEL_ID);
        } else {
            NotificationBuilder = new Notification.Builder(context);
        }

        Notification.Builder mBuilder = NotificationBuilder;
        mBuilder.setSmallIcon(R.drawable.ic_app_icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(data);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder.setChannelId(ConstValue.CHANNEL_ID);
        }
        mNotificationManager.notify(ConstValue.NOTIFICATION_ID, mBuilder.build());
    }

    // generate random animal and push info to notification
    // TODO: DRY
    public static void randomAnimal(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        @SuppressLint("DefaultLocale") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                ConstValue.API_URL, null,
                response -> {
                    try {
                        double ava_len = ((Double.parseDouble(response.getString("length_max"))
                                + Double.parseDouble(response.getString("length_min"))) / 2) * 0.305;
                        double ava_w = ((Double.parseDouble(response.getString("weight_min"))
                                + Double.parseDouble(response.getString("weight_max"))) / 2) * 0.454;
                        String data = response.getString("name") + "\n"
                                + String.format("Animal type: %4s ", response.getString("animal_type")) + "\n"
                                + String.format("Diet: %4s", response.getString("diet")) + "\n"
                                + String.format("Life span: %4s year", response.getString("lifespan")) + "\n"
                                + String.format("Geo range: %4s", response.getString("geo_range")) + "\n"
                                + String.format("Habitat: %4s", response.getString("habitat")) + "\n"
                                + String.format("Average length: %.2f m", ava_len) + "\n"
                                + String.format("Average weight: %.2f kg", ava_w) + "\n"
                                + String.format("Image Link: %4s ", response.getString("image_link"));
                        showNotification(context, ConstValue.DID_YOU_KNOW, data);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(context, "No Internet Available", Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }

    public static void downloadImage(Context context, Activity activity, ImageView imageView, String imageURL) {

        String fileName = Utility.removeSpecialCharacter(imageURL.substring(imageURL.lastIndexOf('/') + 1));
        File dir;
        // chick permissions
        if (!Utility.verifyPermissions(context, activity))
            return;
        // from android 11 (R) and letter the way of saving changed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            dir = new File(ConstValue.DIRE_PATH_R);
        else
            dir = new File(ConstValue.DIRE_PATH);
        // TODO: make thumbnails to make app faster
        Glide.with(context)
                .load(imageURL)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        final Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        Toast.makeText(context, "Saving Image...", Toast.LENGTH_SHORT).show();
                        saveImage(context, bitmap, dir, fileName);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Toast.makeText(context,
                                "Failed to Download Image!", Toast.LENGTH_SHORT).show();
                        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_refresh));
                    }
                });
    }

    // check if device is connected to internet
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    // save image in device
    public static void saveImage(Context context, Bitmap image, File storageDir, String imageFileName) {

        if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        File imageFile = new File(storageDir, imageFileName);
        try {
            OutputStream fOut = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error while saving image!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // get required permissions to work fine
    public static boolean verifyPermissions(Context context, Activity activity) {
        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(activity, STORAGE_PERMISSIONS, 1);
            return false;
        }
        return true;
    }

    // remove special character from string to be more readable
    public static String removeSpecialCharacter(String text) {
        return text.replaceAll("[!@#?$><;:%^&*(){}+/~\\s]", "");
    }
}
