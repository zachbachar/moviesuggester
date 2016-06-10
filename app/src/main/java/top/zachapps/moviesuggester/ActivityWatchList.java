package top.zachapps.moviesuggester;

/**
 * Created by zach on 19-Feb-16.
 */

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import top.zachapps.moviesuggester.Adapters.MovieBaseAdapter;
import top.zachapps.moviesuggester.Dialogs.DialogDelete;
import top.zachapps.moviesuggester.Dialogs.DialogWatchList;
import top.zachapps.moviesuggester.UIAssets.BaseActivity;
import top.zachapps.moviesuggester.logic.Movie;
import top.zachapps.moviesuggester.logic.MoviesList;

public class ActivityWatchList extends BaseActivity implements ObservableScrollViewCallbacks {

    class Layout{
        Layout(){
            imageView = findViewById(R.id.image);
            toolBarView = findViewById(R.id.toolbar);
            listView = (ObservableListView) findViewById(R.id.list);
            listBackgroundView = findViewById(R.id.list_background);
            btnDialog = (ImageView) findViewById(R.id.btnDialog);
            btnSearch = (ImageView) findViewById(R.id.btnSearch);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        }
        View imageView;
        View toolBarView;
        View listBackgroundView;
        ObservableListView listView;
        ImageView btnSearch, btnDialog;
        CoordinatorLayout coordinatorLayout;
    }

    class Events{
        Events(){
            l.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ActivityList.movie = (Movie) adapter.getItem(position - 1);
                    Intent intent = new Intent(ActivityWatchList.this, ActivityMovie.class);

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
                                makeSceneTransitionAnimation(ActivityWatchList.this, pair1, pair2, pair3, pair4, pair5);
                        startActivity(intent, options.toBundle());
                    }else{
                        startActivity(intent);
                    }
                }
            });

            l.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction();
                    DialogDelete fragment = new DialogDelete();
                    fragment.setListener(new DialogDelete.DialogListener() {
                        @Override
                        public void onYes(DialogDelete dialog) {
                            deleteFromList(position, view);
                            dialog.dismiss();
                        }

                        @Override
                        public void onNo(DialogDelete dialog) {
                            dialog.dismiss();
                        }
                    });
                    fragment.show(fm, "Dialog Delete");
                    return true;
                }
            });

            l.btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityWatchList.this, ActivitySearch.class);
                    startActivity(intent);
                }
            });

            l.btnDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        }
    }

    Layout l;
    Events e;

    private int mParallaxImageHeight;
    MovieBaseAdapter adapter;
    ArrayList<Movie> list;
    JSONArray arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list) ;

        l = new Layout(); e = new Events();

        initUI();
        try {
            initWatchList(readPrefsWatchList());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        setSnackbar();
    }

    private ArrayList<JSONObject> readPrefsWatchList() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        arr = new JSONArray(prefs.getString("watch_list", new JSONArray().toString()));
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();

        for(int i = arr.length()-1; i >= 0; i--){
            list.add(new JSONObject(arr.getString(i)));
        }
        return list;
    }

    private void initWatchList(ArrayList<JSONObject> jsonObjects) throws JSONException {
        new MoviesList(getApplicationContext(), jsonObjects, PreferenceManager.getDefaultSharedPreferences(this), new MoviesList.MoviesListListener() {
            @Override
            public void onListFirstInit(ArrayList<Movie> list) {}
            @Override
            public void onListAddMovie(ArrayList<Movie> list, Movie movie) {}

            @Override
            public void onListReady(ArrayList<Movie> list) {
                adapter = new MovieBaseAdapter(list, ActivityWatchList.this);
                l.listView.setAdapter(adapter);
                ActivityWatchList.this.list = list;
            }
        });
    }

    private void initUI(){
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        l.toolBarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        l.listView.setScrollViewCallbacks(this);
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mParallaxImageHeight);
        paddingView.setLayoutParams(lp);
        // This is required to disable header's list selector effect
        paddingView.setClickable(true);
        l.listView.addHeaderView(paddingView);
    }

    private void setSnackbar(){
        if(arr.length() == 0){
            Snackbar.make(l.coordinatorLayout, "Your Watch List Is Empty! Add Movies To See Them Here!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SEARCH", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ActivityWatchList.this, ActivitySearch.class);
                            startActivity(intent);
                        }
                    }).show();
        }else{
            Snackbar.make(l.coordinatorLayout, "Long Click To Delete Movie", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void deleteMovie(final String title) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray arr = new JSONArray(prefs.getString("watch_list", new JSONArray().toString()));

        int toRemove = -1;
        for(int i = 0; i < arr.length(); i++){
            if(arr.getJSONObject(i).getString("Title").equals(title)){
                toRemove = i;
            }
        }
        if (toRemove != -1){
            arr.remove(toRemove);
            editor.remove(title + ActivityMovie.YOUTUBE_KEY);
        }
        editor.putString("watch_list", arr.toString());
        editor.apply();
    }

    private void deleteFromList(final int position, View view){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.item_slide_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                adapter.animations = false;
                Movie movie = (Movie) adapter.getItem(position - 1);
                list.remove(position - 1);
                adapter.notifyDataSetChanged();

                try {
                    deleteMovie(movie.getTitle());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void sortByAtoZ() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        JSONArray arr = new JSONArray(prefs.getString("watch_list", new JSONArray().toString()));
        ArrayList<JSONObject> newList = new ArrayList<JSONObject>();

        char checked = 'A';
        char last = 'Z';
        for(int i = (int)checked; i <= (int)last; i++){
            for(int j = 0; j < arr.length(); j++){
                JSONObject obj = arr.getJSONObject(j);
                String title = obj.getString("Title");
                char firstChar = title.charAt(0);
                if(firstChar == i){
                    newList.add(arr.getJSONObject(j));
                }
            }
        }
        for(int i = 0; i < arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            String title = obj.getString("Title");
            char firstChar = title.charAt(0);
            if(firstChar < checked){
                newList.add(arr.getJSONObject(i));
            }
        }
        initWatchList(newList);
    }

    void showDialog(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction();

        DialogWatchList fr = new DialogWatchList();
        fr.setListener(new DialogWatchList.DialogListener() {
            @Override
            public void onRefresh(DialogWatchList dialog) {
                try {
                    initWatchList(readPrefsWatchList());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onSortAlpha(DialogWatchList dialog) {
                try {
                    sortByAtoZ();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onCredits(DialogWatchList dialog) {
                Intent intent = new Intent(ActivityWatchList.this, ActivityCredits.class);
                dialog.dismiss();
                startActivity(intent);
            }
        });

        fr.show(fm, "Dialog Watch List");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quit")
                    .setMessage("Are You Sure You Want To Exit Movie Suggester?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(l.listView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        l.toolBarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(l.imageView, -scrollY / 2);

        // Translate list background
        ViewHelper.setTranslationY(l.listBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
    }

    @Override
    public void onDownMotionEvent() {
        //anable list animations
        if(!adapter.animations) {
            adapter.animations = true;
            l.listView.clearAnimation();
        }
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}
