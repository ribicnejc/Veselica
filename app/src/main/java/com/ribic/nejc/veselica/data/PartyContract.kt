package com.ribic.nejc.veselica.data

import android.net.Uri
import android.provider.BaseColumns


object PartyContract {

    val CONTENT_AUTHORITY = "com.ribic.nejc.party"
    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
    val PATH_PARTY = "party"

    class PartyEntry : BaseColumns {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_PARTY)
                    .build()

            val _ID = BaseColumns._ID
            val TABLE_NAME = "party"
            val COLUMN_PARTY_ID = "party_id"
            val COLUMN_PARTY_DATE = "party_date"
            val COLUMN_PARTY_NAME = "party_name"
            val COLUMN_PARTY_HREF = "party_href"
        }

    }
}
