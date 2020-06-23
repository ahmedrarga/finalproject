package com.ahmedrarga.finalproject.MovieProfile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.ahmedrarga.finalproject.ExoPlayer.ExoPlayerRecyclerView;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.ahmedrarga.finalproject.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MovieProfileActivity extends AppCompatActivity implements Overview.OnFragmentInteractionListener,
        Posts.OnFragmentInteractionListener, Track.OnFragmentInteractionListener, EpisodeFragment.OnFragmentInteractionListener
        , SeasonFragment.OnFragmentInteractionListener{
    protected int id;
    private ImageView image;
    private TextView movie_name;
    protected String media_type;
    protected static final String api_key = "f98d888dd7ebd466329c6a26f1018a55";
    Movie movie;
    boolean finished = false;
    ImageView poster;
    private boolean isInWatchlist = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TabLayout tabs;
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    protected  ViewPager pager;
    TextView contentRating;
     FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile);
        poster = findViewById(R.id.poster);
        ViewCompat.setTransitionName(poster, VIEW_NAME_HEADER_IMAGE);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        media_type = intent.getStringExtra("media_type");
        tabs = findViewById(R.id.movieProfileTab);
        pager = findViewById(R.id.viewPager);
         adapter = new FragmentAdapter(getApplicationContext(),getSupportFragmentManager(), tabs.getTabCount(), media_type);

        if(media_type.equals("movie"))
            tabs.removeTabAt(1);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 2 || tab.getPosition() == 1){
                    ExoPlayerRecyclerView.videoPlayer.stop();

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        contentRating = findViewById(R.id.content_rating);
        image = findViewById(R.id.backdrop);
        movie_name = findViewById(R.id.title);

        ImageView back = findViewById(R.id.back);
        back.setClickable(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        new task().execute(id);




    }

    @Override
    protected void onResume() {
        super.onResume();
        checkWatchlist();

    }

    @Override
    protected void onDestroy() {
        ExoPlayerRecyclerView.videoPlayer.stop();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ExoPlayerRecyclerView.videoPlayer.stop();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        ExoPlayerRecyclerView.videoPlayer.stop();
        super.onBackPressed();
    }

    protected void checkWatchlist(){
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("watchlist").document(u).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    try {
                        System.out.println(task.getResult().getData()+ "ef;aiweFGHLwieghf   weiGAEURHGvliweuhgv;WEFHUpwieuoghflsdKHFisdghfpwoiEGHFPOWTPERHC");
                        ArrayList<Map<String, Object>> map = (ArrayList)task.getResult().getData().get("listOfMovies");
                        for(int i = 0; i < map.size(); i++){
                            if((long)map.get(i).get("id") == (long)id && map.get(i).get("media_type").equals(media_type)){
                                isInWatchlist = true;
                                FloatingActionButton btn = findViewById(R.id.add);
                                Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_done);
                                btn.setImageDrawable(img);
                                break;
                            }
                        }

                    }catch (NullPointerException e){
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error: " + e.getMessage() + "------------------------------------------------------");
            }
        });
    }

    @Override
    public Movie onFragmentInteraction(View view) {
        return movie;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private final class watchlist{
        private int id;
        private String media_type;
        public watchlist(int id, String media_type){
            this.id = id;
            this.media_type = media_type;
        }


        public int getId() {
            return id;
        }
        public HashMap<String, Object> get(){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", this.id);
            map.put("media_type", this.media_type);
            return map;
        }
    }

    public void addToWatchList(View v){
        if(!isInWatchlist) {
            final View view = v;
            ArrayList<HashMap<String, Object>> w = new ArrayList<>();
            w.add(new watchlist(id, media_type).get());
            final Map<String, Object> toAdd = new HashMap<>();
            toAdd.put("listOfMovies", w);
            db = FirebaseFirestore.getInstance();

            final CollectionReference ref = db.collection("watchlist");
            final String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            ref.document(u).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            System.out.println("DocumentSnapshot data: " +  doc.getData().get("listOfMovies"));
                            ArrayList<HashMap<String, Object>> arrayList = (ArrayList<HashMap<String, Object>>)doc.getData().get("listOfMovies");
                            arrayList.add(new watchlist(id, media_type).get());
                            Map <String, Object> map = new HashMap<>();
                            map.put("listOfMovies", arrayList);
                            ref.document(u).set(map);
                        } else {
                            System.out.println("No such document");
                            ref.document(u).set(toAdd);
                        }
                        isInWatchlist = true;
                        FloatingActionButton btn = findViewById(R.id.add);
                        Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_done);
                        btn.setImageDrawable(img);
                    }
                }
            });
        }else{
            db = FirebaseFirestore.getInstance();
            final String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            db.collection("watchlist")
                    .document(m)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            System.out.println("DocumentSnapshot data: " + doc.getData().get("listOfMovies"));
                            ArrayList<HashMap<String, Object>> arrayList = (ArrayList<HashMap<String, Object>>) doc.getData().get("listOfMovies");
                            ArrayList<HashMap<String,Object>> tmp = new ArrayList<>(arrayList);
                            for(HashMap<String, Object> t : tmp){
                                if((long) t.get("id") == id && ((String)t.get("media_type")).equals(media_type)){
                                    arrayList.remove(t);
                                }
                            }
                            Map<String, Object> ma = new HashMap<>();
                            ma.put("listOfMovies", arrayList);
                            db.collection("watchlist").document(m).set(ma);
                            FloatingActionButton btn = findViewById(R.id.add);
                            Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_plus);
                            btn.setImageDrawable(img);
                            isInWatchlist = false;

                        }
                    }
                }
            });
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class task extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... integers) {
            String query = "https://api.themoviedb.org/3/" +
                    media_type +
                    "/" + integers[0] +
                    "?api_key=" + api_key +
                    "&language=en-US&append_to_response=content_ratings";
            Response response = setResponse(query);
            Movie movie = null;
            if(response != null && response.code() == 200){
                try {
                    movie = new Movie(new JSONObject(response.body().string()), media_type);
                }catch (IOException e){

                }catch (JSONException e1){

                }
            }
            return movie;

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
        protected void onPostExecute(Movie m) {
            super.onPostExecute(movie);
            movie = m;
            if(movie != null){
                pager.setAdapter(adapter);
                pager.setVisibility(View.VISIBLE);
                tabs.setVisibility(View.VISIBLE);
                finished = true;
                Picasso.get().load(movie.getBackdrop_path())
                        .into(image);
                Picasso.get()
                        .load(movie.getPoster_path())
                        .fit()
                        .into(poster);
                String name = movie.getName();
                if(name.length() > 45){
                    name = name.substring(0, 45) + "...";
                }
                movie_name.setText(name);
                TextView rating = findViewById(R.id.rating);
                rating.setText(String.valueOf(movie.getRating()));
                TextView air_dates = findViewById(R.id.air_dates);
                String air = movie.getAirDates() + " | ";
                air_dates.setText(air);
                String content = movie.getContentRating();
                if(content.equals("")){
                    contentRating.setVisibility(View.GONE);
                }else {
                    contentRating.setText(movie.getContentRating());
                }




            }
        }
    }
    public void loadFullSizeImage(){
        if(movie != null) {
            Picasso.get()
                    .load(movie.getPoster_path())
                    .into(poster);
        }
    }

}
