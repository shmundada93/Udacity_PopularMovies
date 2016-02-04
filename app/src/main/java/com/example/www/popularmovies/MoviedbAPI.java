package com.example.www.popularmovies;

import com.example.www.popularmovies.Model.MovieList;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by shmundada on 05-02-2016.
 */
public interface MoviedbAPI {
    @GET("/3/discover/movie?api_key=25957923c5900525a6fcd8bd091e0dbb")
    public Call<MovieList> getMovies(@Query("sort_by") String sort,
                              @Query("page") Integer page);
}
