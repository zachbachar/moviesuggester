package top.zachapps.moviesuggester;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import top.zachapps.moviesuggester.UIAssets.Font;

/**
 * Created by zach on 10-Feb-16.
 */
public class ActivitySplash extends AppCompatActivity {

    class Layout{
        Layout(){
            icon = (ImageView) findViewById(R.id.icon);
            text = (TextView) findViewById(R.id.text);
        }
        ImageView icon;
        TextView text;
    }

    class Events{
        Events(){
            l.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSearch();
                }
            });
        }
    }

    Layout l;
    Events e;

    int tries;
    public static Activity activity;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        l = new Layout(); e = new Events();

        activity = this;
        tries = 0;
        startAnimation();
        Font.setFace(this, l.text);
    }

    private void startAnimation(){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_animations);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                goToSearch();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        l.icon.startAnimation(anim);
        l.text.startAnimation(anim);
    }

    /*private void checkConnection(){
        if (isOnline()){
            goToSearch();
        }
        else{
            Toast.makeText(this, "No internet Connection, Please Connect and Try Again", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }*/

    private void goToSearch(){
        Intent intent = new Intent(ActivitySplash.this, ActivityWatchList.class);
        startActivity(intent);
        finish();
    }
}

