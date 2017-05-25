package com.ribic.nejc.veselica.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ribic.nejc.veselica.fragments.PlaceholderFragment;


public class SectionPagerAdapter {
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //TODO if position is favorites, call new fragment
            return new PlaceholderFragment().newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Main list";
                case 1:
                    return "Favorites";
                case 2:
                    return "Others";
            }
            return null;
        }
    }
}
