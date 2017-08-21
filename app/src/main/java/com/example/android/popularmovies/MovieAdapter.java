package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ListItemClickListener mOnClickListener;

    private List<Movie> movies= new ArrayList<>();

    public interface ListItemClickListener {
        void OnItemClick(Movie movie);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem,parent,false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        final Movie movie = movies.get(position);
        View itemView = holder.itemView;


        Picasso.with(itemView.getContext())
                .load(movie.getmPosterPath())
                .into(holder.movieItemImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener != null) {
                    mOnClickListener.OnItemClick(movie);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView movieItemImage;

        public MovieViewHolder(View itemView) {
            super(itemView);

            movieItemImage = (ImageView) itemView.findViewById(R.id.image_movie);
        }
    }

    public void setMovies(List<Movie> list) {
        movies.clear();

        if(list != null && !list.isEmpty()) {
            movies.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        movies.clear();
    }

    public void setListener(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

}
