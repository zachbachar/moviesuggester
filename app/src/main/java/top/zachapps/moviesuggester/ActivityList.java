package top.zachapps.moviesuggester;


/**
 * Created by zach on 09-Feb-16.
 */

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;

import top.zachapps.moviesuggester.Adapters.MovieBaseAdapter;
import top.zachapps.moviesuggester.Dialogs.DialogSort;
import top.zachapps.moviesuggester.UIAssets.BaseActivity;
import top.zachapps.moviesuggester.logic.Movie;
import top.zachapps.moviesuggester.logic.MoviesList;

public class ActivityList extends BaseActivity implements ObservableScrollViewCallbacks {

    class Layout{
        Layout(){
            imageView = (ImageView) findViewById(R.id.image);
            overlayView = findViewById(R.id.overlay);
            titleView = (TextView) findViewById(R.id.title);
            fab = findViewById(R.id.fab);
            listBackgroundView = findViewById(R.id.list_background);
            listView = (ObservableListView) findViewById(R.id.list);
            progBar = (ProgressBar) findViewById(R.id.progBar);
        }
        ObservableListView listView;
        ImageView imageView;
        View overlayView;
        View listBackgroundView;
        TextView titleView;
        View fab;
        ProgressBar progBar;
    }

    class Events{
        Events(){
            l.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        }
        public void setOnItemClick(ListView view){
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    movie = (Movie) adapter.getItem(position-1);
                    Intent intent = new Intent(ActivityList.this, ActivityMovie.class);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        View image = view.findViewById(R.id.icon);
                        View title = view.findViewById(R.id.firstLine);
                        View year = view.findViewById(R.id.year);
                        View rating = view.findViewById(R.id.rating);
                        View geners = view.findViewById(R.id.geners);

                        Pair<View, String> pair1 = Pair.create(image, image.getTransitionName());
                        Pair<View, String> pair2 = Pair.create(title, title.getTransitionName());
                        Pair<View, String> pair3 = Pair.create(rating, rating.getTransitionName());
                        Pair<View, String> pair4 = Pair.create(year, year.getTransitionName());
                        Pair<View, String> pair5 = Pair.create(geners, geners.getTransitionName());

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(ActivityList.this, pair1, pair2, pair3, pair4, pair5);
                        startActivity(intent, options.toBundle());
                    }else{
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;
    MovieBaseAdapter adapter;
    static Movie movie;
    private Movie searchedMovie;
    MoviesList moviesList;

    Layout l;
    Events e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        l = new Layout(); e = new Events();

        initUI();
        initMoviesList();
        logicInit();
        try {
            writeAutoComplete(movie.getTitle());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void initUI(){
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();

        l.listView.setScrollViewCallbacks(this);
        e.setOnItemClick(l.listView);

        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mFlexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);
        l.listView.addHeaderView(paddingView);

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(l.fab, 0);
        ViewHelper.setScaleY(l.fab, 0);

        setTitle(null);
    }

    private void logicInit(){
        //searched movie image
        movie.init(new Movie.ResponseListener() {
            @Override
            public void responseTrue() {
                Picasso.with(getApplicationContext()).load(movie.getPoster()).error(R.mipmap.error).into(l.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        l.progBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        l.progBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void responseFalse() {

            }
        });

        //new movie object for searched movie description
        searchedMovie = new Movie(this, movie.getTitle(), movie.getYoutube());
        searchedMovie.init(new Movie.ResponseListener() {
            @Override
            public void responseTrue() {}
            @Override
            public void responseFalse() {}
        });
    }

    private void initMoviesList(){
        moviesList = new MoviesList(getApplicationContext(), ActivitySearch.results, new MoviesList.MoviesListListener() {
            @Override
            public void onListFirstInit(ArrayList<Movie> list) {
                setAdapter(list);
                l.listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onListAddMovie(ArrayList<Movie> list, Movie movie) {
                adapter.notifyDataSetChanged();
                l.listView.invalidateViews();
                try {
                    writeAutoComplete(movie.getTitle());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onListReady(ArrayList<Movie> list) {

            }
        });
    }

    private void setAdapter(ArrayList<Movie> list){
        adapter = new MovieBaseAdapter(list, this);
        l.listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        l.listView.invalidateViews();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quit")
                    .setMessage("Are You Sure You Want To Exit Movie Suggester?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop the activity
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - l.overlayView.getHeight();
        ViewHelper.setTranslationY(l.overlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(l.imageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(l.listBackgroundView, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(l.overlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        //setPivotXToTitle();
        ViewHelper.setPivotY(l.titleView, 0);
        ViewHelper.setScaleX(l.titleView, scale);
        ViewHelper.setScaleY(l.titleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - l.titleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(l.titleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - l.fab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - l.fab.getHeight() / 2,
                mActionBarSize - l.fab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) l.fab.getLayoutParams();
            lp.leftMargin = l.overlayView.getWidth() - mFabMargin - l.fab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            l.fab.requestLayout();
        } else {
            ViewHelper.setTranslationX(l.fab, l.overlayView.getWidth() - mFabMargin - l.fab.getWidth());
            ViewHelper.setTranslationY(l.fab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {
        //anable list animations
        adapter.animations = true;
        l.listView.clearAnimation();
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(l.titleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(l.titleView, 0);
        }
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(l.fab).cancel();
            ViewPropertyAnimator.animate(l.fab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(l.fab).cancel();
            ViewPropertyAnimator.animate(l.fab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    void showDialog(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction();

        DialogSort fragment = new DialogSort();
        fragment.setDescMovie(searchedMovie.getTitle());
        fragment.setListener(new DialogSort.DialogListener() {
            @Override
            public void onWatchList() {
                Intent intent = new Intent(ActivityList.this, ActivityWatchList.class);
                startActivity(intent);
            }

            @Override
            public void onSortYear() {
                moviesList.sortByYear();
                ArrayList<Movie> list = moviesList.get();
                setAdapter(list);
            }

            @Override
            public void onSortRating() {
                moviesList.sortByRating();
                ArrayList<Movie> list = moviesList.get();
                setAdapter(list);
            }

            @Override
            public void onNewSearch() {
                Intent i = new Intent(ActivityList.this, ActivitySearch.class);
                i.putExtra("canBack", true);
                startActivity(i);
            }

            @Override
            public void onSortBest() {
                moviesList.sortByBestMatch();
                ArrayList<Movie> list = moviesList.get();
                setAdapter(list);
            }

            @Override
            public void onWatchSearched() {
                movie = searchedMovie;
                Intent intent = new Intent(ActivityList.this, ActivityMovie.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    l.imageView.setTransitionName("icon");
                    l.titleView.setTransitionName("title");

                    View image = l.imageView;
                    View title = l.titleView;

                    Pair<View, String> pair1 = Pair.create(image, image.getTransitionName());
                    Pair<View, String> pair2 = Pair.create(title, title.getTransitionName());

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(ActivityList.this, pair1, pair2);

                    startActivity(intent, options.toBundle());
                }else startActivity(intent);
            }
        });
        fragment.show(fm, "Dialog Sort");
    }

    private void writeAutoComplete(String newTitle) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        HashSet<String> set = (HashSet<String>)prefs.getStringSet("auto_complete", new HashSet<String>());

        for(String item : set){
            if (item.equals(newTitle)){
                return;
            }
        }
        set.add(newTitle);
        editor.putStringSet("auto_complete", set);
        editor.apply();
    }
}


