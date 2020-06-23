package com.ahmedrarga.finalproject.User;

import com.ahmedrarga.finalproject.tmdb.Movie;

import java.util.List;

public interface OnFinished {
    void finished(List<Movie> movies);
}
