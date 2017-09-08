package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by intel on 9/6/2017.
 */

public class MovieContract {

    private MovieContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITES = "favourites";

    public static final class MovieEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_FAVOURITES);

        public static final String TABLE_NAME = "favourites";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_MOVIE_NAME = "name";

        public static final String COLUMN_MOVIE_RELEASE = "release";

        public static final String COLUMN_MOVIE_RATING = "rating";

        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        public static final String COLUMN_MOVIE_POSTER = "poster";
    }
}
