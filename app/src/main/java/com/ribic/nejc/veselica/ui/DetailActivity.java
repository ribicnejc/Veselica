package com.ribic.nejc.veselica.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ribic.nejc.party.R;
import com.ribic.nejc.veselica.fragments.PlaceholderFragment;
import com.ribic.nejc.veselica.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    public final String TAG = "nejc";//DetailActivity.this.getLocalClassName();
    public TextView mTextViewTitle;
    public TextView mTextViewDate;
    public TextView mTextViewLocation;
    public TextView mTextViewRegion;
    public TextView mTextViewActors;
    public TextView mTextViewAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String href = getIntent().getStringExtra(PlaceholderFragment.EXTRA_HREF);
        mTextViewTitle = (TextView) findViewById(R.id.text_view_title);
        mTextViewDate = (TextView) findViewById(R.id.text_view_date);
        mTextViewActors = (TextView) findViewById(R.id.text_view_actors);
        mTextViewLocation = (TextView) findViewById(R.id.text_view_location);
        mTextViewAbout = (TextView) findViewById(R.id.text_view_about);
        mTextViewRegion = (TextView) findViewById(R.id.text_view_region);
        fetchData(href);
    }

    private void fetchData(String href){
        String url = NetworkUtils.getUrlMoreInfo(href);
        final RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String title = response.getString("title");
                            String date = response.getString("date");
                            String actors = response.getString("actors");
                            String location = response.getString("location");
                            String region = response.getString("region");
                            String about = response.getString("about");

                            mTextViewTitle.setText(title);
                            mTextViewDate.setText(date);
                            mTextViewActors.setText(actors);
                            mTextViewLocation.setText(location);
                            mTextViewRegion.setText(region);
                            mTextViewAbout.setText(about);
                        }catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(jsonObjReq);
    }
}
