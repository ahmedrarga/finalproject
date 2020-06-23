package com.ahmedrarga.finalproject.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.ahmedrarga.finalproject.BaseActivity;
import com.ahmedrarga.finalproject.HomePage.PosterAdapter;
import com.ahmedrarga.finalproject.HomePage.SearchResultsRecyclerAdapter;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.ahmedrarga.finalproject.tmdb.Requests;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DiscoverActivity extends BaseActivity {
    private RecyclerView NowPlaying;
    private RecyclerView popularM;
    private RecyclerView popularS;
    private RecyclerView topRatedM;
    private RecyclerView topRatedS;
    private RecyclerView Upcoming;
    private RecyclerView trending;
    private RecyclerView trendingS;
    private final String api_key = "f98d888dd7ebd466329c6a26f1018a55";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        initToolbar("Discover");
        findViewById(R.id.news).setVisibility(View.GONE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Requests r = new Requests();
                trending = findViewById(R.id.trendingMovies);
                trendingS = findViewById(R.id.trendingShows);
                NowPlaying = findViewById(R.id.nowPlayingRView);
                Upcoming = findViewById(R.id.upcoming);
                popularM = findViewById(R.id.popularMovies);
                popularS = findViewById(R.id.popularShows);
                topRatedM = findViewById(R.id.topRatedMovies);
                topRatedS = findViewById(R.id.topRatedShows);
                new task().execute("upcoming");
                new task().execute("nowPlaying");
                new task().execute("trendingMovies");
                new task().execute("trendingShows");
                new task().execute("popualrMovies");
                new task().execute("popularShows");
                new task().execute("topRatedMovies");
                new task().execute("topRatedShows");

            }
        });
    }



    private void initRList(RecyclerView r, List<Movie> movies, String cl){
        r.setAdapter(new PosterAdapter(movies, getApplicationContext(), cl));
        r.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }
    public class task extends AsyncTask<String, Void, List<Object>> {
        String trendingMovies = "https://api.themoviedb.org/3/trending/movie/day?" +
                "api_key=" + api_key;
        String trendingShows = "https://api.themoviedb.org/3/trending/tv/day?" +
                "api_key=" + api_key;
        String popularMovies = "https://api.themoviedb.org/3/movie/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String popularShows = "https://api.themoviedb.org/3/tv/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String topRatedMovies ="https://api.themoviedb.org/3/movie/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String topRatedShows = "https://api.themoviedb.org/3/tv/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String upcoming = "https://api.themoviedb.org/3/movie/upcoming?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String nowPlaying = "https://api.themoviedb.org/3/movie/" +
                "now_playing?api_key=" + api_key +
                "&language=en-US&page=1";

        @Override
        protected List<Object> doInBackground(String... strings) {
            List<Movie> toRet = new ArrayList<>();
            List<Object> obj = new ArrayList<>();
            Response response = null;
            if(strings[0].equals("trendingMovies")){
                response = setResponse(trendingMovies);
            } else if(strings[0].equals("trendingShows")){
                response = setResponse(trendingShows);
            } else if(strings[0].equals("upcoming")){
                response = setResponse(upcoming);

            } else if(strings[0].equals("nowPlaying")){
                response = setResponse(nowPlaying);

            } else if(strings[0].equals("popualrMovies")){
                response = setResponse(popularMovies);

            } else if(strings[0].equals("popularShows")){
                response = setResponse(popularShows);

            } else if(strings[0].equals("topRatedMovies")){
                response = setResponse(topRatedMovies);

            } else if(strings[0].equals("topRatedShows")){
                response = setResponse(topRatedShows);
            }
            String media_type = "movie";
            if(strings[0].contains("Shows")){
                media_type = "tv";
            }
            if(response != null){
                try{
                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("results");
                    for (int i = 0; i < array.length(); i++){
                        toRet.add(new Movie(array.getJSONObject(i), media_type));
                    }
                    obj.add((Object)toRet);
                    obj.add(strings[0]);
                }catch (IOException e1){

                }catch (JSONException e2){

                }
            }
            return obj;

        }
        private Response setResponse(String query){
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            Request request = new Request.Builder()
                    .url(query)
                    .get()
                    .build();

            try {
                response = client.newCall(request).execute();
                return response;

            } catch (Exception e) {
                System.out.println("Error in doInBackground");
                System.out.println("Error:" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            findViewById(R.id.news).setVisibility(View.GONE);
            findViewById(R.id.progressBar3).setVisibility(View.VISIBLE);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Object> obj) {
            super.onPostExecute(obj);
            if(obj.size() == 2){
                if(((String)obj.get(1)).equals("trendingMovies")){
                    initRList(trending, (ArrayList)obj.get(0), "movie");
                } else if(((String)obj.get(1)).equals("trendingShows")){
                    initRList(trendingS, (ArrayList)obj.get(0), "show");
                } else if(((String)obj.get(1)).equals("upcoming")){
                    initRList(Upcoming, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("nowPlaying")){
                    initRList(NowPlaying, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("popualrMovies")){
                    initRList(popularM, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("popularShows")){
                    initRList(popularS, (ArrayList)obj.get(0), "show");

                } else if(((String)obj.get(1)).equals("topRatedMovies")){
                    initRList(topRatedM, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("topRatedShows")){
                    initRList(topRatedS, (ArrayList)obj.get(0), "show");
                }
                findViewById(R.id.news).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar3).setVisibility(View.GONE);
            }

        }
    }
}