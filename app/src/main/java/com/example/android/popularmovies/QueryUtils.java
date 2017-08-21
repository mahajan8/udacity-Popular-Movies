package com.example.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by intel on 8/19/2017.
 */

final public class QueryUtils {

    private QueryUtils() {
    }

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    private static List<Movie> extractFromJSON(String movieJSON) {

        if(TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();


        try{

            JSONObject base = new JSONObject(movieJSON);
            JSONArray results = base.getJSONArray("results");
            for(int i=0;i<results.length();i++) {
                JSONObject current = results.getJSONObject(i);

                String title = current.getString("title");
                String posterPath = current.getString("poster_path");
                String release = current.getString("release_date");
                String overview = current.getString("overview");
                double rating = current.getDouble("vote_average");

                movies.add(new Movie(title,overview,release,rating,posterPath));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG,"Problem parsing JSON");
        }

        return movies;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Problem building URL",e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url==null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG,"Error Response Code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem retrieving data from results", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
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
        return output.toString();
    }

    public static List<Movie> fetchData (String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem making HTTP request", e);
        }

        List<Movie> movies = extractFromJSON(jsonResponse);

        return movies;
    }
}
