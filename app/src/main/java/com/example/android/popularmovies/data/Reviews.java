package com.example.android.popularmovies.data;

/**
 * Created by intel on 9/2/2017.
 */

public class Reviews {

    private String mReviewer;

    private String mReview;

    public Reviews(String reviewer,String review) {
        mReviewer = reviewer;
        mReview = review;
    }

    public String getmReviewer() {
        return mReviewer;
    }

    public String getmReview() {
        return mReview;
    }
}
