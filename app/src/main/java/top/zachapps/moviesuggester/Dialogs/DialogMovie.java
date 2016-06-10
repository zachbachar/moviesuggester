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

import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;

/**
 * Created by zach on 25-Feb-16.
 */
public class DialogMovie extends DialogFragment {

    public interface DiallogListener{
        void onAddToWatchList();
        void onWatchTrailer();
        void onBackToList();
    }

    class Layout{
        Layout(){
            addToWatchList = (LinearLayout) dialog.findViewById(R.id.addToWatchList);
            watchTrailer = (LinearLayout) dialog.findViewById(R.id.watchTrailer);
            backToList = (LinearLayout) dialog.findViewById(R.id.backToList);
            txt = new TextView[]{
                    (TextView) dialog.findViewById(R.id.txtAdd),
                    (TextView) dialog.findViewById(R.id.txtBack),
                    (TextView) dialog.findViewById(R.id.txtWatch)
            };
        }
        LinearLayout addToWatchList, watchTrailer, backToList;
        TextView[] txt;
    }

    class Events {
        Events(final DiallogListener listener){
            l.addToWatchList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddToWatchList();
                    DialogMovie.this.dismiss();
                }
            });

            l.watchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWatchTrailer();
                    DialogMovie.this.dismiss();
                }
            });

            l.backToList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBackToList();
                    DialogMovie.this.dismiss();
                }
            });
        }
    }


    View dialog;
    DiallogListener listener;
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
        dialog = inflater.inflate(R.layout.dialog_movie, container, false);

        l = new Layout(); e = new Events(listener);

        for (TextView item: l.txt){
            Font.setFace(getContext(), item);
        }

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
    }

    public void setListener(DiallogListener listener){
        this.listener = listener;
    }
}

