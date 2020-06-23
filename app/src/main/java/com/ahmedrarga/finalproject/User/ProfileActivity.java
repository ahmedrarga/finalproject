package com.ahmedrarga.finalproject.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmedrarga.finalproject.BaseActivity;
import com.ahmedrarga.finalproject.ExoPlayer.ExoPlayerRecyclerView;
import com.ahmedrarga.finalproject.ExoPlayer.MediaRecyclerAdapter;
import com.ahmedrarga.finalproject.HomePage.HomeActivity;
import com.ahmedrarga.finalproject.HomePage.PosterAdapter;
import com.ahmedrarga.finalproject.MediaObject;
import com.ahmedrarga.finalproject.MovieProfile.Tracking;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.UserPost;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView cover;
    ImageView profile;
    TextView name;
    private ArrayList<Map<String, Object>> map;
    TextView email;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView posts;
    RecyclerView watching;
    ProgressBar watchingBar;
    TextView watchingMessage;
    List<String> postsArray = new ArrayList<>();
    String userName = "";
    ArrayList<MediaObject> videos = new ArrayList();
    ExoPlayerRecyclerView mRecyclerView;
    int uId = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


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

                                        name = findViewById(R.id.name);
                                        email = findViewById(R.id.email);
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
                            profile = findViewById(R.id.profile);
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
                        cover = findViewById(R.id.cover);
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .into(cover);
                    }
                });
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videos = new ArrayList<>();
        final MediaRecyclerAdapter mAdapter = new MediaRecyclerAdapter(videos, initGlide());
        mRecyclerView.setMediaObjects(videos);
        mRecyclerView.setAdapter(mAdapter);
        String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("userPosts")
                .document(m)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserPost post = documentSnapshot.toObject(UserPost.class);
                        if(post == null){
                            post = new UserPost();
                        }
                        for (String path : post.paths) {
                            MediaObject obj = new MediaObject();
                            obj.setUserHandle(userName);
                            obj.setPath(path);
                            videos.add(obj);
                        }
                    }
                });
    }
    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

}
