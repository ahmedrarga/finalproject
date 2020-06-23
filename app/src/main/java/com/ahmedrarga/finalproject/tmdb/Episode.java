package com.ahmedrarga.finalproject.tmdb;

import androidx.annotation.NonNull;

import com.ahmedrarga.finalproject.MovieProfile.MovieProfileActivity;
import com.ahmedrarga.finalproject.MovieProfile.Tracking;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Episode {
    private int id;
    private int s_id;
    private JSONObject obj;
    private static String IMAGE_PATH = "https://image.tmdb.org/t/p/w500";
    private boolean watched = false;

    public Episode(final int id, final int s_id, JSONObject obj){
        this.id = id;
        this.s_id = s_id;
        this.obj = obj;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Tracking")
                .document(m)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Tracking t = task.getResult().toObject(Tracking.class);
                            if(t == null){
                                watched = false;
                            }
                            else {
                                if (t.tracking == null) {
                                    watched = false;

                                } else {
                                    if(t.tracking.get(String.valueOf(id)) == null){
                                        watched = false;
                                    }else {
                                        for (Map<String, String> m : t.tracking.get(String.valueOf(id))) {
                                            if (m.get("season").equals(String.valueOf(s_id)) && m.get("episode").equals(String.valueOf(getEpisodeNumber()))) {
                                                watched = true;
                                            }
                                        }
                                    }
                            }
                            }
                        }
                    }
                });
    }

    public String getImage(){
        try{
            String s = obj.getString("still_path");
            if(s.equals("null")){
                return "";
            }
            return IMAGE_PATH + s;
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public String getEpisode(){
        try{
            return obj.getString("name");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public int getEpisodeNumber(){
        try{
            return obj.getInt("episode_number");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }
    public String getAirDate(){
        try{
            return obj.getString("air_date");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public String getRating(){
        try{
            return obj.getString("vote_average");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public String getOverview(){
        try{
            return obj.getString("overview");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public boolean isWatched() {
        return watched;

    }


    public int getId() {
        return id;
    }

    public int getS_id() {
        return s_id;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
