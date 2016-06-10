package top.zachapps.moviesuggester.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;
import top.zachapps.moviesuggester.logic.Movie;

/**
 * Created by zach on 09-Feb-16.
 */
public class ViewMovieInfo extends LinearLayout {


    class Layout{
        Layout(){
            director = (TextView) findViewById(R.id.director);
            writers = (TextView) findViewById(R.id.writers);
            actors = (TextView) findViewById(R.id.actors);
            plot = (TextView) findViewById(R.id.plot);
            titles = new TextView[]{
                    (TextView) findViewById(R.id.titleDirector),
                    (TextView) findViewById(R.id.titleWriters),
                    (TextView) findViewById(R.id.titleActors),
                    (TextView) findViewById(R.id.titlePlot)
            };
        }
        TextView director, writers, actors, plot;
        TextView[] titles;
    }

    private Context ctx;
    Layout l;

    public ViewMovieInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_movie_info, this);
        ctx = context; l = new Layout();

        for(TextView item: l.titles){
            Font.setFaceBold(ctx, item);
        }
    }

    public void updateInfo(Movie movie){
        if(movie.getType().equals("series")){
            l.director.setVisibility(GONE);
            l.titles[0].setVisibility(GONE);
        }

        l.director.setText(movie.getDirector());
        l.writers.setText(movie.getWriter());
        l.actors.setText(movie.getActors());
        l.plot.setText(movie.getPlot());

        Font.setFace(ctx, l.director);
        Font.setFace(ctx, l.writers);
        Font.setFace(ctx, l.actors);
        Font.setFace(ctx, l.plot);
    }
}

