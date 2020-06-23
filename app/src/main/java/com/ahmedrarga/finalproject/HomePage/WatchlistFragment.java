package com.ahmedrarga.finalproject.HomePage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ahmedrarga.finalproject.MovieProfile.Tracking;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.User.DiscoverActivity;
import com.ahmedrarga.finalproject.User.WatchlistActivity;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.ahmedrarga.finalproject.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WatchlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WatchlistFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Requests r;
    private ArrayList<Map<String, Object>> map;
    private static List<Movie> movies = new ArrayList<>();
    private RecyclerView waiting;
    private RecyclerView watching;
    private RecyclerView stremaing;
    private RecyclerView moviesOnly;
    private RecyclerView showsOnly;
    private List<Movie> waitingLst = new ArrayList<>();
    private List<Movie> watchingLst = new ArrayList<>();
    private List<Movie> streamingLst = new ArrayList<>();
    public static List<Movie> moviesLst = new ArrayList<>();
    private List<Movie> showsLst = new ArrayList<>();
    View v;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WatchlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WatchlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WatchlistFragment newInstance(String param1, String param2) {
        WatchlistFragment fragment = new WatchlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_watchlist, container, false);
        this.v = v;
        v.findViewById(R.id.layout).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        v.findViewById(R.id.add).setVisibility(GONE);
        moviesOnly = init(moviesOnly, R.id.movies, moviesLst);
        showsOnly = init(showsOnly, R.id.shows, showsLst);
        stremaing = init(stremaing, R.id.streaming, streamingLst);
        waiting = init(waiting, R.id.waiting, waitingLst);
        watching = init(watching, R.id.watching, watchingLst);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                db.collection("watchlist").document(u).
                        get().
                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        map = (ArrayList) task.getResult().getData().get("listOfMovies");
                                        new task().execute(map);


                                    } catch (NullPointerException e) {
                                        Button btn = v.findViewById(R.id.add);
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getContext(), DiscoverActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        v.findViewById(R.id.progress).setVisibility(GONE);
                                        btn.setVisibility(View.VISIBLE);
                                    }
                                }
                                else{
                                    Snackbar.make(v, "Error in importing data", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage() + "ewfqqergkhjegfjWEHFGOWELFG ");
                    }
                });
            }
        });
        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void initRList(List<Movie> movies){
        for (Movie m : movies){
            if(m.getMedia_type().equals("tv")){
                showsLst.add(m);
            }else{
                moviesLst.add(m);
            }
        }

        for(final Movie m : showsLst){
            if(m.isStreaming()){
                streamingLst.add(m);
                stremaing.getAdapter().notifyItemInserted(streamingLst.size() - 1);
            }else if(m.isWaiting()){
                waitingLst.add(m);
                waiting.getAdapter().notifyItemInserted(waitingLst.size() - 1);
            }
            FirebaseFirestore.getInstance().collection("Tracking")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Tracking t = documentSnapshot.toObject(Tracking.class);
                    if(t != null && t.tracking != null &&
                            t.tracking.get(String.valueOf(m.getId())) != null
                            && !m.isWatched()){
                        watchingLst.add(m);
                        watching.getAdapter().notifyItemInserted(watchingLst.size() - 1);
                    }
                }
            });
        }
        v.findViewById(R.id.progress).setVisibility(GONE);
        v.findViewById(R.id.layout).setVisibility(View.VISIBLE);

    }
    private RecyclerView init(RecyclerView r, int id, List<Movie> movies){
        r = v.findViewById(id);
        r.setAdapter(new PosterAdapter(movies, getContext(), "tv"));
        r.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        return r;
    }
    private class task extends AsyncTask<ArrayList<Map<String, Object>>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(ArrayList<Map<String, Object>>... arrayLists) {
            List<Movie> movies = new ArrayList<>();
            for(Map<String, Object> m : arrayLists[0]){
                String query = "https://api.themoviedb.org/3/" +
                        (String)m.get("media_type") +
                        "/" + m.get("id") +
                        "?api_key=" + HomeActivity.api_key;
                Response response = setResponse(query);
                if(response != null && response.code() == 200){
                    try {
                        movies.add(new Movie(new JSONObject(response.body().string()), (String)m.get("media_type")));
                    }catch (IOException e1){

                    }catch (JSONException e2){

                    }

                }
            }
            return movies;
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
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if(movies.size() == 0){
                v.findViewById(R.id.layout).setVisibility(GONE);
                v.findViewById(R.id.progress).setVisibility(GONE);
                v.findViewById(R.id.add).setVisibility(View.VISIBLE);
            }else {
                initRList(movies);
                if(waitingLst.size() == 0) {
                    waiting.setVisibility(GONE);
                    v.findViewById(R.id.waiting_text).setVisibility(GONE);
                }
                if(streamingLst.size() == 0) {
                    stremaing.setVisibility(GONE);
                    v.findViewById(R.id.streamingText).setVisibility(GONE);
                }
                if(watchingLst.size() == 0) {
                    watching.setVisibility(GONE);
                    v.findViewById(R.id.watchingText).setVisibility(GONE);
                }
                if(moviesLst.size() == 0) {
                    moviesOnly.setVisibility(GONE);
                    v.findViewById(R.id.moviesText).setVisibility(GONE);
                }
                if(showsLst.size() == 0) {
                    showsOnly.setVisibility(GONE);
                    v.findViewById(R.id.showsText).setVisibility(GONE);
                }

            }


        }
    }
}
