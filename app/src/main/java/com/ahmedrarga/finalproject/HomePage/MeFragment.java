package com.ahmedrarga.finalproject.HomePage;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmedrarga.finalproject.MovieProfile.Tracking;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.User.FeedsAdapter;
import com.ahmedrarga.finalproject.User.ProfileActivity;
import com.ahmedrarga.finalproject.UserPost;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.util.List;
import java.util.Map;

import static android.os.Looper.getMainLooper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView cover;
    ImageView profile;
    TextView name;
    private ArrayList<Map<String, Object>> map;
    TextView email;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView hist;
    RecyclerView watching;
    ProgressBar watchingBar;
    TextView watchingMessage;
    List<String> postsArray = new ArrayList<>();
    String userName = "";

    private OnFragmentInteractionListener mListener;

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
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
        final View v = inflater.inflate(R.layout.fragment_me, container, false);
    hist = v.findViewById(R.id.hist);



        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                final String m = user.getEmail();
                System.out.println(m);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("EMAIL", m)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        name = v.findViewById(R.id.name);
                                        email = v.findViewById(R.id.email);
                                        userName = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                        name.setText(userName);
                                        email.setText(m);
                                    }
                                } else {
                                }
                            }
                        });
                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference pRef = ref.child("images/" + m + "/profile.jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            profile = v.findViewById(R.id.profile);
                            Picasso.get()
                                    .load(task.getResult())
                                    .fit()
                                    .into(profile);
                        }
                    }
                });
                pRef = ref.child("images/" + m + "/cover.jpg");
                pRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        cover = v.findViewById(R.id.cover);
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .into(cover);
                    }
                });
                db.collection("history")
                        .document(m)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                History hist = documentSnapshot.toObject(History.class);
                                if(hist == null) {
                                    hist = new History();
                                }
                                new task().execute(hist.array);
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
    private class task extends AsyncTask<ArrayList<Map<String,String>>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(ArrayList<Map<String, String>>... arrayLists) {
            ArrayList<Map<String,String>> arrayList = arrayLists[0];
            List<Movie> movies = new ArrayList<>();
            for (Map<String, String> m : arrayList){
                String query = "";
                String media_type = m.get("media_type");
                if(media_type.equals("tv")){
                    query = "https://api.themoviedb.org/3/" +
                            "tv" +
                            "/" + Integer.parseInt(m.get("id")) +
                            "?api_key=" + HomeActivity.api_key +
                            "&language=en-US";
                }
                else{
                    query = "https://api.themoviedb.org/3/" +
                            "movie" +
                            "/" + Integer.parseInt(m.get("id")) +
                            "?api_key=" + HomeActivity.api_key +
                            "&language=en-US";
                }
                Response r = setResponse(query);
                if (r != null && r.code() == 200) {
                    try {
                        movies.add(new Movie(new JSONObject(r.body().string()), media_type));
                    }catch (IOException e){

                    }catch (JSONException e2) {

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
            hist.setAdapter(new PosterAdapter(movies, getContext(), "home"));
            hist.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
    }


}
