package com.ahmedrarga.finalproject.MovieProfile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmedrarga.finalproject.ExoPlayer.ExoPlayerRecyclerView;
import com.ahmedrarga.finalproject.ExoPlayer.MediaRecyclerAdapter;
import com.ahmedrarga.finalproject.HomePage.DateSorting;
import com.ahmedrarga.finalproject.HomePage.FeedsAdapter;
import com.ahmedrarga.finalproject.MediaObject;
import com.ahmedrarga.finalproject.Post;
import com.ahmedrarga.finalproject.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Posts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Posts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ExoPlayerRecyclerView mRecyclerView;
    ArrayList<MediaObject> videos = new ArrayList<>();
    int uId = 0;
    boolean flag = false;

    private OnFragmentInteractionListener mListener;

    public Posts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Posts.
     */
    // TODO: Rename and change types and number of parameters
    public static Posts newInstance(String param1, String param2) {
        Posts fragment = new Posts();
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
        final View v =  inflater.inflate(R.layout.fragment_posts, container, false);
        mRecyclerView = v.findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        videos = new ArrayList<>();
        final MediaRecyclerAdapter mAdapter = new MediaRecyclerAdapter(videos, initGlide());
        mRecyclerView.setMediaObjects(videos);
        mRecyclerView.setAdapter(mAdapter);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("posts").document(String.valueOf(((MovieProfileActivity)getActivity()).movie.getId()))
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
        });




        return v;
    }
    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
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
}
