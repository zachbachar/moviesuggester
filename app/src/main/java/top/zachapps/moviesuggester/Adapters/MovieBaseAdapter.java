package top.zachapps.moviesuggester.Adapters;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;
import top.zachapps.moviesuggester.logic.Movie;

/**
 * Created by zach on 05-Feb-16.
 */public class MovieBaseAdapter extends BaseAdapter{

    private ArrayList<Movie> data;
    private Context context;
    private  LayoutInflater mInflater;
    private int mLastPosition;
    public boolean animations;

    public MovieBaseAdapter(ArrayList<Movie> data, Context context) {
        this.data = data;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        animations = false;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.movie_item_list, parent, false);
            holder = new MovieHolder();
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtTitle = (TextView )convertView.findViewById(R.id.firstLine);
            holder.txtYear = (TextView) convertView.findViewById(R.id.year);
            holder.txtRating = (TextView) convertView.findViewById(R.id.rating);
            holder.txtGeners = (TextView) convertView.findViewById(R.id.geners);
            holder.progBar = (ProgressBar) convertView.findViewById(R.id.progBar);

            convertView.setTag(holder);
        } else {
            holder = (MovieHolder) convertView.getTag();
        }

        Movie movie = data.get(position);
        holder.txtTitle.setText(movie.getTitle());
        holder.txtYear.setText(movie.getYear());
        holder.txtRating.setText(movie.getImdbRating());
        holder.txtGeners.setText(movie.getGenre());

        Font.setFace(context, holder.txtTitle);
        Font.setFace(context, holder.txtYear);
        Font.setFace(context, holder.txtRating);
        Font.setFace(context, holder.txtGeners);

        String url = data.get(position).getPoster();
        final MovieHolder finalHolder = holder;
        Picasso.with(context).load(url).error(R.mipmap.error).into(holder.imgIcon, new Callback() {
            @Override
            public void onSuccess() {
                finalHolder.progBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                finalHolder.progBar.setVisibility(View.GONE);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertView.setTransitionName("profile");
        }

        /**************************row animation*****************************/
        if(animations) {
            float initialTranslation = (mLastPosition <= position ? 500f : -500f);

            convertView.setTranslationY(initialTranslation);
            convertView.animate()
                    .setInterpolator(new DecelerateInterpolator(1.0f))
                    .translationY(0f)
                    .setDuration(300l)
                    .setListener(null);

            // Keep track of the last position we loaded
            mLastPosition = position;


            /*Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_fade_in);
            convertView.startAnimation(anim);*/
        }

        return convertView;
    }

    static class MovieHolder
    {
        ImageView imgIcon;
        TextView txtTitle, txtYear, txtRating, txtGeners;
        ProgressBar progBar;
    }
}


