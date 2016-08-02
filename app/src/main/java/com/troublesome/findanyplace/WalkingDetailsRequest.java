package com.troublesome.findanyplace;

import android.location.Location;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by troublesome on 8/22/15.
 */

public class WalkingDetailsRequest {

    //Tag used to cancel the request
    public static String tag_json_obj = "json_obj_req";
    static final String directionURL = "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyAwJMcBAzDt2ij8YKUKLgtZJKIZVOJpbAA&mode=walking";
    static final String origin = "&origin=";
    static final String destination = "&destination=";
    static String distance, duration;

    public static void getWalkingDetailsRequest(Location location) {
        String tempOrigin = origin + MapsActivity.mLatitude + "," + MapsActivity.mLongitude;
        String tempDestination = destination + location.getLatitude() + "," + location.getLongitude();
        String tempURL = directionURL + tempOrigin + tempDestination;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                tempURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray routes = response.getJSONArray("routes");
                            /** Traversing all routes */
                            for (int i = 0; i < routes.length(); i++) {
                                JSONObject route = routes.getJSONObject(i);
                                JSONArray legs = route.getJSONArray("legs");
                                /** Traversing first leg only */
                                JSONObject leg = legs.getJSONObject(0);

                                JSONObject distanceObj = leg.getJSONObject("distance");
                                distance = distanceObj.getString("text");

                                JSONObject durationObj = leg.getJSONObject("duration");
                                duration = durationObj.getString("text");
                            }

                            populatingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private static void populatingView() {
        DetailsActivity.walkingDurationTextView.setText(duration);
        DetailsActivity.walkingDistanceTextView.setText(distance);
    }
}
