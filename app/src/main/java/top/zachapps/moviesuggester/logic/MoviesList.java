package top.zachapps.moviesuggester.logic;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import top.zachapps.moviesuggester.ActivityMovie;

/**
 * Created by zach on 01-Feb-16.
 */
public class MoviesList {

    public interface MoviesListListener{
        void onListFirstInit(ArrayList<Movie> list);
        void onListAddMovie(ArrayList<Movie> list, Movie movie);
        void onListReady(ArrayList<Movie> list);
    }

    public MoviesList(Context ctx, JSONArray arr, MoviesListListener listener){
        this.ctx = ctx;
        list = new ArrayList<Movie>();
        originalList = new ArrayList<Movie>();
        try {
            init(arr, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MoviesList(Context ctx,ArrayList<JSONObject> jsonObjects, SharedPreferences prefs, MoviesListListener listener) throws JSONException {
        this.ctx = ctx;
        list = new ArrayList<Movie>();
        originalList = new ArrayList<Movie>();
        init(jsonObjects, prefs, listener);
    }

    private Context ctx;
    private ArrayList<Movie> list;
    final private ArrayList<Movie> originalList;

    public void init(JSONArray arr, final MoviesListListener listener) throws JSONException {
        listener.onListFirstInit(list);
        for(int i = 0; i < arr.length(); i++){
            String yID = arr.getJSONObject(i).getString("yID");
            final Movie movie = new Movie(ctx, arr.getJSONObject(i).getString("Name"), yID);
            movie.init(new Movie.ResponseListener() {
                @Override
                public void responseTrue() {
                    list.add(movie);
                    originalList.add(movie);
                    listener.onListAddMovie(list, movie);
                }
                @Override
                public void responseFalse() {

                }
            });
        }
        listener.onListReady(list);
    }

    private void init(final ArrayList<JSONObject> jsonObjects, SharedPreferences prefs, final MoviesListListener listener) throws JSONException {
        listener.onListFirstInit(list);
        for(JSONObject item: jsonObjects){
            String yID = prefs.getString(item.getString("Title") + ActivityMovie.YOUTUBE_KEY, "");
            Movie movie = new Movie(item, yID);
            list.add(movie);
            listener.onListAddMovie(list, movie);
        }
        listener.onListReady(list);
    }

    public ArrayList<Movie> get(){
        return list;
    }

    public void sortByRating(){
        ArrayList<Movie> newList = new ArrayList<Movie>();
        double topRated;

        int movies = list.size();
        for (int i = 0; i < movies; i++) {
            topRated = 0;
            for (Movie item : list) {
                if (Double.parseDouble(item.getImdbRating()) > topRated) {
                    topRated = Double.parseDouble(item.getImdbRating());
                }
            }
            for (Movie item : list) {
                if (Double.parseDouble(item.getImdbRating()) == topRated) {
                    newList.add(item);
                    break;
                }
            }
            list.remove(newList.get(newList.size() - 1));
        }
        list = newList;
    }

    public void sortByYear(){
        ArrayList<Movie> newList = new ArrayList<Movie>();
        int year;

        int movies = list.size();
        for (int i = 0; i < movies; i++) {
            year = 0;
            for (Movie item : list) {
                if (Integer.parseInt(item.getYear()) > year) {
                    year = Integer.parseInt(item.getYear());
                }
            }
            for (Movie item : list) {
                if (Integer.parseInt(item.getYear()) == year) {
                    newList.add(item);
                    break;
                }
            }
            list.remove(newList.get(newList.size() - 1));
        }
        list = newList;
    }

    public void sortByBestMatch(){
        for (int i = 0; i < originalList.size(); i++){
            list.remove(i);
            list.add(i, originalList.get(i));
        }
    }
}

