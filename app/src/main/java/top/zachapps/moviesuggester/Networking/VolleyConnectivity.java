package top.zachapps.moviesuggester.Networking;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by zach on 18-Jan-16.
 */
public class VolleyConnectivity {

    private static RequestQueue q;

    public static void add(Context ctx, Request<?> request){
        if(q == null){
            Cache cache = new DiskBasedCache(ctx.getCacheDir(), 1024 * 1024);

            Network network = new BasicNetwork(new HurlStack());

            q = new RequestQueue(cache, network);

            q.start();
        }
        q.add(request);
    }
}
