package com.ribic.nejc.veselica.utils;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String COUNTRY_PARTIES_URL = "http://nejcribic.com/veselice_api/myApi.php?getAll/";

    private static final String format = "json";

    public static URL buildUrl(){
        Uri builtUri = Uri.parse(COUNTRY_PARTIES_URL);

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if(scanner.hasNext()){
                return scanner.next();
            }else return null;
        }finally {
            urlConnection.disconnect();
        }
    }


}
