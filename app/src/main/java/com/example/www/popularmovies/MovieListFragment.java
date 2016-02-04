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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.www.popularmovies.Adapter.MovieAdapter;
import com.example.www.popularmovies.Model.MovieData;
import com.example.www.popularmovies.Model.MovieList;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A list fragment representing a list of Movies. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MovieDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MovieListFragment extends Fragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */


    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(MovieData movie);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(MovieData movie) {
        }
    };


    private final String MOVIEDB_KEY = "25957923c5900525a6fcd8bd091e0dbb";
    MovieAdapter movieAdapter;
    ArrayList<MovieData> movieList = new ArrayList<>();
    GridView gridView;
    private ProgressDialog mProgresDialog;
    public static final String BASE_URL = "http://api.themoviedb.org";
    private Retrofit retrofit;
    private MoviedbAPI moviedbAPI;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
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
        movieAdapter = new MovieAdapter(getContext(), movieList);
        mProgresDialog = new ProgressDialog(getContext());
        mProgresDialog.setTitle("Please wait..");
        mProgresDialog.setMessage("Getting awesome movies list..");
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallbacks.onItemSelected(movieList.get(i));
            }
        });
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        moviedbAPI = retrofit.create(MoviedbAPI.class);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
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


    private void updateMovie() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(getString(R.string.pref_sort),
                getString(R.string.pref_sort_default));
        mProgresDialog.show();
        Call<MovieList> call = moviedbAPI.getMovies(sortPref,2);
        Log.d("MOVIE_APP", call.toString());
        call.enqueue(new Callback<MovieList>() {

            @Override
            public void onResponse(Response<MovieList> response, Retrofit retrofit) {
                MovieList allMovies = response.body();
                Log.d("MOVIE_APP", response.toString());
                Log.d("MOVIE_APP", allMovies.getResults().get(0).getTitle());
                mProgresDialog.dismiss();
                movieList.clear();
                movieList.addAll(allMovies.getResults());
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

}
