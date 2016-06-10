package top.zachapps.moviesuggester;


/**
 * Created by zach on 09-Feb-16.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import top.zachapps.moviesuggester.CustomViews.ViewMovieHeader;
import top.zachapps.moviesuggester.CustomViews.ViewMovieInfo;
import top.zachapps.moviesuggester.Dialogs.DialogMovie;
import top.zachapps.moviesuggester.UIAssets.BaseActivity;

public class ActivityMovie extends BaseActivity implements ObservableScrollViewCallbacks {

    class Layout{
        Layout(){
            imageView = (ImageView) findViewById(R.id.image);
            headerInfo = (ViewMovieHeader) findViewById(R.id.headerInfo);
            movieInfo = (ViewMovieInfo) findViewById(R.id.movieInfo);
            overlayView = findViewById(R.id.overlay);
            scrollView = (ObservableScrollView) findViewById(R.id.scroll);
            titleView = (TextView) findViewById(R.id.title);
            fab = findViewById(R.id.fab);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        }
        ImageView imageView;
        View overlayView;
        ObservableScrollView scrollView;
        TextView titleView;
        ViewMovieHeader headerInfo;
        ViewMovieInfo movieInfo;
        View fab;
        CoordinatorLayout coordinatorLayout;
    }

    class Events{
        Events(){
            l.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            ScrollUtils.addOnGlobalLayoutListener(l.scrollView, new Runnable() {
                @Override
                public void run() {
                    //mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                    // If you'd like to start from scrollY == 0, don't write like this:
                    // You can also achieve it with the following codes.
                    // This causes scroll change from 1 to 0.
                    l.scrollView.scrollTo(0, 1);
                    l.scrollView.scrollTo(0, 0);
                }
            });
        }
    }

    public static final String YOUTUBE_KEY = "yID";
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;
    Snackbar snackbar;

    Layout l;
    Events e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        l = new Layout(); e = new Events();

        init();
        snackbar.make(l.coordinatorLayout, "Scroll Down For More Info", Snackbar.LENGTH_SHORT).show();
    }

    private void init(){
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();

        Picasso.with(getApplicationContext()).load(ActivityList.movie.getPoster()).error(R.mipmap.error).into(l.imageView);
        l.headerInfo.updateHeader(ActivityList.movie);
        l.movieInfo.updateInfo(ActivityList.movie);
        l.scrollView.setScrollViewCallbacks(this);
        l.titleView.setText(""); // <-- toolbar text
        setTitle(null);

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(l.fab, 0);
        ViewHelper.setScaleY(l.fab, 0);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - l.overlayView.getHeight();
        ViewHelper.setTranslationY(l.overlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(l.imageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(l.overlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(l.titleView, 0);
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
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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

        DialogMovie fr = new DialogMovie();
        fr.setListener(new DialogMovie.DiallogListener() {
            @Override
            public void onAddToWatchList() {
                try {
                    writePrefs();

                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Did Not Added To Watch List", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onWatchTrailer() {
                Intent intent = new Intent(ActivityMovie.this, ActivityTrailer.class);
                intent.putExtra("youtube", ActivityList.movie.getYoutube());
                startActivity(intent);
            }

            @Override
            public void onBackToList() {
                ActivityMovie.super.onBackPressed();
            }
        });

        fr.show(fm, "Dialog Movie");
    }

    private void writePrefs() throws JSONException { //maybe will be saved as JSON
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray arr = new JSONArray(prefs.getString("watch_list", new JSONArray().toString()));

        boolean exist = false;
        for(int i = 0; i < arr.length(); i++){
            if(arr.getJSONObject(i).getString("Title").equals(ActivityList.movie.getTitle())){
                exist = true;
            }
        }
        if(!exist){
            arr.put(ActivityList.movie.getJsonObject());
            editor.putString(ActivityList.movie.getTitle() + YOUTUBE_KEY, ActivityList.movie.getYoutube());
        }else{
            Toast.makeText(getApplicationContext(), "Movie Already Added To Watch List", Toast.LENGTH_SHORT).show();
            return;
        }
        editor.putString("watch_list", arr.toString());
        editor.apply();
        Toast.makeText(getApplicationContext(), "Added To Watch List", Toast.LENGTH_SHORT).show();
    }

}


