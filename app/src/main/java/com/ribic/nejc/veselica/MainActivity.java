package com.ribic.nejc.veselica;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.objects.Date;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public TextView testTextView;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testTextView = (TextView) findViewById(R.id.testTextView);
        new FetchData().execute();
    }

    private class FetchData extends AsyncTask<String, String, ArrayList<Date>> {

        @Override
        protected ArrayList<Date> doInBackground(String... strings) {
            URL url = NetworkUtils.buildUrl();
            ArrayList<Date> dates = new ArrayList<>();
            try {
                String result = NetworkUtils.getResponseFromHttpUrl(url);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Date date = new Date();
                    String dateAndName = jsonObject.getString("date");
                    date.setDate(dateAndName);

                    ArrayList<Party> parties= new ArrayList<>();

                    JSONArray jsonArray1 = jsonObject.getJSONArray("places");
                    for (int j = 0; j < jsonArray1.length(); j++){
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("name");
                        String href = jsonObject1.getString("href");
                        Party party = new Party();
                        party.setName(name);
                        party.setHref(href);
                    }
                    date.setPlaces(parties);
                    dates.add(date);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "problem appeared when parsing JSON");
            }
            Log.v(TAG, "result get from internet");
            return dates;
        }

        @Override
        protected void onPostExecute(ArrayList<Date> dates) {
            super.onPostExecute(dates);
            //TODO set adapter
        }
    }

}




