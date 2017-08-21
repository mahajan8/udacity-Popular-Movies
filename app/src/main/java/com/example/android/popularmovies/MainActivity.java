package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.data.Movie;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>>,SharedPreferences.OnSharedPreferenceChangeListener{

    private MovieAdapter mAdapter;

    private String API;

    private LoaderManager loaderManager;

    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API = "https://api.themoviedb.org/3/movie";

        RecyclerView mMoviesList = (RecyclerView) findViewById(R.id.movies_list);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mMoviesList.setLayoutManager(layoutManager);

        mMoviesList.setHasFixedSize(true);

        mAdapter = new MovieAdapter();
        mAdapter.setListener(this);

        mMoviesList.setAdapter(mAdapter);

        loaderManager = getLoaderManager();

        Loader<String> movieLoader = loaderManager.getLoader(LOADER_ID);

        if(movieLoader == null) {
            loaderManager.initLoader(LOADER_ID,null,this);
        } else {
            loaderManager.restartLoader(LOADER_ID,null,this);
        }
    }

    @Override
    public void OnItemClick(Movie movie) {

        if(movie!=null) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString("title",movie.getmTitle());
            bundle.putString("overview",movie.getmOverview());
            bundle.putString("release",movie.getmReleaseDate());
            bundle.putDouble("rating",movie.getmAverageRating());
            bundle.putString("poster",movie.getmPosterPath());

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sortBy = sharedPrefs.getString(
                getString(R.string.sort_by_key),
                getString(R.string.popularity_value));

        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        Uri baseUri = Uri.parse(API);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendPath(sortBy);
        uriBuilder.appendQueryParameter("api_key","61378087161ec06ca251637e4002e938");

        return new MovieLoader(this,uriBuilder.build().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        mAdapter.setMovies(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loaderManager.restartLoader(LOADER_ID,null,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
