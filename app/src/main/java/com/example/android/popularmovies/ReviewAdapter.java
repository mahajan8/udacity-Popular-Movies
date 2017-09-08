package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.Reviews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by intel on 9/2/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Reviews> reviews = new ArrayList<>();

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.review_list_layout,parent,false);

        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        final Reviews review = reviews.get(position);

        holder.mReviewer.setText(review.getmReviewer());
        holder.mReview.setText(review.getmReview());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView mReviewer;

        TextView mReview;

        public ReviewViewHolder (View itemView) {
            super(itemView);

            mReviewer = (TextView) itemView.findViewById(R.id.review_name);
            mReview = (TextView) itemView.findViewById(R.id.review);
        }
    }

    public void setReviews(List<Reviews> list) {

        reviews.clear();

        if(list != null && !list.isEmpty()) {
            reviews.addAll(list);
            notifyDataSetChanged();
        }
    }
}
