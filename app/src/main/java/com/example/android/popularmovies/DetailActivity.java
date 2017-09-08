package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.Reviews;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class DetailActivity extends AppCompatActivity {

    private static String LOG_TAG = DetailActivity.class.getSimpleName();

    private ReviewAdapter mReviewAd;

    private String API;

    private TextView synopsisView;

    private TextView titleView;

    private TextView releaseDateView;

    private Uri mCurrentMovieUri;

    private String poster;
    private double rating;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        API = "https://api.themoviedb.org/3/movie";

        int ten = 10;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String title = bundle.getString("title");
        String release = bundle.getString("release");
        String overview = bundle.getString("overview");
        poster = bundle.getString("poster");
        rating = bundle.getDouble("rating");
        long id = bundle.getLong("id");

        byte[] posterBytes;

        mCurrentMovieUri = intent.getData();


        setTitle(title);

        ImageView posterView = (ImageView) findViewById(R.id.poster_image);
        TextView ratingView = (TextView) findViewById(R.id.rating_text_view);
        synopsisView = (TextView) findViewById(R.id.synopsis_text_view);
        titleView = (TextView) findViewById(R.id.title_text_view);
        releaseDateView = (TextView) findViewById(R.id.release_date_text_view);


        titleView.setText(title);
        ratingView.setText(String.valueOf(rating) + "/" + String.valueOf(ten));
        synopsisView.setText(overview);
        releaseDateView.setText(release);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView reviewList = (RecyclerView) findViewById(R.id.review_list);

        TextView reviewHeading = (TextView) findViewById(R.id.review_heading);

        if(mCurrentMovieUri==null) {

            reviewHeading.setVisibility(View.VISIBLE);

            Picasso.with(DetailActivity.this)
                    .load(poster)
                    .into(posterView);

            mReviewAd = new ReviewAdapter();

            reviewList.setLayoutManager(layoutManager);

            reviewList.setAdapter(mReviewAd);

            final URL reviewUrl = createUrl(id, "reviews");

            final URL trailerUrl = createUrl(id, "videos");

            URL[] params = {reviewUrl,trailerUrl};

            new ReviewAsyncTask().execute(params);
            new TrailerAsyncTask().execute(params);

            posterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + key);
                    Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
            });

        } else {

            reviewHeading.setVisibility(View.INVISIBLE);

            posterBytes = bundle.getByteArray("posterByte");

            posterView.setImageBitmap(getBitmap(posterBytes));
        }

    }

    private URL createUrl(long id,String choice) {

        Uri base = Uri.parse(API);
        Uri.Builder builder = base.buildUpon();

        builder.appendPath(String.valueOf(id));
        builder.appendPath(choice);
        builder.appendQueryParameter("api_key","61378087161ec06ca251637e4002e938");

        URL url = null;

        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Error creating URL");
        }

        return url;
    }

    private String makeRequest(URL url) throws IOException {

        String response = "";

        if(url==null) {
            return response;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode()==200) {
                inputStream = httpURLConnection.getInputStream();

                StringBuilder output = new StringBuilder();
                if(inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    while (line != null) {
                        output.append(line);
                        line = reader.readLine();
                    }
                }

                response = output.toString();

            } else {
                Log.e(LOG_TAG,"Error Response Code" + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem retreiving results");
        } finally {
            if(httpURLConnection!=null) {
                httpURLConnection.disconnect();
            }
            if(inputStream!=null) {
                inputStream.close();
            }
        }
        return response;
    }

    private List<Reviews> extractReviews (String response) {

        if(TextUtils.isEmpty(response)) {
            return null;
        }

        List<Reviews> reviews = new ArrayList<>();

        try {

            JSONObject base = new JSONObject(response);
            JSONArray results = base.getJSONArray("results");

            for(int i=0;i<results.length();i++) {
                JSONObject current = results.getJSONObject(i);

                String author = current.getString("author");
                String content = current.getString("content");

                reviews.add(new Reviews(author,content.trim()));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG,"Problem parsing JSON");
        }

        return reviews;
    }

    class ReviewAsyncTask extends AsyncTask<URL,Void,List<Reviews>> {

        @Override
        protected List<Reviews> doInBackground(URL... params) {

            String response = "";
            try{
                response = makeRequest(params[0]);
            } catch(IOException e) {
                Log.e(LOG_TAG,"Problem getting response");
            }

            return extractReviews(response);
        }

        @Override
        protected void onPostExecute(List<Reviews> reviewses) {
            super.onPostExecute(reviewses);

            mReviewAd.setReviews(reviewses);
        }
    }

    class TrailerAsyncTask extends AsyncTask<URL,Void,String> {

        @Override
        protected String doInBackground(URL... params) {

            String response = "";
            try{
                response = makeRequest(params[1]);
            } catch(IOException e) {
                Log.e(LOG_TAG,"Problem getting response");
            }

            return extractKey(response);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            key = s;
        }
    }

    private String extractKey(String response) {
        if(TextUtils.isEmpty(response)) {
            return null;
        }

        String vidKey = "";

        try{
            JSONObject base = new JSONObject(response);
            JSONArray videos = base.getJSONArray("results");

            JSONObject trailer = videos.getJSONObject(0);
            vidKey = trailer.getString("key");

        } catch (JSONException e) {
            Log.e(LOG_TAG,"Problem parsing JSON");
        }

        return vidKey;
    }

    private void saveMovie() {

        String nameString = titleView.getText().toString().trim();
        String releaseString = releaseDateView.getText().toString().trim();
        String ratingString = String.valueOf(rating);
        String overviewString = synopsisView.getText().toString().trim();
        Bitmap posterBit = getBitmapFromURL(poster);

        byte[] posterBytes = getBytes(posterBit);

        ContentValues values = new ContentValues();

        values.put(MovieEntry.COLUMN_MOVIE_NAME,nameString);
        values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW,overviewString);
        values.put(MovieEntry.COLUMN_MOVIE_RATING,Double.valueOf(ratingString));
        values.put(MovieEntry.COLUMN_MOVIE_RELEASE,releaseString);
        values.put(MovieEntry.COLUMN_MOVIE_POSTER,posterBytes);

        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI,values);

        if(newUri == null) {
            Toast.makeText(this,R.string.error_save,
                    Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, R.string.saved,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMovie();
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMovie() {
        int rowsDeleted = getContentResolver().delete(mCurrentMovieUri,null,null);

        if(rowsDeleted == 0) {
            Toast.makeText(this, R.string.error_delete, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

        return stream.toByteArray();
    }

    private Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        if(mCurrentMovieUri==null) {
            menu.findItem(R.id.fav).setIcon(R.drawable.btn_star_big_off);
            menu.findItem(R.id.fav).setChecked(false);
        }else {
            menu.findItem(R.id.fav).setIcon(R.drawable.btn_star_big_on);
            menu.findItem(R.id.fav).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.fav) {
            if (item.isChecked()) {
                showDeleteConfirmationDialog();
                item.setChecked(false);
                item.setIcon(R.drawable.btn_star_big_off);
            }else {
                saveMovie();
                item.setChecked(true);
                item.setIcon(R.drawable.btn_star_big_on);
                item.setEnabled(false);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error getting image");
            return null;
        }
    }
}
