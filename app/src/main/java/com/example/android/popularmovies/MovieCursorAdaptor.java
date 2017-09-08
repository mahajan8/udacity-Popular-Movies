package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

import static android.R.attr.start;

/**
 * Created by intel on 9/6/2017.
 */

public class MovieCursorAdaptor extends CursorAdapter {

    public MovieCursorAdaptor(Context context,Cursor c) {
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_grid_item,parent,false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        final int id =cursor.getInt(cursor.getColumnIndex(MovieEntry._ID));
        final String name = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_NAME));
        final String overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW));
        final double rating = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING));
        final String release = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE));
        final byte[] poster = cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER));

        ImageView moviePoster = (ImageView) view.findViewById(R.id.image_movie);

        Bitmap posterBitmap = getBitmap(poster);

        moviePoster.setImageBitmap(posterBitmap);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("title",name);
                bundle.putString("overview",overview);
                bundle.putString("release",release);
                bundle.putDouble("rating",rating);
                bundle.putByteArray("posterByte",poster);
                bundle.putString("poster",null);
                bundle.putLong("id",0);

                intent.putExtras(bundle);
                Uri movieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI,id);
                intent.setData(movieUri);
                context.startActivity(intent);
            }
        });

    }

    private Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }
}
