package com.ribic.nejc.veselica.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PartyDbHelper(context: Context) : SQLiteOpenHelper(context, PartyDbHelper.DATABASE_NAME, null, PartyDbHelper.DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val SQL_CREATE_PARTY_TABLE = "CREATE TABLE " + PartyContract.PartyEntry.TABLE_NAME + " (" +
                PartyContract.PartyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PartyContract.PartyEntry.COLUMN_PARTY_ID + " INTEGER NOT NULL, " +
                PartyContract.PartyEntry.COLUMN_PARTY_DATE + " VARCHAR(500) NOT NULL, " +
                PartyContract.PartyEntry.COLUMN_PARTY_NAME + " VARCHAR(500) NOT NULL, " +
                PartyContract.PartyEntry.COLUMN_PARTY_HREF + " VARCHAR(500) NOT NULL, " +
                "UNIQUE (" + PartyContract.PartyEntry.COLUMN_PARTY_HREF + ") ON CONFLICT REPLACE);"
        sqLiteDatabase.execSQL(SQL_CREATE_PARTY_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartyContract.PartyEntry.TABLE_NAME)
        onCreate(sqLiteDatabase)
    }

    companion object {
        val DATABASE_NAME = "parties.db"
        private val DATABASE_VERSION = 4
    }
}
