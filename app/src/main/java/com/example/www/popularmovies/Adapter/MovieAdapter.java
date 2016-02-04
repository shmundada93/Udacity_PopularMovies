package com.example.www.popularmovies.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.www.popularmovies.Model.MovieData;
import com.example.www.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shmundada on 19-12-2015.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MovieData> mMovieList;

    public MovieAdapter(Context c, ArrayList<MovieData> movieList) {
        mContext = c;
        mMovieList = movieList;
    }

    public int getCount() {
        return mMovieList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_movie_item, parent, false);
            }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_image);
        //imageView.setLayoutParams(new GridView.LayoutParams(120, 180));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String imagePath = mMovieList.get(position).getPosterPath();
        Picasso.with(mContext).load(imageBaseUrl + imagePath).into(imageView);
        return imageView;
    }

}
