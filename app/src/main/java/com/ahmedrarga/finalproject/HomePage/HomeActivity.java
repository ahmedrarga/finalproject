package com.ahmedrarga.finalproject.HomePage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ahmedrarga.finalproject.Account.MainActivity;
import com.ahmedrarga.finalproject.Account.SettingsActivity;
import com.ahmedrarga.finalproject.Account.resetPassword;
import com.ahmedrarga.finalproject.ExoPlayer.ExoPlayerRecyclerView;
import com.ahmedrarga.finalproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Handler;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;


public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        WatchlistFragment.OnFragmentInteractionListener, RecommendationsFragment.OnFragmentInteractionListener,
        MeFragment.OnFragmentInteractionListener, News.OnFragmentInteractionListener, Feeds.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener{
    private TextView mTextMessage;
    Fragment active = null;
    FragmentManager fm;
    Fragment home;
    Fragment watchlist;
    Fragment recommendations;
    Fragment me;
    Toolbar toolbar;
    Fragment search;
    BottomNavigationView navView;
    static String query;
    public static final String api_key = "f98d888dd7ebd466329c6a26f1018a55";

    Handler handler = new Handler();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    fm.beginTransaction().hide(active).show(home).commit();
                    active = home;
                    ExoPlayerRecyclerView.videoPlayer.stop();
                    return true;
                case R.id.watchlist:
                    fm.beginTransaction().hide(active).show(watchlist).commit();
                    active = watchlist;
                    ExoPlayerRecyclerView.videoPlayer.stop();
                    return true;
                case R.id.recs:
                    fm.beginTransaction().hide(active).show(recommendations).commit();
                    active = recommendations;
                    ExoPlayerRecyclerView.videoPlayer.stop();
                    return true;
                case R.id.me:
                    fm.beginTransaction().hide(active).show(me).commit();
                    active = me;
                    setSupportActionBar(null);
                    ExoPlayerRecyclerView.videoPlayer.stop();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.dashboard);
        if(getSupportActionBar() != null) {
           // getSupportActionBar().setIcon(R.drawable.ic_fulll);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        fm = getSupportFragmentManager();
        home = new HomeFragment();
        watchlist = new WatchlistFragment();
        recommendations = new RecommendationsFragment();
        search = new SearchFragment();
        me = new MeFragment();
        fm.beginTransaction().add(R.id.main_container, me, "4").hide(me).commit();
        fm.beginTransaction().add(R.id.main_container, recommendations, "3").hide(recommendations).commit();
        fm.beginTransaction().add(R.id.main_container, watchlist, "2").hide(watchlist).commit();
        fm.beginTransaction().add(R.id.main_container, home, "1").hide(home).commit();
        fm.beginTransaction().add(R.id.main_container, search, "0").hide(search).commit();

        fm.beginTransaction().show(home).commit();
        active = home;
        navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if(isOpen){
                            navView.setVisibility(View.INVISIBLE);
                        }else{
                            navView.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                fm.beginTransaction().hide(active).show(search).commit();
                active = search;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                fm.beginTransaction().hide(active).show(home).commit();
                active = home;
                navView.setVisibility(View.VISIBLE);
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                query = q;
                ((SearchFragment)search).performSearch(q);
                return false;
            }
            @Override
            public boolean onQueryTextChange(final String newText) {
                query = newText;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((SearchFragment)search).performSearch(query);
                    }
                }, 300);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                fm.beginTransaction().hide(active).show(search).commit();
                navView.setVisibility(View.GONE);
                active = search;
                return true;
            case R.id.setting:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.change_pass:
                Intent intent1 = new Intent(getApplicationContext(), resetPassword.class);
                startActivity(intent1);
                return true;
            case R.id.logout:
                signOut();
                return true;
        }
        return false;
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
