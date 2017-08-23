package com.ribic.nejc.veselica.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri


class PartyProvider : ContentProvider() {

    private var mOpenHelper: PartyDbHelper? = null


    override fun onCreate(): Boolean {
        mOpenHelper = PartyDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor: Cursor
        when (sUriMatcher.match(uri)) {
            CODE_PARTY_WITH_ID -> {
                val id = uri.lastPathSegment
                val selectionArgs2 = arrayOf(id)
                cursor = mOpenHelper!!.readableDatabase.query(PartyContract.PartyEntry.TABLE_NAME,
                        projection,
                        PartyContract.PartyEntry.COLUMN_PARTY_ID + " = ? ",
                        selectionArgs2, null, null,
                        sortOrder)
            }
            CODE_PARTY -> {
                cursor = mOpenHelper!!.readableDatabase.query(
                        PartyContract.PartyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder)
            }
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }

        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mOpenHelper!!.writableDatabase
        when (sUriMatcher.match(uri)) {
            CODE_PARTY -> {
                db.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {
                        val _id = db.insert(PartyContract.PartyEntry.TABLE_NAME, null, value)
                        if (_id.toInt() != -1) rowsInserted++
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                if (rowsInserted > 0) context!!.contentResolver.notifyChange(uri, null)
                return rowsInserted
            }
            else -> return super.bulkInsert(uri, values)
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        val numRowsDeleted: Int
        if (null == selection) selection = "1"
        when (sUriMatcher.match(uri)) {
            CODE_PARTY -> numRowsDeleted = mOpenHelper!!.writableDatabase.delete(
                    PartyContract.PartyEntry.TABLE_NAME,
                    selection,
                    selectionArgs)
            CODE_PARTY_WITH_ID -> {
                val id = uri.lastPathSegment
                numRowsDeleted = mOpenHelper!!.writableDatabase.delete(
                        PartyContract.PartyEntry.TABLE_NAME,
                        "movie_id=?",
                        arrayOf(id))
            }
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
        if (numRowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return numRowsDeleted
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun shutdown() {
        mOpenHelper!!.close()
        super.shutdown()
    }

    companion object {

        val CODE_PARTY = 100
        val CODE_PARTY_WITH_ID = 101

        private val sUriMatcher = buildUriMatcher()

        fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = PartyContract.CONTENT_AUTHORITY

            matcher.addURI(authority, PartyContract.PATH_PARTY, CODE_PARTY)
            matcher.addURI(authority, PartyContract.PATH_PARTY + "/#", CODE_PARTY_WITH_ID)
            return matcher
        }
    }
}
