package com.ribic.nejc.veselica.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class PartyProvider extends ContentProvider{

    public static final int CODE_PARTY = 100;
    public static final int CODE_PARTY_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private PartyDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PartyContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PartyContract.PATH_PARTY, CODE_PARTY);
        matcher.addURI(authority, PartyContract.PATH_PARTY + "/#", CODE_PARTY_WITH_ID);
        return matcher;
    }




    @Override
    public boolean onCreate() {
        mOpenHelper = new PartyDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case CODE_PARTY_WITH_ID: {
                String id = uri.getLastPathSegment();
                String[] selectionArgs2 = new String[]{id};
                cursor = mOpenHelper.getReadableDatabase().query(PartyContract.PartyEntry.TABLE_NAME,
                        projection,
                        PartyContract.PartyEntry.COLUMN_PARTY_ID + " = ? ",
                        selectionArgs2,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_PARTY: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        PartyContract.PartyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case CODE_PARTY:
                db.beginTransaction();
                int rowsInserted = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(PartyContract.PartyEntry.TABLE_NAME, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)){
            case CODE_PARTY:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        PartyContract.PartyEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_PARTY_WITH_ID:
                String id = uri.getLastPathSegment();
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        PartyContract.PartyEntry.TABLE_NAME,
                        "movie_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
