package com.ribic.nejc.veselica.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.adapters.VideosAdapter;
import com.ribic.nejc.veselica.fragments.MainEventsFragment;
import com.ribic.nejc.veselica.objects.Party;
import com.ribic.nejc.veselica.objects.Video;
import com.ribic.nejc.veselica.utils.NetworkUtils;
import com.ribic.nejc.veselica.utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements VideosAdapter.TrailersAdapterOnClickHandler{

    public final String TAG = "nejc";//DetailActivity.this.getSimpleName();
    public TextView mTextViewDate;
    public TextView mTextViewLocation;
    public TextView mTextViewRegion;
    public TextView mTextViewActors;
    public TextView mTextViewAbout;
    public ArrayList<Video> mVideos;
    public RecyclerView mRecyclerView;
    public VideosAdapter mAdapter;
    public Party party = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (!getIntent().hasExtra(MainEventsFragment.EXTRA_HREF)){
            return;
        }

        String href = getIntent().getStringExtra(MainEventsFragment.EXTRA_HREF);
        mTextViewDate = (TextView) findViewById(R.id.text_view_date);
        mTextViewActors = (TextView) findViewById(R.id.text_view_actors);
        mTextViewLocation = (TextView) findViewById(R.id.text_view_location);
        mTextViewAbout = (TextView) findViewById(R.id.text_view_about);
        mTextViewRegion = (TextView) findViewById(R.id.text_view_region);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_item);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        fetchData(href);
    }

    private void fetchData(final String href){
        String url = NetworkUtils.getUrlMoreInfo(href);
        final RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mVideos = new ArrayList<>();
                        try{
                            String title = response.getString("title");
                            String date = response.getString("date");
                            String actors = response.getString("actors");
                            String location = response.getString("location");
                            String region = response.getString("region");
                            String about = response.getString("about");
                            setTitle(title);
                            //TODO fix api to show id in more info
                            //TODO remove Veselica: pred title etc
                            party = new Party(date, title, href, "tmp");

                            mTextViewDate.setText(date);
                            mTextViewActors.setText(actors);
                            mTextViewLocation.setText(location);
                            mTextViewRegion.setText(region);
                            mTextViewAbout.setText(about);
                            JSONArray jsonArray = response.getJSONArray("videos");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String thumb = jsonObject.getString("thumbnail_url");
                                String videoTitle = jsonObject.getString("title");
                                String youtube = jsonObject.getString("html");
                                Video video = new Video(videoTitle, thumb, youtube);
                                mVideos.add(video);
                            }
                        }catch (Exception e){
                            Log.i(TAG, e.getLocalizedMessage());
                        }
                        mAdapter = new VideosAdapter(mVideos, DetailActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(jsonObjReq);
    }

    @Override
    public void trailersOnClick(int clickedItemIndex) {
        String MAIN_YOUTUBE = mVideos.get(clickedItemIndex).getVideoUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MAIN_YOUTUBE));
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        Toast.makeText(this, mVideos.get(clickedItemIndex).getVideoUrl(), Toast.LENGTH_SHORT).show();
    }

    public void favoriteEvent(View view) {
        if (party != null){
            //TODO check if its marked and make heart and other stuff
            PrefUtils.saveName(party.toString(), this);
        }
    }
}
