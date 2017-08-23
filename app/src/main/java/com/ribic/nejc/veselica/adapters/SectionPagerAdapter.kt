package com.ribic.nejc.veselica.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.ribicnejc.party.R
import com.ribic.nejc.veselica.fragments.FavoriteEventsFragment
import com.ribic.nejc.veselica.fragments.MainEventsFragment


class SectionPagerAdapter {

    class SectionsPagerAdapter(fm: FragmentManager, internal var context: Context) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainEventsFragment (defined as a static inner class below).
            if (position == 1) {
                return FavoriteEventsFragment().newInstance(position + 1)
            } else
                return MainEventsFragment().newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return context.resources.getString(R.string.page_main)
                1 -> return context.resources.getString(R.string.page_favorites)
                2 -> return "Others"
            }
            return null
        }
    }
}
