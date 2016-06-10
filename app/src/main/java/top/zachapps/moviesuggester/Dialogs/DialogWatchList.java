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
 * Created by zach on 29-Feb-16.
 */
public class DialogWatchList extends DialogFragment {

    public interface DialogListener{
        void onRefresh(DialogWatchList dialog);
        void onSortAlpha(DialogWatchList dialog);
        void onCredits(DialogWatchList dialog);
    }

    class Layout{
        Layout(){
            txt = new TextView[]{
                    (TextView) dialog.findViewById(R.id.txtRefresh),
                    (TextView) dialog.findViewById(R.id.txtSort),
                    (TextView) dialog.findViewById(R.id.txtCredits)
            };
            refresh = (LinearLayout) dialog.findViewById(R.id.refresh);
            sortAlpha = (LinearLayout) dialog.findViewById(R.id.sortAlpha);
            credits = (LinearLayout) dialog.findViewById(R.id.credits);
        }
        TextView[] txt;
        LinearLayout refresh, sortAlpha, credits;
    }

    class Events{
        Events(final DialogListener listener){
            l.sortAlpha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSortAlpha(DialogWatchList.this);
                }
            });

            l.refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefresh(DialogWatchList.this);
                }
            });

            l.credits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCredits(DialogWatchList.this);
                }
            });
        }
    }

    View dialog;
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
        dialog = inflater.inflate(R.layout.dialog_watchlist, container, false);

        l = new Layout(); e = new Events(listener);

        for(TextView item: l.txt){
            Font.setFace(getContext(), item);
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
}

