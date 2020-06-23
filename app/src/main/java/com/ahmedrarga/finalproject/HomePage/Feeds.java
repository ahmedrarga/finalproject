package com.ahmedrarga.finalproject.HomePage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.ahmedrarga.finalproject.ExoPlayer.ExoPlayerRecyclerView;
import com.ahmedrarga.finalproject.ExoPlayer.MediaRecyclerAdapter;
import com.ahmedrarga.finalproject.MediaObject;
import com.ahmedrarga.finalproject.MovieProfile.MovieProfileActivity;
import com.ahmedrarga.finalproject.NewPostActivity;
import com.ahmedrarga.finalproject.Post;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.User.OnFinished;
import com.ahmedrarga.finalproject.User.WatchlistActivity;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.ahmedrarga.finalproject.tmdb.Requests;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.widget.LinearLayout.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Feeds#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Feeds extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<Map<String, Object>> movies;
    final ArrayList<Map<String,String>> data = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    ExoPlayerRecyclerView mRecyclerView;
    ArrayList<MediaObject> videos = new ArrayList<>();
    int uId = 0;


    private OnFragmentInteractionListener mListener;

    public Feeds() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Feeds.
     */
    // TODO: Rename and change types and number of parameters
    public static Feeds newInstance(String param1, String param2) {
        Feeds fragment = new Feeds();
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
        final View v =  inflater.inflate(R.layout.fragment_feeds, container, false);
        final LinearLayout add = v.findViewById(R.id.addClip);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                getActivity().startActivity(intent);
            }
        });
        mRecyclerView = v.findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final MediaRecyclerAdapter mAdapter = new MediaRecyclerAdapter(videos, initGlide());
        mRecyclerView.setMediaObjects(videos);
        mRecyclerView.setAdapter(mAdapter);
        db.collection("watchlist").document(u).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            try {
                                ArrayList<Map<String, Object>> map = (ArrayList)task.getResult().getData().get("listOfMovies");
                                    for (int i = 0; i < map.size(); i++) {
                                        final long id = (long) map.get(i).get("id");
                                        final String media_type = (String) map.get(i).get("media_type");
                                        Map<String, Object> m = map.get(i);
                                        firestore.collection("posts").document(String.valueOf((long) m.get("id")))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Post post;
                                                            post = task.getResult().toObject(Post.class);
                                                            if (post != null) {
                                                                for (final Map<String, String> m : post.arrayList) {
                                                                    final MediaObject media = new MediaObject();
                                                                    media.setId(uId);
                                                                    uId++;
                                                                    media.setObject(m);
                                                                    FirebaseFirestore.getInstance().collection("users")
                                                                            .whereEqualTo("EMAIL", m.get("user"))
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if(task.isSuccessful()) {
                                                                                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                                            media.setUserHandle(documentSnapshot.get("FIRST_NAME") + " " + documentSnapshot.get("LAST_NAME"));
                                                                                            mAdapter.addMediaObject(media);
                                                                                            mAdapter.notifyDataSetChanged();
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                }


                            }catch (NullPointerException e){

                            }
                        }
                        else{
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage() + "ewfqqergkhjegfjWEHFGOWELFG ");
            }
        });


        return v;
    }

    private void init() {

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

    @Override
    public void onStop() {
        ExoPlayerRecyclerView.videoPlayer.stop();

        super.onStop();
    }

    @Override
    public void onPause() {
        ExoPlayerRecyclerView.videoPlayer.stop();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        ExoPlayerRecyclerView.videoPlayer.stop();

        super.onDestroy();
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
    public interface WatchlistListener {
        void Watchlist(List<Movie> movies);
    }
    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }
    private void prepareVideoList() {
        MediaObject mediaObject = new MediaObject();
        mediaObject.setId(1);
        mediaObject.setUserHandle("@h.pandya");
        mediaObject.setTitle(
                "Do you think the concept of marriage will no longer exist in the future?");
        mediaObject.setCoverUrl(
                "https://androidwave.com/media/images/exo-player-in-recyclerview-in-android-1.png");
        mediaObject.setUrl("https://androidwave.com/media/androidwave-video-1.mp4");
        MediaObject mediaObject2 = new MediaObject();
        mediaObject2.setId(2);
        mediaObject2.setUserHandle("@hardik.patel");
        mediaObject2.setTitle(
                "If my future husband doesn't cook food as good as my mother should I scold him?");
        mediaObject2.setCoverUrl(
                "https://androidwave.com/media/images/exo-player-in-recyclerview-in-android-2.png");
        mediaObject2.setUrl("https://androidwave.com/media/androidwave-video-2.mp4");
        MediaObject mediaObject3 = new MediaObject();
        mediaObject3.setId(3);
        mediaObject3.setUserHandle("@arun.gandhi");
        mediaObject3.setTitle("Give your opinion about the Ayodhya temple controversy.");
        mediaObject3.setCoverUrl(
                "https://androidwave.com/media/images/exo-player-in-recyclerview-in-android-3.png");
        mediaObject3.setUrl("https://androidwave.com/media/androidwave-video-3.mp4");
        MediaObject mediaObject4 = new MediaObject();
        mediaObject4.setId(4);
        mediaObject4.setUserHandle("@sachin.patel");
        mediaObject4.setTitle("When did kama founders find sex offensive to Indian traditions");
        mediaObject4.setCoverUrl(
                "https://androidwave.com/media/images/exo-player-in-recyclerview-in-android-4.png");
        mediaObject4.setUrl("https://androidwave.com/media/androidwave-video-6.mp4");
        MediaObject mediaObject5 = new MediaObject();
        mediaObject5.setId(5);
        mediaObject5.setUserHandle("@monika.sharma");
        mediaObject5.setTitle("When did you last cry in front of someone?");
        mediaObject5.setCoverUrl(
                "https://androidwave.com/media/images/exo-player-in-recyclerview-in-android-5.png");
        mediaObject5.setUrl("https://androidwave.com/media/androidwave-video-5.mp4");
        videos.add(mediaObject);
        videos.add(mediaObject2);
        videos.add(mediaObject3);
        videos.add(mediaObject4);
        videos.add(mediaObject5);
        videos.add(mediaObject);
        videos.add(mediaObject2);
        videos.add(mediaObject3);
        videos.add(mediaObject4);
        videos.add(mediaObject5);
    }

}

