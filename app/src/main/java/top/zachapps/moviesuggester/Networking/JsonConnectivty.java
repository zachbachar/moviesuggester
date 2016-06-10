package top.zachapps.moviesuggester.Networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zach on 09-Feb-16.
 */
public class JsonConnectivty {

    public interface responseListener{
        void onResponse(JSONObject response) throws JSONException;
    }

    public void getJson(Context context, String url, final responseListener handler){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    handler.onResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyConnectivity.add(context, jsObjRequest);
    }
}
