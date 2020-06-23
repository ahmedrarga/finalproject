package com.ahmedrarga.finalproject;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class MediaObject {
    private int uId;
    private String title;
    private String mediaUrl;
    String user;
    private String mediaCoverImgUrl;
    private String userHandle;
    private String path;
    public String getUserHandle() {
        return userHandle;
    }
    public void setUserHandle(String mUserHandle) {
        this.userHandle = mUserHandle;
    }
    public int getId() {
        return uId;
    }
    public void setId(int uId) {
        this.uId = uId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String mTitle) {
        this.title = mTitle;
    }
    public String getUrl() {
        return mediaUrl;
    }
    public void setUrl(String mUrl) {
        this.mediaUrl = mUrl;
    }
    public String getCoverUrl() {
        return mediaCoverImgUrl;
    }
    public void setCoverUrl(String mCoverUrl) {
        this.mediaCoverImgUrl = mCoverUrl;
    }

    public void setObject(final Map<String,String> obj) {
        setTitle(obj.get("title"));
        setPath(obj.get("path"));
        setUser(obj.get("user"));

    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}