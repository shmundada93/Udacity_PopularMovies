package com.example.www.popularmovies;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MovieListActivity extends AppCompatActivity
        implements MovieListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if (findViewById(R.id.movie_detail_container) != null)
            mTwoPane = true;
            ((MovieListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.movie_list))
                    .setActivateOnItemClick(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Callback method from {@link MovieListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(MovieData movie, View view) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(MovieDetailFragment.ARG_ITEM ,movie);
        if (mTwoPane) {
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(
                    view, 0, 0, view.getWidth(), view.getHeight());
            detailIntent.putExtra("movieBundle",arguments);
            startActivity(detailIntent, opts.toBundle());
        }
    }
}
