package top.zachapps.moviesuggester.UIAssets;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by zach on 04-Mar-16.
 */
public abstract class Font {

    public static void setFace(Context ctx, TextView view){
        view.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Light.ttf"));
    }

    public static void setFaceBold(Context ctx, TextView view){
        view.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Black.ttf"));
    }
}
