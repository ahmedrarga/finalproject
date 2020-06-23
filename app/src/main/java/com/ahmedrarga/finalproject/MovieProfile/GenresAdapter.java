package com.ahmedrarga.finalproject.MovieProfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GenresAdapter extends
        RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    private List<String> genres;
    private Context context;
    private String cl;
    public GenresAdapter(List<String> movies, Context context){
        this.genres = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.genres_layout, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String movie = genres.get(position);

        // Set item views based on your views and data model
        //TextView textView = holder.nameTextView;
        // textView.setText(movie.getName());
        TextView text = holder.text;
        String s = genres.get(position);
        text.setText(s);



    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public TextView text;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            text =  itemView.findViewById(R.id.genre);
            text.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
