package com.ahmedrarga.finalproject.HomePage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ahmedrarga.finalproject.Account.RatingsActivity;
import com.ahmedrarga.finalproject.Account.rating;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.RecommenderSystem.Dictionary;
import com.ahmedrarga.finalproject.RecommenderSystem.recommenderActivity;
import com.ahmedrarga.finalproject.User.WatchlistActivity;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecommendationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recommendations;
    private ProgressBar bar;
    private String mail;
    ArrayList<Map<String, Object>> map;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendationsFragment newInstance(String param1, String param2) {
        RecommendationsFragment fragment = new RecommendationsFragment();
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
        View v = inflater.inflate(R.layout.fragment_recommendations, container, false);
        getWatchList();
        recommendations = v.findViewById(R.id.recommendations);
        bar = v.findViewById(R.id.recProg);
        bar.setVisibility(View.VISIBLE);
        recommendations.setVisibility(View.GONE);
        final Button btn = v.findViewById(R.id.add_ratings);
        btn.setVisibility(View.GONE);
        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance().collection("ratings")
                .document(mail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        rating r = documentSnapshot.toObject(rating.class);
                        if(r != null && r.arrayList != null && r.arrayList.size() > 0){
                            ArrayList<Map<String,String>> tmp = new ArrayList<>();
                            for(Map<String,String> m : r.arrayList){
                                if(m.get("type").equals("movie")){
                                    tmp.add(m);
                                }
                            }
                            new task().execute(tmp);
                        } else{
                            btn.setVisibility(View.VISIBLE);
                            bar.setVisibility(View.GONE);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), RatingsActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
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
    private void getWatchList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("watchlist")
                .document(m)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().getData() != null)
                                map = (ArrayList) task.getResult().getData().get("listOfMovies");
                        }
                    }
                });

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
    private class task extends AsyncTask<ArrayList<Map<String, String>>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(ArrayList<Map<String, String>>... arrayLists) {
            Dictionary dict = new Dictionary(arrayLists[0]);
            System.out.println(dict.toString());
            List<Movie> movies = new ArrayList<>();
            String query = "https://recommender-sce.nw.r.appspot.com/api/" +
                    "recommendations?" +
                    "user=" + mail +
                    "&hist=" + dict.toString();
            Response response = setResponse(query);
            if(response != null && response.code() == 200){
                try{
                    JSONArray obj = new JSONArray(response.body().string());
                    System.out.println(obj);
                    for (int i = 0; i < obj.length(); i++){
                        int id = obj.getInt(i);
                        if(!compareToWatchlist(id)) {
                            String q = "https://api.themoviedb.org/3/movie/" +
                                    String.valueOf(id) +
                                    "?api_key=" + HomeActivity.api_key;
                            Response r = setResponse(q);
                            if (r != null && r.code() == 200) {
                                movies.add(new Movie(new JSONObject(r.body().string()), "movie"));
                            }
                        }
                    }
                }catch(IOException e1){

                }catch (JSONException e2){
                    System.out.println(e2.getMessage() + "oerjfowergjnpweoirgjpeoirgjpeoirgjpeorgsoergjos;ergjopw;eirgewg ============================");
                }
            }
            return movies;
        }
        private boolean compareToWatchlist(int id) {
            if(map != null) {
                for(Map<String,Object> m : map) {
                    if(Integer.parseInt((m.get("id").toString())) == id) {
                        return true;
                    }
                }
                return false;
            }
            return false;
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
            recommendations.setAdapter(new SearchResultsRecyclerAdapter(movies, getContext(), "movie"));
            recommendations.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recommendations.setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
        }
    }
}
