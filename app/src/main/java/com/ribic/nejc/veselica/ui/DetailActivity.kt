package com.ribic.nejc.veselica.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ribicnejc.party.R
import com.ribic.nejc.veselica.adapters.VideosAdapter
import com.ribic.nejc.veselica.fragments.MainEventsFragment
import com.ribic.nejc.veselica.objects.Party
import com.ribic.nejc.veselica.objects.Video
import com.ribic.nejc.veselica.utils.NetworkUtils
import com.ribic.nejc.veselica.utils.PrefUtils

import org.json.JSONArray
import org.json.JSONObject

import java.util.ArrayList

import android.view.View.GONE

class DetailActivity : AppCompatActivity(), VideosAdapter.TrailersAdapterOnClickHandler {

    val TAG = "nejc"//DetailActivity.this.getSimpleName();
    lateinit var mTextViewDate: TextView
    lateinit var mTextViewLocation: TextView
    lateinit var mTextViewRegion: TextView
    lateinit var mTextViewActors: TextView
    lateinit var mTextViewAbout: TextView
    lateinit var mTextViewError: TextView
    lateinit var mVideos: ArrayList<Video>
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: VideosAdapter
    lateinit var mLayout: LinearLayout
    lateinit var mProgressBarMain: ProgressBar
    lateinit var mProgressBarVideos: ProgressBar
    lateinit var mImageViewFavorite: ImageView
    lateinit var mImageViewError: ImageView
    var party: Party? = null
    lateinit var mSnackBar: Snackbar
    var href = ""
    var errorOccured = false
    private var stared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (!intent.hasExtra(MainEventsFragment.EXTRA_HREF)) {
            return
        }

        href = intent.getStringExtra(MainEventsFragment.EXTRA_HREF)
        mTextViewDate = findViewById(R.id.text_view_date) as TextView
        mTextViewActors = findViewById(R.id.text_view_actors) as TextView
        mTextViewLocation = findViewById(R.id.text_view_location) as TextView
        mTextViewAbout = findViewById(R.id.text_view_about) as TextView
        mTextViewRegion = findViewById(R.id.text_view_region) as TextView
        mRecyclerView = findViewById(R.id.recycler_view_item) as RecyclerView
        mLayout = findViewById(R.id.linear_layout_detail) as LinearLayout
        mProgressBarMain = findViewById(R.id.progress_bar_main) as ProgressBar
        mProgressBarVideos = findViewById(R.id.progress_bar_videos) as ProgressBar
        mImageViewFavorite = findViewById(R.id.image_view_favorite) as ImageView
        mTextViewError = findViewById(R.id.text_view_error_detail_no_videos) as TextView
        mImageViewError = findViewById(R.id.image_view_sad_smile) as ImageView
        mLayout.visibility = GONE

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView.layoutManager = layoutManager

        mTextViewActors.movementMethod = ScrollingMovementMethod()
        mTextViewAbout.movementMethod = ScrollingMovementMethod()

        mSnackBar = Snackbar.make(mLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE)

        onRefresh()

    }

    private fun onRefresh() {
        if (!NetworkUtils.networkUp(this)) {
            errorOccured = false
            mSnackBar = Snackbar.make(mLayout, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
            mProgressBarMain.visibility = GONE
            mSnackBar.setAction(R.string.try_again) { onRefresh() }
            mSnackBar.show()
            mImageViewError.visibility = View.VISIBLE
        } else if (errorOccured) {
            errorOccured = false
            mSnackBar = Snackbar.make(mLayout, R.string.error_occurred, Snackbar.LENGTH_INDEFINITE)
            mProgressBarMain.visibility = GONE
            mSnackBar.setAction(R.string.try_again) { onRefresh() }
            mSnackBar.show()
            mImageViewError.visibility = View.VISIBLE
        } else {
            mProgressBarMain.visibility = View.VISIBLE
            mSnackBar.dismiss()
            fetchData(href)
        }

    }

    private fun checkFavorite() {
        if (party != null) {
            if (PrefUtils.exitsts(party!!.toString(), this))
                mImageViewFavorite.setImageResource(R.drawable.icon_stared)
            else
                mImageViewFavorite.setImageResource(R.drawable.icon_unstared)
        }
    }

    private fun fetchData(href: String) {
        mProgressBarMain.visibility = View.VISIBLE
        mProgressBarVideos.visibility = View.VISIBLE
        mLayout.visibility = View.INVISIBLE
        mImageViewError.visibility = GONE
        val url = NetworkUtils.getUrlMoreInfo(href)
        val mRequestQueue = Volley.newRequestQueue(applicationContext)
        val jsonObjReq = JsonObjectRequest(Request.Method.GET,
                url, null,
                Response.Listener<JSONObject> { response ->
                    mVideos = ArrayList<Video>()
                    try {
                        val title = response.getString("title")
                        val date = response.getString("date")
                        val actors = response.getString("actors")
                        val location = response.getString("location")
                        val region = response.getString("region")
                        val about = response.getString("about")
                        val id = response.getInt("id").toString() + ""
                        //setTitle(title);
                        val tit = title.split(": ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        setTitle(actors)
                        party = Party(date, tit, href, id)

                        mTextViewDate.text = date
                        mTextViewActors.text = actors
                        mTextViewLocation.text = location
                        mTextViewRegion.text = region
                        mTextViewAbout.text = about
                        val jsonArray = response.getJSONArray("videos")
                        for (i in 0..jsonArray.length() - 1) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val thumb = jsonObject.getString("thumbnail_url")
                            val videoTitle = jsonObject.getString("title")
                            val youtube = jsonObject.getString("html")
                            val video = Video(videoTitle, thumb, youtube)
                            mVideos.add(video)
                        }
                    } catch (e: Exception) {
                        errorOccured = true
                        Log.i(TAG, e.message)
                        mProgressBarMain.visibility = GONE
                        mLayout.visibility = GONE
                        mImageViewError.visibility = View.VISIBLE
                        onRefresh()
                    }

                    mImageViewError.visibility = GONE
                    mAdapter = VideosAdapter(mVideos, this@DetailActivity)
                    mRecyclerView.adapter = mAdapter
                    mProgressBarMain.visibility = GONE
                    mLayout.visibility = View.VISIBLE
                    mProgressBarVideos.visibility = GONE
                    checkFavorite()
                    if (mVideos.size == 0) {
                        mRecyclerView.visibility = GONE
                        mTextViewError.visibility = View.VISIBLE
                    } else {
                        mRecyclerView.visibility = View.VISIBLE
                        mTextViewError.visibility = View.INVISIBLE
                    }
                }, Response.ErrorListener {
            mProgressBarMain.visibility = GONE
            mLayout.visibility = GONE
            mImageViewError.visibility = View.VISIBLE
            onRefresh()
        })
        mRequestQueue.add(jsonObjReq)
    }

    override fun trailersOnClick(clickedItemIndex: Int) {
        val MAIN_YOUTUBE = mVideos[clickedItemIndex].videoUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MAIN_YOUTUBE))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    fun favoriteEvent(view: View) {
        if (party != null) {
            stared = !stared
            if (PrefUtils.exitsts(party!!.toString(), this)) {
                PrefUtils.remove(party!!.toString(), this)
                mImageViewFavorite.setImageResource(R.drawable.icon_unstared)
            } else {
                PrefUtils.saveName(party!!.toString(), this)
                mImageViewFavorite.setImageResource(R.drawable.icon_stared)
                Toast.makeText(this, R.string.toast_notify_event_when_favorited, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (stared) {
            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
            super.onBackPressed()
        }
        super.onBackPressed()
    }
}
