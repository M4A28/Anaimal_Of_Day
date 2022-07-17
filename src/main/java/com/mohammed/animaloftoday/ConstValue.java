package com.mohammed.animaloftoday;

import android.os.Environment;

public class ConstValue {
    public static final int NOTIFICATION_ID = 9909;
    public static final int REQ_CODE_FOR_ALARM = 206917;
    public static final Long MILLI_SECS_PER_MIN = 60000L;
    public static final Long MILLI_SECS_PER_DAY = 86400000L;
    public static final Long DELAY = 259200000L;
    public static final String SHARED_PREF = "SHARED_PREF";
    public static final String TAG = "TAGX";
    public static final String LAST_RUN = "LastRun";
    public static final String ENABLED = "enabled";
    public static final String API_URL = "https://zoo-animal-api.herokuapp.com/animals/rand/";
    public static final String MY_TWITTER = "https://twitter.com/M4A28";
    public static final String MY_FACEBOOK = "https://facebook.com/M4A28";
    public static final String MY_GITHUB = "https://github.com/M4A28";
    public static final String TWITTER_PACKAGE = "com.twitter.android";
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String DID_YOU_KNOW = "Did you know this ?";
    public static final String MISS_TITLE = "WE MISS YOU!";
    public static final String MISS_DATA = "come and get new animals";
    public static final String CHANNEL_ID = "AnimalOfDay";
    public static final String NOTIFICATION_NAME = "Animal Of Day";
    public static final String DIRE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/" + "Animal Image" + "/";
    public static final String DIRE_PATH_R = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/" + "Animal Image" + "/";


}