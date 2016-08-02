package com.troublesome.findanyplace;

import android.location.Location;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by troublesome on 8/15/15.
 */

public class SearchRequest {

    //Tag used to cancel the request
    public static String tag_json_obj = "json_obj_req";
    static final String searchURL = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAwJMcBAzDt2ij8YKUKLgtZJKIZVOJpbAA";
    public static void searchRequest(final String placeId) {
        String tempURL = searchURL + "&placeid=" + placeId;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                tempURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            JSONObject geometry = result.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            Location loc = new Location("");
                            loc.setLatitude(location.getDouble("lat"));
                            loc.setLongitude(location.getDouble("lng"));
                            String name = result.getString("name");
                            MapsActivity.setMarkerOnLocation(loc, name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}