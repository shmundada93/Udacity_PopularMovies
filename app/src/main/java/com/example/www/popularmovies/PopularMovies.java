package com.example.www.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by shmundada on 05-02-2016.
 */
public class PopularMovies extends Application {
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        Stetho.initializeWithDefaults(this);
    }
}
