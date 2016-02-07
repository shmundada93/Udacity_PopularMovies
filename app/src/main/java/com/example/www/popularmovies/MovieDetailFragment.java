package com.example.www.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.www.popularmovies.Adapter.ReviewAdapter;
import com.example.www.popularmovies.Adapter.TrailerAdapter;
import com.example.www.popularmovies.Model.MoviedbAPI;
import com.example.www.popularmovies.Model.Review;
import com.example.www.popularmovies.Model.ReviewList;
import com.example.www.popularmovies.Model.Trailer;
import com.example.www.popularmovies.Model.TrailerList;
import com.example.www.popularmovies.UI.NonScrollListView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
    private Retrofit retrofit;
    private MoviedbAPI moviedbAPI;
    private Integer movieId;
    private ArrayList<Trailer> allTrailers = new ArrayList<Trailer>();
    private ArrayList<Review> allReviews = new ArrayList<Review>();
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private ShareActionProvider mShareActionProvider;
    private static final String BASE_URL = "http://api.themoviedb.org";
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
    @Bind(R.id.trailerList)
    NonScrollListView trailerListView;
    @Bind(R.id.reviewList)
    NonScrollListView reviewListView;
    @Bind(R.id.fav_button)
    ImageButton favButton;

    @OnClick(R.id.fav_button)
    public void addToFav(){
        MovieData dbMovie = SQLite.select().from(MovieData.class)
                .where(MovieData_Table.id.eq(movieId)).querySingle();
        if(dbMovie==null){
            movieData.save();
            favButton.setImageResource(R.drawable.ic_favorite_red_500_48dp);
        }
        else{
            movieData.delete();
            favButton.setImageResource(R.drawable.ic_favorite_outline_red_500_48dp);
        }
    }

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            movieData = (MovieData) getArguments().getSerializable(ARG_ITEM);
        }
        movieId = movieData.getId();
        setHasOptionsMenu(true);
        reviewAdapter = new ReviewAdapter(getContext(), allReviews);
        trailerAdapter = new TrailerAdapter(getContext(),allTrailers);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        moviedbAPI = retrofit.create(MoviedbAPI.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        movieTitle.setText(movieData.getTitle());
        try{
        movieReleaseDate.setText(movieData.getReleaseDate().substring(0, 4));}
        catch(Exception e){
            movieReleaseDate.setText("");
        }
        movieSynopsis.setText(movieData.getOverview());
        movieVoteAvg.setText(movieData.getVoteAverage().toString());
        reviewListView.setAdapter(reviewAdapter);
        trailerListView.setAdapter(trailerAdapter);
        String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String imagePath = movieData.getPosterPath();
        Log.d("MOVIE_APP", "Detail Fragment Image: " + imageBaseUrl + imagePath);
        Picasso.with(getContext()).load(imageBaseUrl + imagePath).into(movieImage);
        initFavButton();
        setTrailers();
        setReviews();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_item_share, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item) ;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch trailer of " + movieData.getTitle());
        shareIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(shareIntent);
    }

    private void setShareIntent() {
        Log.d("MOVIE_APP", "Set share intent");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch trailer of " + movieData.getTitle()
                + " https://www.youtube.com/watch?v=" + allTrailers.get(0).getKey());
        if (mShareActionProvider != null) {
            Log.d("MOVIE_APP", "Set share intent action");
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void initFavButton(){
        MovieData dbMovie = SQLite.select().from(MovieData.class)
                .where(MovieData_Table.id.eq(movieId)).querySingle();
        if(dbMovie==null){
            favButton.setImageResource(R.drawable.ic_favorite_outline_red_500_48dp);

        }
        else{
            favButton.setImageResource(R.drawable.ic_favorite_red_500_48dp);
        }
    }

    private void setTrailers(){
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri youtube = Uri.parse("https://www.youtube.com/watch?v="+allTrailers.get(i).getKey());
                Intent intent = new Intent(Intent.ACTION_VIEW,youtube);
                startActivity(intent);
            }
        });
        Call<TrailerList> call = moviedbAPI.getTrailers(movieId);
        Log.d("MOVIE_APP", "Setting Trailers for movie: " +  movieId);
        call.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Response<TrailerList> response, Retrofit retrofit) {
                try {
                    allTrailers.clear();
                    allTrailers.addAll(response.body().getResults());
                    trailerAdapter.notifyDataSetChanged();
                    if(!allTrailers.isEmpty())
                        setShareIntent();
                } catch (Exception e) {
                    Log.d("MOVIE_APP", "Error in (Response) getting Trailer for movie Id: " + movieId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MOVIE_APP", "Error in getting Trailer for movie Id: " + movieId);
            }
        });
    }

    private void setReviews(){
        Call<ReviewList> call = moviedbAPI.getReviews(movieId);
        Log.d("MOVIE_APP", "Setting Reviews for movie: " + movieId);
        call.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Response<ReviewList> response, Retrofit retrofit) {
                try {
                    allReviews.clear();
                    allReviews.addAll(response.body().getResults());
                    reviewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("MOVIE_APP", "Error in (Response) getting reviews for movie Id: " + movieId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MOVIE_APP", "Error in getting reviews for movie Id: " + movieId);
            }
        });


    }
}
