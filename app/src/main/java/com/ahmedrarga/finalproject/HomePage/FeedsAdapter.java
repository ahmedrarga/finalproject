package com.ahmedrarga.finalproject.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.User.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class FeedsAdapter extends
        RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private ArrayList<Map<String,String>> posts;
    private Context context;
    public FeedsAdapter(ArrayList<Map<String,String>> posts, Context context){
        this.posts= posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.feeds_row, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Get the data model based on position
        final Map<String,String> post = posts.get(position);

        if(post != null) {
            final String[] path = post.get("path").split("/");
            holder.time.setText(path[path.length - 1]);
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            StorageReference pRef = ref.child("images/" + post.get("user") + "/profile.jpg");
            pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Picasso.get()
                                .load(task.getResult())
                                .placeholder(R.drawable.ic_person)
                                .into(holder.profile);
                    }
                }
            });
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("EMAIL", post.get("user"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String n = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                String text = n + " • " + post.get("title");
                                holder.name.setText(text);
                            }

                        }
                    });
            if (path[1].equals("images")) {
                holder.video.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                StorageReference iRef = storage.getReference(post.get("path"));
                iRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.ic_fulll)
                                .fit()
                                .into(holder.image);

                    }
                });
            } else {
                holder.view.setMinimumHeight(100);
                holder.video.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
                holder.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        mediaPlayer.pause();
                    }
                });
                holder.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        holder.video.start();
                    }
                });
                StorageReference vRef = storage.getReference(post.get("path"));
                vRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                holder.video.setVideoURI(uri);
                            }
                        });

            }
        }

    }
    public void updateData(ArrayList<Map<String, String>> map){
        posts.addAll(map);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView image;
        public TextView name;
        public TextView time;
        public ImageView profile;
        public VideoView video;
        public CardView view;
        public LinearLayout user;
        private boolean isPlaying = false;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            setIsRecyclable(false);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            image =  itemView.findViewById(R.id.post_image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
            user = itemView.findViewById(R.id.user);
            view = itemView.findViewById(R.id.image);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }
            });
            video = itemView.findViewById(R.id.video);
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(video.isPlaying()) {
                        video.pause();
                    }else{
                        video.start();
                    }

                }
            });

        }



    }

}
