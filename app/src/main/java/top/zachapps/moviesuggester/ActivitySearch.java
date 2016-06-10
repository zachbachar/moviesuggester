package top.zachapps.moviesuggester;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import top.zachapps.moviesuggester.Adapters.SpinnerAdapter;
import top.zachapps.moviesuggester.Networking.JsonConnectivty;
import top.zachapps.moviesuggester.UIAssets.Font;
import top.zachapps.moviesuggester.logic.Movie;

/**
 * Created by zach on 31-Jan-16.
 */
public class ActivitySearch extends AppCompatActivity {

    class Layout{
        Layout(){
            userSearchMovie = (AutoCompleteTextView) findViewById(R.id.userSearchMovie);
            btnSearchMovie = (Button) findViewById(R.id.btnSearchMovie);
            progressBar = (ProgressBar) findViewById(R.id.progressbar);
            spinner = (Spinner) findViewById(R.id.spinner);
            layoutSpinner = (LinearLayout) findViewById(R.id.layoutSpinner);
            txtType = (TextView) findViewById(R.id.txtType);
        }
        AutoCompleteTextView userSearchMovie;
        Button btnSearchMovie;
        ProgressBar progressBar;
        TextView  txtType;
        Spinner spinner;
        LinearLayout layoutSpinner;
    }

    class Events{
        Events(){
            l.btnSearchMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (l.userSearchMovie.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Enter a Movie or a Series You Like", Toast.LENGTH_SHORT).show();
                    } else sendRequest();
                }
            });

            l.userSearchMovie.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        hideKeyboard();
                        if (editTextErrorListener(l.userSearchMovie)) {
                            sendRequest();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter a Movie or a Series You Like", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        boolean editTextErrorListener(EditText edit){
            if(edit.getText().toString().length() < 1){
                return false;
            }
            return true;
        }

        void hideKeyboard(){
            InputMethodManager in = (InputMethodManager) getSystemService(ActivitySearch.this.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(l.userSearchMovie.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        void showKeyboard(){
            InputMethodManager keyboard = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(l.userSearchMovie, 0);
        }
    }

    Layout l;
    Events e;
    public static JSONArray results;
    public static JSONObject movie;
    public static String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        l = new Layout(); e = new Events();

        init();
    }

    private void init(){
        ArrayList<String> autoComplete = readSet();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,autoComplete);
        l.userSearchMovie.setAdapter(adapter);

        Font.setFace(this, l.txtType);

        ArrayList<String> spinnerList = new ArrayList<String>();
        spinnerList.add("Select Type");
        spinnerList.add("Movie");
        spinnerList.add("Series");

        l.spinner.setAdapter(new SpinnerAdapter(spinnerList, this));
        Font.setFace(this, l.txtType);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                e.showKeyboard();
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getBooleanExtra("canBack", true)){
            super.onBackPressed();
        }
    }

    private void sendRequest() {
        if(l.spinner.getSelectedItem().toString().equals("Select Type")){
            Toast.makeText(this, "You Must Select a Type!", Toast.LENGTH_SHORT).show();
            return;
        }
        l.userSearchMovie.setVisibility(View.GONE);
        l.btnSearchMovie.setVisibility(View.GONE);
        l.layoutSpinner.setVisibility(View.GONE);
        l.progressBar.setVisibility(View.VISIBLE);

        String val = l.userSearchMovie.getText().toString().replaceAll(" ", "%20");
        selectedType = l.spinner.getSelectedItem().toString();

        String url = "https://www.tastekid.com/api/similar?q=" + val + "&type=" + selectedType + "&k=196832-MovieSug-EQCH8CU7&verbose=1";
        new JsonConnectivty().getJson(this, url, new JsonConnectivty.responseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getJSONObject("Similar").getJSONArray("Info").getJSONObject(0).getString("Type").equals("unknown")) {
                        Toast.makeText(getApplicationContext(), "Please check your Spell OR try Another Title", Toast.LENGTH_SHORT).show();
                        l.userSearchMovie.setVisibility(View.VISIBLE);
                        l.btnSearchMovie.setVisibility(View.VISIBLE);
                        l.layoutSpinner.setVisibility(View.VISIBLE);
                        l.progressBar.setVisibility(View.GONE);
                    } else {
                        results = response.getJSONObject("Similar").getJSONArray("Results");
                        movie = response.getJSONObject("Similar").getJSONArray("Info").getJSONObject(0);
                        ActivityList.movie = new Movie(getApplicationContext(), movie.getString("Name"), movie.getString("yID"));
                        ActivityList.movie.init(new Movie.ResponseListener() {
                            @Override
                            public void responseTrue() {
                                goToActivitySimilarList();
                            }

                            @Override
                            public void responseFalse() {
                                Toast.makeText(getApplicationContext(), "Please check your Spell OR try Another Title", Toast.LENGTH_SHORT).show();
                                l.userSearchMovie.setVisibility(View.VISIBLE);
                                l.btnSearchMovie.setVisibility(View.VISIBLE);
                                l.layoutSpinner.setVisibility(View.VISIBLE);
                                l.progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void goToActivitySimilarList(){
        Intent intent = new Intent(ActivitySearch.this, ActivityList.class);
        startActivity(intent);
        ActivitySplash.activity.finish();
        ActivitySearch.this.finish();
    }

    private ArrayList<String> readSet() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        HashSet<String> set = (HashSet<String>)prefs.getStringSet("auto_complete", new HashSet<String>());
        ArrayList<String> list = new ArrayList<String>();
        for(String item : set){
            list.add(item);
        }
        return list;
    }
}

