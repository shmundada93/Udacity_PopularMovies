package com.example.www.popularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.www.popularmovies.Adapter.MovieAdapter;
import com.example.www.popularmovies.Model.MovieList;
import com.example.www.popularmovies.Model.MoviedbAPI;
import com.example.www.popularmovies.Utilities.DBFlowExclusionStrategy;
import com.google.gson.ExclusionStrategy;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MovieListFragment extends Fragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks = sMovieCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;

    public interface Callbacks {
        public void onItemSelected(MovieData movie, View view);
    }

    private static Callbacks sMovieCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(MovieData movie, View view) {
        }
    };

    private MovieAdapter movieAdapter;
    private ArrayList<MovieData> movieList = new ArrayList<>();
    private GridView gridView;
    private ProgressDialog mProgresDialog;
    public static final String BASE_URL = "http://api.themoviedb.org";
    private Retrofit retrofit;
    private MoviedbAPI moviedbAPI;
    private Boolean loadingMore = false;
    private Boolean notLoad = true;
    private String sortByPref;
    private LinearLayout loadMoreSpinner;
    private int currentPage = 1;


    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(getString(R.string.pref_sort),
                getString(R.string.pref_sort_default));
        sortByPref = sortPref;
        movieAdapter = new MovieAdapter(getContext(), movieList);
        mProgresDialog = new ProgressDialog(getContext());
        mProgresDialog.setTitle("Please wait..");
        mProgresDialog.setMessage("Getting awesome movies list..");
        loadMoreSpinner = (LinearLayout) rootView.findViewById(R.id.linlaProgressBar);
        loadMoreSpinner.setVisibility(View.GONE);

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallbacks.onItemSelected(movieList.get(i), view);
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            //Load more movies when we reach end of grid
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)
                        && !sortByPref.equals(getString(R.string.pref_sort_fav))) {
                    if (notLoad == false) {
                        loadMoreMovies();
                    }
                }
            }
        });

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()});

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        moviedbAPI = retrofit.create(MoviedbAPI.class);
        if(sortByPref.equals(getString(R.string.pref_sort_fav))){
            setFavMovies();
        }
        else{
            updateMovie();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(getString(R.string.pref_sort),
                getString(R.string.pref_sort_default));
        if(sortPref.equals(getString(R.string.pref_sort_fav))){
            sortByPref = sortPref;
            setFavMovies();
        }
        if(!sortByPref.equals(sortPref)){
            currentPage = 1;
            movieList.clear();
            updateMovie();
        }

    }

    private void setFavMovies(){
        movieList.clear();
        movieList.addAll(SQLite.select().from(MovieData.class).queryList());
        movieAdapter.notifyDataSetChanged();
    }

    private void updateMovie() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(getString(R.string.pref_sort),
                getString(R.string.pref_sort_default));
        sortByPref = sortPref;
        notLoad = true;
        mProgresDialog.show();
        Call<MovieList> call = moviedbAPI.getMovies(sortPref, 1);
        Log.d("MOVIE_APP", "Loading(Update) movies sort by " + sortByPref + " page: " + currentPage);
        call.enqueue(new Callback<MovieList>() {

            @Override
            public void onResponse(Response<MovieList> response, Retrofit retrofit) {
                try {
                    MovieList allMovies = response.body();
                    notLoad = false;
                    mProgresDialog.dismiss();
                    movieList.clear();
                    movieList.addAll(allMovies.getResults());
                    movieAdapter.notifyDataSetChanged();
                }
                catch(Exception e){
                    Log.d("MOVIE_APP", "Error in Update Movie response: " + sortByPref + " page: " + currentPage);
                    notLoad = false;
                    mProgresDialog.dismiss();
                    Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MOVIE_APP", "Error in Update Movie: " + t.getMessage());
                notLoad = false;
                mProgresDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreMovies() {
        currentPage += 1;
        Call<MovieList> call = moviedbAPI.getMovies(sortByPref, currentPage);
        Log.d("MOVIE_APP", "Loading more movies sort by " + sortByPref + " page: " + currentPage);
        loadingMore = true;
        loadMoreSpinner.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MovieList>() {

            @Override
            public void onResponse(Response<MovieList> response, Retrofit retrofit) {
                loadMoreSpinner.setVisibility(View.GONE);
                loadingMore = false;
                try {
                    MovieList allMovies = response.body();
                    movieList.addAll(allMovies.getResults());
                    movieAdapter.notifyDataSetChanged();
                } catch(Exception e) {
                    Log.d("MOVIE_APP", "Error in load more movies response: " + sortByPref + " page: " + currentPage);
                    Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MOVIE_APP", "Error in load more movies: " + sortByPref + " page: " + currentPage);
                loadMoreSpinner.setVisibility(View.GONE);
                loadingMore = false;
                Toast.makeText(getActivity(), "Network Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sMovieCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        gridView.setChoiceMode(activateOnItemClick
                ? GridView.CHOICE_MODE_SINGLE
                : GridView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == GridView.INVALID_POSITION) {
            gridView.setItemChecked(mActivatedPosition, false);
        } else {
            gridView.setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

}
