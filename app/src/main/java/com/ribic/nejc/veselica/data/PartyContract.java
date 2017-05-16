package com.ribic.nejc.veselica.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class PartyContract {

    public static final String CONTENT_AUTHORITY = "com.ribic.nejc.party";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PARTY = "party";

    public static final class PartyEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PARTY)
                .build();

        public static final String TABLE_NAME = "party";
        public static final String COLUMN_PARTY_ID = "party_id";
        public static final String COLUMN_PARTY_DATE = "party_date";
        public static final String COLUMN_PARTY_NAME = "party_name";
        public static final String COLUMN_PARTY_HREF = "party_href";

    }
}
