package com.example.www.popularmovies.Model;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by shmundada on 07-02-2016.
 */
@Database(name = MovieDB.NAME, version = MovieDB.VERSION)
public class MovieDB {
    public static final String NAME = "MovieDB";

    public static final int VERSION = 1;
}
