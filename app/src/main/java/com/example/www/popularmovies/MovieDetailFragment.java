package com.example.www.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.www.popularmovies.Model.MovieData;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "movie_data";
    private MovieData movieData;
    @Bind(R.id.movie_title)
    TextView movieTitle;
    @Bind(R.id.movie_release_date)
    TextView movieReleaseDate;
    @Bind(R.id.movie_vote_avg)
    TextView movieVoteAvg;
    @Bind(R.id.movie_synopsis)
    TextView movieSynopsis;
    @Bind(R.id.movie_detail_image)
    ImageView movieImage;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            movieData = (MovieData) getArguments().getSerializable(ARG_ITEM);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Movie Detail");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        movieTitle.setText(movieData.getTitle());
        movieReleaseDate.setText("Release: " + movieData.getReleaseDate());
        movieSynopsis.setText(movieData.getOverview());
        movieVoteAvg.setText("Rating : "+movieData.getVoteAverage()+"/"+"10.0");
        String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String imagePath = movieData.getPosterPath();
        Log.d("MOVIE_APP", "Detail Fragment Image: " + imageBaseUrl + imagePath);
        Picasso.with(getContext()).load(imageBaseUrl + imagePath).into(movieImage);
        return rootView;
    }
}
