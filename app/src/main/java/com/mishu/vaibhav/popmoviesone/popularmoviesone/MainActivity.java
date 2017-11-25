package com.mishu.vaibhav.popmoviesone.popularmoviesone;

import android.content.Context;
import android.net.Network;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mishu.vaibhav.popmoviesone.popularmoviesone.utils.MovieDbJsonUtils;
import com.mishu.vaibhav.popmoviesone.popularmoviesone.utils.NetworkUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static List<MovieDbJsonUtils.Movie> movies;
    private MovieAdapter adapter;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new MovieAdapter(this,null);
        GridLayoutManager movieLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        RecyclerView movieRecyclerView = findViewById(R.id.movie_recycler_view);
        movieRecyclerView.setAdapter(adapter);
        movieRecyclerView.setLayoutManager(movieLayoutManager);

        CallMovieServer task = new CallMovieServer(this);
        task.execute(NetworkUtils.POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CallMovieServer task = new CallMovieServer(this);
        switch (item.getItemId()){
            case R.id.sort_popular:
                task.execute(NetworkUtils.POPULAR);
                break;

            case R.id.sort_rating:
                task.execute(NetworkUtils.RATING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static class CallMovieServer extends AsyncTask<String,Void,List<MovieDbJsonUtils.Movie>>{

        private WeakReference<MainActivity> activityReference;

        CallMovieServer(MainActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<MovieDbJsonUtils.Movie> doInBackground(String... type) {
            Log.i(LOG_TAG,"doinback");
            try {
                movies = MovieDbJsonUtils.jsonStringToMovies(
                        NetworkUtils.getResponseFromHttpUrl(
                                NetworkUtils.buildMovieUrl(type[0])
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<MovieDbJsonUtils.Movie> movies) {
            super.onPostExecute(movies);

            Log.i(LOG_TAG,"onpostexecute");

            MainActivity activity = activityReference.get();
            if (activity == null) return;

            Log.i(LOG_TAG,"activity not null... swapping list");
            activity.adapter.swap(movies);
        }
    }
}
