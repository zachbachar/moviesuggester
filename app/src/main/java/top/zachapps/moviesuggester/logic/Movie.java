package top.zachapps.moviesuggester.logic;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import top.zachapps.moviesuggester.ActivitySearch;
import top.zachapps.moviesuggester.Networking.VolleyConnectivity;

/**
 * Created by zach on 01-Feb-16.
 */
public class Movie {

    public interface ResponseListener{
        void responseTrue();
        void responseFalse();
    }

    public Movie(Context ctx, String title, String yID){
        this.title = title;
        this.ctx = ctx;
        setYoutube(yID);
    }

    public Movie(JSONObject obj, String yID) throws JSONException {
        this.title = obj.getString("Title");
        updateFields(obj);
        setYoutube(yID);
    }

    private Context ctx;
    private String title;
    private String year;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String awards;
    private String poster;
    private String type;
    private String imdbRating;
    private String imdbVotes;
    private String youtube;
    private JSONObject jsonObject;

    public void init(final ResponseListener handler){
        String url = "http://www.omdbapi.com/?t=" + this.title.replaceAll(" ", "%20") + "&type=" + ActivitySearch.selectedType + "&plot=full&r=json";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("Response").equals("True")) {
                        updateFields(response);
                        handler.responseTrue();
                    }else handler.responseFalse();
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.responseFalse();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                handler.responseFalse();
            }
        });
        VolleyConnectivity.add(ctx, jsObjRequest);
    }

    private void updateFields(JSONObject obj) throws JSONException{
        setTitle(obj.getString("Title"));
        setYear(obj.getString("Year"));
        setReleased(obj.getString("Released"));
        setRuntime(obj.getString("Runtime"));
        setGenre(obj.getString("Genre"));
        setDirector(obj.getString("Director"));
        setWriter(obj.getString("Writer"));
        setActors(obj.getString("Actors"));
        setPlot(obj.getString("Plot"));
        setAwards(obj.getString("Awards"));
        setPoster(obj.getString("Poster"));
        setType(obj.getString("Type"));
        setImdbRating(obj.getString("imdbRating"));
        setImdbVotes(obj.getString("imdbVotes"));
        setJsonObject(obj);
    }

    public String getYoutube() {
        return youtube;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if(checkNA(year)) {
            this.year = year;
        }
        else this.year = "0";
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        if(checkNA(released)) {
            this.released = released;
        }
        else this.released = "";
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        if(checkNA(runtime)) {
            this.runtime = runtime;
        }
        else this.runtime = "";
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if(checkNA(genre)) {
            this.genre = genre;
        }
        else this.genre = "";
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        if(checkNA(director)) {
            this.director = director;
        }
        else this.director = "";
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        if(checkNA(writer)) {
            this.writer = writer;
        }
        else this.writer = "";
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        if(checkNA(actors)) {
            this.actors = actors;
        }
        else this.actors = "";
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        if(checkNA(plot)) {
            this.plot = plot;
        }
        else this.plot = "";
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        if(checkNA(awards)) {
            this.awards = awards;
        }
        else this.awards = "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoster(){
        return poster;
    }

    public void setPoster(String poster){
        this.poster = poster;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        if(checkNA(imdbRating)) {
            this.imdbRating = imdbRating;
        }
        else this.imdbRating = "0";
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        if(checkNA(imdbVotes)) {
            this.imdbVotes = imdbVotes;
        }
        else this.imdbVotes = "0";
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private boolean checkNA(String str){
        if (str.equals("N/A")) return false;
        return true;
    }
}

