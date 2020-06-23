package com.ahmedrarga.finalproject.HomePage;

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

import com.ahmedrarga.finalproject.MovieProfile.MovieProfileActivity;
import com.ahmedrarga.finalproject.R;
import com.ahmedrarga.finalproject.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class SearchResultsRecyclerAdapter extends
        RecyclerView.Adapter<SearchResultsRecyclerAdapter.ViewHolder> {

    private List<Movie> movies;
    private Context context;
    private String cl;
    public SearchResultsRecyclerAdapter(List<Movie> movies, Context context, String cl){
        this.movies = movies;
        this.context = context;
        this.cl = cl;
    }
    public void updateData(List<Movie> list){
        movies = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.search_results_row, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Movie movie = movies.get(position);

        // Set item views based on your views and data model
        //TextView textView = holder.nameTextView;
       // textView.setText(movie.getName());
        ImageView image = holder.image;
        System.out.println(movie.getPoster_path() + " lhkjhbvvuyvgyuvgjftfytfcjd ydyrd gjd     ydjydjydtjytd jyd jy");
        Picasso.get()
                .load(movie.getPoster_path())
                .error(R.drawable.ic_photo)
                .fit()
                .into(holder.image);
        holder.title.setText(movie.getName());




    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView image;
        public TextView title;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            image =  itemView.findViewById(R.id.movie_image);
            image.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            String media_type = "";
            if(cl.equals("movie"))
                media_type = "movie";
            else if (cl.equals("show")){
                media_type = "tv";
            }
            else
                media_type = movies.get(getPosition()).getMedia_type();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            db.collection("history")
                    .document(m)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        History hist = task.getResult().toObject(History.class);
                        if(hist == null) {
                            hist = new History();
                        }
                        hist.set(movies.get(getPosition()).getId(), movies.get(getPosition()).getMedia_type());
                        db.collection("history").document(m).set(hist);
                    }
                }
            });
            Intent intent = new Intent(context, MovieProfileActivity.class);
            intent.putExtra("id", movies.get(this.getPosition()).getId());
            intent.putExtra("media_type",  media_type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
