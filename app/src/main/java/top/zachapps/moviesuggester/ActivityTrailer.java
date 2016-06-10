package top.zachapps.moviesuggester;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import top.zachapps.moviesuggester.Networking.JsonConnectivty;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zach on 15-Feb-16.
 */


public class ActivityTrailer extends YouTubeBaseActivity {

    class Layout{
        Layout(){
            youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        }
        YouTubePlayerView youTubePlayerView;
    }

    Layout l;

    final String API_KEY = "AIzaSyC4SsRCP9FQvzjUs-gNoDWWQIIphLSKtBQ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        l = new Layout();
        if(ActivityList.movie.getYoutube().equals("null") || ActivityList.movie.getYoutube().equals("")){
            searchYID();
        }else init(ActivityList.movie.getYoutube());
    }

    public void init(final String youtube){
        if(youtube.equals("null") || youtube.equals("")){
            Toast.makeText(ActivityTrailer.this, "Sorry, No Trailer Found", Toast.LENGTH_LONG).show();
            ActivityTrailer.this.finish();
            return;
        }
        l.youTubePlayerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.cueVideo(youtube);
                    player.setFullscreen(true);
                    player.play();
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "Failured to Initialize!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchYID(){
        String url = "https://www.tastekid.com/api/similar?q=" + ActivityList.movie.getTitle().replaceAll(" ", "%20") + "&type=movie&k=196832-MovieSug-EQCH8CU7&verbose=1";
        new JsonConnectivty().getJson(this, url, new JsonConnectivty.responseListener() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                String newYID = response.getJSONObject("Similar").getJSONArray("Info").getJSONObject(0).getString("yID");
                writePrefs(newYID);
                init(newYID);
            }
        });
    }

    private void writePrefs(String youtube) throws JSONException { //maybe will be saved as JSON
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ActivityList.movie.getTitle() + ActivityMovie.YOUTUBE_KEY, youtube);

        editor.apply();
    }
}

