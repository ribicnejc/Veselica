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
    private static final String API_KEY = "appid=b587472c-5fe5-4a16-83ae-626aa4aad33e";
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String COUNTRY_PARTIES_MAIN = "http://api.nejcribic.com/veselica/myApi.php?";
    private static final String COUNTRY_PARTIES_URL = COUNTRY_PARTIES_MAIN + "getAll/";


    private static final String format = "json";

    public static String getUrlAll() {
        return COUNTRY_PARTIES_URL + "&" + API_KEY;
    }

    public static String getUrlPlace(String place) {
        return String.format("%s/place/%s&%s", COUNTRY_PARTIES_URL, place, API_KEY);
    }

    public static String getUrlDate(String date) {
        return String.format("%s/date/%s&%s", COUNTRY_PARTIES_URL, date, API_KEY);
    }

    public static String getUrlDay(String day) {
        return String.format("%s/day/%s&%s", COUNTRY_PARTIES_URL, day, API_KEY);
    }

    public static String getUrlMoreInfo(String href) {
        return String.format("%smoreInfo%s&%s", COUNTRY_PARTIES_MAIN, href, API_KEY);
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
