package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int ten = 10;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String title = bundle.getString("title");
        String release = bundle.getString("release");
        String overview = bundle.getString("overview");
        String poster = bundle.getString("poster");
        double rating = bundle.getDouble("rating");

        ImageView posterView = (ImageView) findViewById(R.id.poster_image);
        TextView ratingView = (TextView) findViewById(R.id.rating_text_view);
        TextView synopsisView = (TextView) findViewById(R.id.synopsis_text_view);
        TextView titleView = (TextView) findViewById(R.id.title_text_view);
        TextView releaseDateView = (TextView) findViewById(R.id.release_date_text_view);

        Picasso.with(DetailActivity.this)
                .load(poster)
                .into(posterView);

        titleView.setText(title);
        ratingView.setText(String.valueOf(rating) + "/" + String.valueOf(ten));
        synopsisView.setText(overview);
        releaseDateView.setText(release);

    }
}
