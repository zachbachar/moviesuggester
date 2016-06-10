package top.zachapps.moviesuggester.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;
import top.zachapps.moviesuggester.logic.Movie;

/**
 * Created by zach on 31-Jan-16.
 */
public class ViewMovieHeader extends LinearLayout {

    class Layout{
        Layout(){
            txtMovieTitle = (TextView) findViewById(R.id.txtMovieTitle);
            txtMovieYear = (TextView) findViewById(R.id.txtMovieYear);
            txtMovieGeners = (TextView) findViewById(R.id.txtMovieGeners);
            txtMovieLength = (TextView) findViewById(R.id.txtMovieLength);
            txtMovieRating = (TextView) findViewById(R.id.txtMovieRating);
            txtMovieReleased = (TextView) findViewById(R.id.txtMovieReleased);
            txtMovieVotes = (TextView) findViewById(R.id.txtMovieVotes);
            titleContainer = (LinearLayout) findViewById(R.id.titleContainer);
        }
        TextView txtMovieTitle, txtMovieYear, txtMovieGeners, txtMovieVotes,
                txtMovieLength, txtMovieRating, txtMovieReleased;
        LinearLayout titleContainer;

    }

    class Events{
        Events(){

        }
    }

    Events e;
    Layout l;
    Context ctx;

    public ViewMovieHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_movie_header, this);
        ctx = context;

        l = new Layout(); e = new Events();
    }

    public void updateHeader(Movie movie) {
        l.txtMovieTitle.setText(checkNA(movie.getTitle()));
        l.txtMovieYear.setText("(" + checkNA(movie.getYear()) + ")");
        l.txtMovieGeners.setText(checkNA(movie.getGenre()));
        l.txtMovieLength.setText(checkNA(movie.getRuntime()));
        l.txtMovieRating.setText(checkNA(movie.getImdbRating()));
        l.txtMovieReleased.setText(checkNA(movie.getReleased()));
        l.txtMovieVotes.setText(checkNA(movie.getImdbVotes()));

        Font.setFace(ctx, l.txtMovieTitle);
        Font.setFace(ctx, l.txtMovieYear);
        Font.setFace(ctx, l.txtMovieGeners);
        Font.setFace(ctx, l.txtMovieLength);
        Font.setFace(ctx, l.txtMovieRating);
        Font.setFace(ctx, l.txtMovieReleased);
        Font.setFace(ctx, l.txtMovieVotes);
    }

    String checkNA(String s){
        if (s.equals("N/A")){
            s = "0";
        }else if(s.equals(null)){
            s = "0";
        }
        return s;
    }
}


