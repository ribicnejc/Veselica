package com.ribic.nejc.veselica;

import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.MainAdapter;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.utils.NetworkUtils;
import com.ribic.nejc.veselica.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
//TODO make notification
public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler{
    public ProgressBar mProgressBar;
    public RecyclerView mRecyclerView;
    public MainAdapter mMainAdapter;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_main_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_parties);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        new FetchData().execute();
    }

    @Override
    public void partyOnClick(int clickedItemIndex) {
        Toast.makeText(this, "Time to go to sleep!", Toast.LENGTH_SHORT).show();
    }

    public void testNotification(View view) {
        NotificationUtils.remindUserBecauseCharging(this);
    }

    private class FetchData extends AsyncTask<String, String, ArrayList<Party>> {

        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Party> doInBackground(String... strings) {
            URL url = NetworkUtils.buildUrl();
            ArrayList<Party> parties = new ArrayList<>();
            try {
                String result = NetworkUtils.getResponseFromHttpUrl(url);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("places");
                    for (int j = 0; j < jsonArray1.length(); j++){
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name = jsonObject1.getString("name");
                        String href = jsonObject1.getString("href");
                        Party party = new Party();
                        party.setPlace(name);
                        party.setHref(href);
                        party.setDate(date);
                        parties.add(party);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "problem appeared when parsing JSON");
            }
            Log.v(TAG, "result get from internet");
            return parties;
        }

        @Override
        protected void onPostExecute(ArrayList<Party> parties) {
            super.onPostExecute(parties);
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mMainAdapter = new MainAdapter(parties, MainActivity.this);
            mRecyclerView.setAdapter(mMainAdapter);
        }
    }

}




