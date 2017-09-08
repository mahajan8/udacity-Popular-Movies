package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.os.Build.VERSION_CODES.M;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by intel on 9/6/2017.
 */

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final int MOVIES = 100;

    private static final int MOVIE_ID = 101;

    private static final UriMatcher sUri = new UriMatcher(UriMatcher.NO_MATCH);

    private MovieDbHelper mHelper;

    static {
        sUri.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_FAVOURITES, MOVIES);

        sUri.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_FAVOURITES + "/#", MOVIE_ID);
    }


    @Override
    public boolean onCreate() {
        mHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUri.match(uri);

        switch (match) {
            case MOVIES:
                cursor = database.query(MovieEntry.TABLE_NAME,projection,selection,
                        selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(MovieEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUri.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUri.match(uri);
        switch (match) {
            case MOVIES:
                return insertItem(uri,values);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri,ContentValues values) {

        String name = values.getAsString(MovieEntry.COLUMN_MOVIE_NAME);
        if(name==null) {
            throw new IllegalArgumentException("Movie has no name");
        }

        String overview = values.getAsString(MovieEntry.COLUMN_MOVIE_OVERVIEW);
        if(overview == null) {
            throw new IllegalArgumentException("Movie has no overview");
        }

        SQLiteDatabase database = mHelper.getWritableDatabase();

        long id = database.insert(MovieEntry.TABLE_NAME,null,values);

        if(id==-1) {
            Log.e(LOG_TAG,"Failed to insert row");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUri.match(uri);
        switch (match){
            case MOVIES:
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
