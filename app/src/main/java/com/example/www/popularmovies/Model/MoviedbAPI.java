package com.example.www.popularmovies.Model;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by shmundada on 05-02-2016.
 */
public interface MoviedbAPI {
    @GET("/3/discover/movie?api_key=25957923c5900525a6fcd8bd091e0dbb&vote_count.gte=10")
    public Call<MovieList> getMovies(@Query("sort_by") String sort,
                              @Query("page") Integer page);

    @GET("/3/movie/{id}/reviews?api_key=25957923c5900525a6fcd8bd091e0dbb")
    public Call<ReviewList> getReviews(@Path("id") Integer movieId);

    @GET("/3/movie/{id}/videos?api_key=25957923c5900525a6fcd8bd091e0dbb")
    public Call<TrailerList> getTrailers(@Path("id") Integer movieId);
}
