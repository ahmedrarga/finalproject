package com.ahmedrarga.finalproject.ExoPlayer;


import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.ahmedrarga.finalproject.MediaObject;
import com.ahmedrarga.finalproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Created by Morris on 03,June,2019
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    public FrameLayout mediaContainer;
    public ImageView mediaCoverImage, volumeControl, userImage;
    public ProgressBar progressBar;
    public RequestManager requestManager;
    private TextView title, userHandle;
    private View parent;
    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        mediaContainer = itemView.findViewById(R.id.mediaContainer);
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
        title = itemView.findViewById(R.id.tvTitle);
        userHandle = itemView.findViewById(R.id.tvUserHandle);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.ivVolumeControl);
        userImage = itemView.findViewById(R.id.userImage);
    }
    void onBind(MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
        title.setText(mediaObject.getTitle());
        userHandle.setText(mediaObject.getUserHandle());
        StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + mediaObject.getUser() + "/profile.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .into(userImage);
            }
        });


    }
}