package top.zachapps.moviesuggester.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.zachapps.moviesuggester.ActivitySearch;
import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;

/**
 * Created by zach on 25-Feb-16.
 */
public class DialogSort extends DialogFragment {

    public interface DialogListener{
        void onWatchList();
        void onSortYear();
        void onSortRating();
        void onNewSearch();
        void onSortBest();
        void onWatchSearched();
    }

    class Layout{
        Layout(){
            sortYear = (LinearLayout) dialog.findViewById(R.id.sortYear);
            sortRating = (LinearLayout) dialog.findViewById(R.id.sortRating);
            newSearch = (LinearLayout) dialog.findViewById(R.id.newSearch);
            watchSearched = (LinearLayout) dialog.findViewById(R.id.watchSearched);
            sortBest = (LinearLayout) dialog.findViewById(R.id.sortBest);
            watchList = (LinearLayout) dialog.findViewById(R.id.watchList);
            descMovie = (TextView) dialog.findViewById(R.id.descMovie);
            txt = new TextView[]{
                    (TextView) dialog.findViewById(R.id.descMovie),
                    (TextView) dialog.findViewById(R.id.txtYear),
                    (TextView) dialog.findViewById(R.id.txtRating),
                    (TextView) dialog.findViewById(R.id.txtSearch),
                    (TextView) dialog.findViewById(R.id.txtWatch),
                    (TextView) dialog.findViewById(R.id.txtBest)
            };
        }
        LinearLayout sortYear, sortRating, newSearch, sortBest, watchSearched, watchList;
        TextView descMovie;
        TextView[] txt;
    }

    class Events{
        Events(final DialogListener listener){
            l.sortYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSortYear();
                    DialogSort.this.dismiss();
                }
            });

            l.sortRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSortRating();
                    DialogSort.this.dismiss();
                }
            });

            l.newSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNewSearch();
                    DialogSort.this.dismiss();
                }
            });

            l.sortBest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSortBest();
                    DialogSort.this.dismiss();
                }
            });

            l.watchSearched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWatchSearched();
                    DialogSort.this.dismiss();
                }
            });

            l.watchList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWatchList();
                    DialogSort.this.dismiss();
                }
            });
        }
    }

    View dialog;
    String movieTitle;
    DialogListener listener;
    Layout l;
    Events e;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        dialog = inflater.inflate(R.layout.dialog_sort, container, false);

        l = new Layout(); e = new Events(listener);

        for (TextView item: l.txt){
            Font.setFace(getContext(), item);
        }

        l.descMovie.setText(movieTitle + " Description");

        if(ActivitySearch.selectedType.equals("Series")){
            l.sortYear.setVisibility(View.GONE);
        }

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
    }

    public void setListener(DialogListener listener){
        this.listener = listener;
    }

    public void setDescMovie(String s){
        this.movieTitle = s;
    }
}

