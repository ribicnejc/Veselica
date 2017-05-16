package com.ribic.nejc.veselica.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PartyDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "parties.db";
    private static final int DATABASE_VERSION = 1;

    public PartyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PARTY_TABLE =
                "CREATE TABLE " + PartyContract.PartyEntry.TABLE_NAME + " (" +
                        PartyContract.PartyEntry._ID                            + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
                        PartyContract.PartyEntry.COLUMN_PARTY_ID                + " INTEGER NOT NULL, "                     +
                        PartyContract.PartyEntry.COLUMN_PARTY_DATE              + " VARCHAR(500) NOT NULL, "                +
                        PartyContract.PartyEntry.COLUMN_PARTY_NAME              + " VARCHAR(500) NOT NULL, "                +
                        PartyContract.PartyEntry.COLUMN_PARTY_HREF              + " VARCHAR(500) NOT NULL, "                +
                        "UNIQUE (" + PartyContract.PartyEntry.COLUMN_PARTY_HREF + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_PARTY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartyContract.PartyEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
