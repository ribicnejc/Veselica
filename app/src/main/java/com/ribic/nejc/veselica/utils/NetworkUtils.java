package com.ribic.nejc.veselica.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String COUNTRY_PARTIES_MAIN = "http://dev.nejcribic.com/veselice_api/myApi.php?";
    private static final String COUNTRY_PARTIES_URL = COUNTRY_PARTIES_MAIN + "getAll/";


    private static final String format = "json";

    public static String getUrlAll() {
        return COUNTRY_PARTIES_URL;
    }

    public static String getUrlPlace(String place) {
        return String.format("%s/place/%s", COUNTRY_PARTIES_URL, place);
    }

    public static String getUrlDate(String date) {
        return String.format("%s/date/%s", COUNTRY_PARTIES_URL, date);
    }

    public static String getUrlDay(String day) {
        return String.format("%s/day/%s", COUNTRY_PARTIES_URL, day);
    }

    public static String getUrlMoreInfo(String href) {
        return String.format("%smoreInfo%s", COUNTRY_PARTIES_MAIN, href);
    }


    public static URL buildUrl() {
        Uri builtUri = Uri.parse(COUNTRY_PARTIES_URL);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            } else return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean networkUp(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


}
