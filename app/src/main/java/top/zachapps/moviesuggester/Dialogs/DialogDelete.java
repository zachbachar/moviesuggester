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
import android.widget.Button;
import android.widget.TextView;

import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;

/**
 * Created by zach on 25-Feb-16.
 */
public class DialogDelete extends DialogFragment {

    public interface DialogListener{
        void onYes(DialogDelete dialog);
        void onNo(DialogDelete dialog);
    }

    class Layout{
        Layout(){
            btnYes = (Button) dialog.findViewById(R.id.btnYes);
            btnNo = (Button) dialog.findViewById(R.id.btnNo);
            txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        }
        Button btnYes, btnNo;
        TextView txtTitle;
    }

    class Events{
        Events(final DialogListener listener){
            l.btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onYes(DialogDelete.this);
                }
            });

            l.btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNo(DialogDelete.this);
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
        dialog = inflater.inflate(R.layout.dialog_delete_movie, container, false);

        l = new Layout(); e = new Events(listener);
        Font.setFace(getContext(), l.txtTitle);

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

