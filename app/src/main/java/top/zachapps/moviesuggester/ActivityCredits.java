package top.zachapps.moviesuggester;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import top.zachapps.moviesuggester.UIAssets.Font;

/**
 * Created by zach on 06-Mar-16.
 */
public class ActivityCredits extends AppCompatActivity {


    class Layout{
        Layout(){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            btnBack = (ImageView) findViewById(R.id.btnBack);
            txt1 = (TextView) findViewById(R.id.txt1);
            txtTasteKid = (TextView) findViewById(R.id.txtTasteKid);
            txtOmdb = (TextView) findViewById(R.id.txtOmdb);
        }
        Toolbar toolbar;
        ImageView btnBack;
        TextView txt1, txtTasteKid, txtOmdb;
    }

    class Events{
        Events(){
            l.txtTasteKid.setMovementMethod(LinkMovementMethod.getInstance());
            l.txtOmdb.setMovementMethod(LinkMovementMethod.getInstance());

            l.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCredits.this.onBackPressed();
                }
            });
        }
    }

    Layout l;
    Events e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        l = new Layout(); e = new Events();
        init();

    }

    private void init(){
        setSupportActionBar(l.toolbar);
        Font.setFace(this, l.txtOmdb);
        Font.setFace(this, l.txt1);
        Font.setFace(this, l.txtTasteKid);
    }
}
