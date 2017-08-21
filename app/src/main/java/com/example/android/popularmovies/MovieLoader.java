package com.example.android.popularmovies;

import android.content.Context;
import android.content.AsyncTaskLoader;

import com.example.android.popularmovies.data.Movie;

import java.util.List;

/**
 * Created by intel on 8/19/2017.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>>{

    private String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);

        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if(mUrl == null) {
            return null;
        }

        List<Movie> movies = QueryUtils.fetchData(mUrl);
        return movies;
    }
}
