package com.ribic.nejc.veselica.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ribicnejc.party.R
import com.ribic.nejc.veselica.adapters.FavoriteAdapter
import com.ribic.nejc.veselica.objects.Party
import com.ribic.nejc.veselica.ui.DetailActivity
import com.ribic.nejc.veselica.utils.PrefUtils

import java.util.ArrayList

import android.app.Activity.RESULT_OK

class FavoriteEventsFragment : Fragment(), FavoriteAdapter.FavoriteAdapterOnClickHandler, SwipeRefreshLayout.OnRefreshListener {
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: FavoriteAdapter
    lateinit var mParties: ArrayList<Party>
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mTextViewError: TextView

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    fun newInstance(sectionNumber: Int): FavoriteEventsFragment {
        val fragment = FavoriteEventsFragment()
        val args = Bundle()
        args.putInt(ARG_SECTION_NUMBER, sectionNumber)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        mRecyclerView = rootView.findViewById(R.id.recycler_view_favorites) as RecyclerView
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_favorites) as SwipeRefreshLayout
        mTextViewError = rootView.findViewById(R.id.text_view_error_favorite) as TextView
        mSwipeRefreshLayout.setOnRefreshListener(this)


        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorSwipeRefresh1),
                ContextCompat.getColor(context, R.color.colorSwipeRefresh2),
                ContextCompat.getColor(context, R.color.colorSwipeRefresh3)
        )

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = layoutManager


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val party = mParties.removeAt(viewHolder.adapterPosition)
                PrefUtils.remove(party.toString(), context)
                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                checkIfEmpty()
                //TODO notify all data even on the other fragment

            }
        }).attachToRecyclerView(mRecyclerView)


        onRefresh()
        return rootView
    }

    fun readData() {
        mSwipeRefreshLayout.isRefreshing = true
        val elts = ArrayList<Party>()
        val tmp = PrefUtils.getNames(context)
        for (elt in tmp) {
            val tmp2 = elt.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val date = tmp2[0]
            val place = tmp2[1]
            val href = tmp2[2]
            val id = tmp2[3]
            val party = Party(date, place, href, id)
            elts.add(party)
        }
        mParties = elts

        checkIfEmpty()

        mAdapter = FavoriteAdapter(elts, this)
        mRecyclerView.adapter = mAdapter
        mSwipeRefreshLayout.isRefreshing = false

    }

    override fun partyOnClick(clickedItemIndex: Int) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(MainEventsFragment.EXTRA_HREF, mParties[clickedItemIndex].href)
        startActivityForResult(intent, CHECK_FOR_CHANGE_REQUEST)
    }

    override fun onRefresh() {
        readData()
    }


    private fun checkIfEmpty() {
        if (mParties.size == 0) {
            mTextViewError.visibility = View.VISIBLE
        } else
            mTextViewError.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHECK_FOR_CHANGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                readData()
            }
        }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        //        if (Constants.fragmentContentChanged) {
        //            Constants.fragmentContentChanged = false;
        //            readData();
        //        }
        super.setUserVisibleHint(isVisibleToUser)
    }

    companion object {

        val CHECK_FOR_CHANGE_REQUEST = 2
        private val ARG_SECTION_NUMBER = "section_number"
    }
}
