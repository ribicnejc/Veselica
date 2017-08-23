package com.ribic.nejc.veselica.fragments

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ribicnejc.party.R
import com.ribic.nejc.veselica.adapters.MainAdapter
import com.ribic.nejc.veselica.data.PartyContract
import com.ribic.nejc.veselica.objects.Party
import com.ribic.nejc.veselica.ui.DetailActivity
import com.ribic.nejc.veselica.utils.Constants
import com.ribic.nejc.veselica.utils.NetworkUtils

import org.json.JSONArray
import org.json.JSONObject

import java.util.ArrayList

import android.app.Activity.RESULT_OK


class MainEventsFragment : Fragment(), MainAdapter.MainAdapterOnClickHandler, SwipeRefreshLayout.OnRefreshListener {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mMainAdapter: MainAdapter
    var TAG = this@MainEventsFragment.tag
    lateinit var mTextViewError: TextView
    lateinit var mParties: ArrayList<Party>
    lateinit var mSnackbar: Snackbar

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    fun newInstance(sectionNumber: Int): MainEventsFragment {
        val fragment = MainEventsFragment()
        val args = Bundle()
        args.putInt(ARG_SECTION_NUMBER, sectionNumber)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)
        mRecyclerView = rootView.findViewById(R.id.rv_parties) as RecyclerView
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh) as SwipeRefreshLayout
        mTextViewError = rootView.findViewById(R.id.text_view_error) as TextView
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorSwipeRefresh1),
                ContextCompat.getColor(context, R.color.colorSwipeRefresh2),
                ContextCompat.getColor(context, R.color.colorSwipeRefresh3)
        )
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView.layoutManager = layoutManager

        mSnackbar = Snackbar.make(container!!, "No internet connection", Snackbar.LENGTH_LONG)


        onRefresh()
        return rootView
    }

    override fun onRefresh() {
        if (NetworkUtils.networkUp(context)) {
            mSwipeRefreshLayout.isRefreshing = true
            fetchData()
        } else {
            mSwipeRefreshLayout.isRefreshing = true
            mSnackbar.setAction(R.string.try_again) { onRefresh() }
            mSnackbar.show()
            readDataFromDatabase()
        }
    }


    override fun partyOnClick(clickedItemIndex: Int) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(EXTRA_HREF, mParties[clickedItemIndex].href)
        startActivityForResult(intent, CHECK_FOR_CHANGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHECK_FOR_CHANGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                readDataFromDatabase()
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        //        if (Constants.fragmentContentChanged) {
        //            Constants.fragmentContentChanged = false;
        //            readDataFromDatabase();
        //        }
        super.setUserVisibleHint(isVisibleToUser)
    }

    private fun fetchData() {
        val url = NetworkUtils.urlAll
        val queue = Volley.newRequestQueue(context)

        //mRecyclerView.setVisibility(View.INVISIBLE);
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener<JSONArray> { response ->
            val parties = ArrayList<Party>()
            val values = ArrayList<ContentValues>()
            try {
                for (i in 0..response.length() - 1) {
                    val jsonObject = response.getJSONObject(i)
                    val date = jsonObject.getString("date")
                    val jsonArray1 = jsonObject.getJSONArray("places")
                    for (j in 0..jsonArray1.length() - 1) {
                        val jsonObject1 = jsonArray1.getJSONObject(j)
                        val name = jsonObject1.getString("name")
                        val href = jsonObject1.getString("href")
                        val id = jsonObject1.getString("id")
                        val party = Party()
                        party.place = name
                        party.href = href
                        party.date = date
                        party.id = id
                        parties.add(party)

                        val contentValues = ContentValues()
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_DATE, party.date)
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_HREF, party.href)
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_ID, Integer.parseInt(party.id))
                        contentValues.put(PartyContract.PartyEntry.COLUMN_PARTY_NAME, party.place)
                        values.add(contentValues)
                    }
                }
                context.contentResolver.bulkInsert(PartyContract.PartyEntry.CONTENT_URI,
                        values.toTypedArray())
            } catch (e: Exception) {
                parties.clear()
                e.printStackTrace()
                Log.v(TAG, "problem appeared when parsing JSON")
            }


            if (parties.size != 0) {
                mTextViewError.visibility = View.GONE
                mRecyclerView.visibility = View.VISIBLE
                mParties = parties
                mMainAdapter = MainAdapter(parties, this@MainEventsFragment)
                mRecyclerView.adapter = mMainAdapter
                mSwipeRefreshLayout.isRefreshing = false
            } else {
                if (activity != null && isAdded) {
                    mSwipeRefreshLayout.isRefreshing = false
                    mTextViewError.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.INVISIBLE
                    mTextViewError.text = resources.getString(R.string.error_web_page_down)
                }
            }

            Log.v(TAG, "Volley JsonArray data parsed and saved to database")
        }, Response.ErrorListener {
            mSwipeRefreshLayout.isRefreshing = false
            mTextViewError.visibility = View.VISIBLE
            mRecyclerView.visibility = View.INVISIBLE
            mTextViewError.text = resources.getString(R.string.error_web_page_down)
        })
        queue.add(jsonArrayRequest)
    }

    fun readDataFromDatabase() {
        object : AsyncTask<Void, Void, Cursor>() {
            override fun onPreExecute() {
                mSwipeRefreshLayout.isRefreshing = true
                super.onPreExecute()
            }

            override fun doInBackground(vararg voids: Void): Cursor {
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(PartyContract.PartyEntry.CONTENT_URI, null, null, null, PartyContract.PartyEntry._ID)
                } catch (e: Exception) {
                    Log.v(TAG, "Failed to query data")
                    e.printStackTrace()
                }

                return cursor!!
            }

            override fun onPostExecute(cursor: Cursor?) {
                if (cursor == null) {
                    mSwipeRefreshLayout.isRefreshing = false
                    mTextViewError.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.INVISIBLE
                    mTextViewError.text = resources.getString(R.string.error_no_data_in_db)
                    return
                }
                val parties = ArrayList<Party>()
                if (cursor.moveToFirst()) {
                    do {
                        val date = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_DATE))
                        val href = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_HREF))
                        val id = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_ID))
                        val name = cursor.getString(cursor.getColumnIndex(PartyContract.PartyEntry.COLUMN_PARTY_NAME))
                        val party = Party(date, name, href, id)
                        parties.add(party)
                    } while (cursor.moveToNext())
                }
                cursor.close()
                mMainAdapter = MainAdapter(parties, this@MainEventsFragment)
                mSwipeRefreshLayout.isRefreshing = false
                mParties = parties
                mRecyclerView.adapter = mMainAdapter

                if (parties.size == 0) {
                    mSwipeRefreshLayout.isRefreshing = false
                    mTextViewError.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.INVISIBLE
                    mTextViewError.text = resources.getString(R.string.error_no_data_in_db)
                }
                super.onPostExecute(cursor)
            }
        }.execute()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val CHECK_FOR_CHANGE_REQUEST = 1
        val EXTRA_HREF = "com.nejc.ribic.veselica.href"
        private val ARG_SECTION_NUMBER = "section_number"
    }

}
