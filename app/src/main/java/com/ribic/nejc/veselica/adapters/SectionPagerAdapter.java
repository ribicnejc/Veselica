package com.ribic.nejc.veselica.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ribicnejc.party.R;
import com.ribic.nejc.veselica.fragments.FavoriteEventsFragment;
import com.ribic.nejc.veselica.fragments.MainEventsFragment;


public class SectionPagerAdapter {

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        Context context;
        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainEventsFragment (defined as a static inner class below).
            if (position == 1) {
                return new FavoriteEventsFragment().newInstance(position + 1);
            } else
                return new MainEventsFragment().newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getResources().getString(R.string.page_main);
                case 1:
                    return context.getResources().getString(R.string.page_favorites);
                case 2:
                    return "Others";
            }
            return null;
        }
    }
}
