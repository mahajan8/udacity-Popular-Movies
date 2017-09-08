package com.example.android.popularmovies.data;


/**
 * Created by intel on 8/19/2017.
 */

public class Movie {

    private String mTitle;

    private String mOverview;

    private String mReleaseDate;

    private double mAverageRating;

    private String mPosterPath;

    private long mId;

    private String Image_URL = "https://image.tmdb.org/t/p/w500";

    public Movie (long id,String title, String overview, String release, double rating, String poster) {
        mTitle = title;
        mOverview = overview;
        mReleaseDate = release;
        mAverageRating = rating;
        mPosterPath = poster;
        mId = id;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmOverview() {
        return mOverview;
    }

    public double getmAverageRating() {
        return mAverageRating;
    }

    public String getmPosterPath() {
        return Image_URL + mPosterPath;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public long getmId() {
        return mId;
    }
}
