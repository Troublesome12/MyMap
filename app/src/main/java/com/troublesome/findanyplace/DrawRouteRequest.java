package com.troublesome.findanyplace;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troublesome on 8/18/15.
 */

public class DrawRouteRequest {

    //Tag used to cancel the request
    public static String tag_json_obj = "json_obj_req";
    static final String directionURL = "https://maps.googleapis.com/maps/api/directions/json?alternatives=true";
    static final String origin = "&origin=";
    static final String destination = "&destination=";
    static String distance, duration;
    static ArrayList<LatLng> path = new ArrayList<>();
    static int minDistance = 0;
    static int minRouteIndex=0;

    public static void getRouteRequest(Location location) {
        showDialog();

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
                                /** Traversing all legs */
                                for (int j = 0; j < legs.length(); j++) {
                                    JSONObject leg = legs.getJSONObject(j);

                                    JSONObject distanceObj = leg.getJSONObject("distance");
                                    int tempDistance = distanceObj.getInt("value");
                                    Log.w("tempDistance = ", tempDistance + "");
                                    if (tempDistance < minDistance) {
                                        minDistance = tempDistance;
                                        minRouteIndex = i;
                                    }
                                }
                            }
                            /** Traversing min routes */
                            JSONObject route = routes.getJSONObject(minRouteIndex);
                            JSONArray legs = route.getJSONArray("legs");
                            /** Traversing all legs */
                            for (int j = 0; j < legs.length(); j++) {
                                JSONObject leg = legs.getJSONObject(j);

                                JSONObject distanceObj = leg.getJSONObject("distance");
                                distance = distanceObj.getString("text");

                                JSONObject durationObj = leg.getJSONObject("duration");
                                duration = durationObj.getString("text");

                                JSONArray steps = leg.getJSONArray("steps");
                                /** Traversing all steps */
                                for (int k = 0; k < steps.length(); k++) {
                                    JSONObject step = steps.getJSONObject(k);

                                    JSONObject polyline = step.getJSONObject("polyline");
                                    String points = polyline.getString("points");

                                    List<LatLng> list = decodePoly(points);

                                    /** Traversing all points */
                                    for (LatLng a : list) {
                                        path.add(new LatLng(a.latitude, a.longitude));
                                    }
                                }
                            }
                            DrawPath();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void DrawPath() {
        if (MapsActivity.line != null) {
            MapsActivity.line.remove();
        }

        PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);

        for (LatLng x : path) {
            options.add(x);
        }

        MapsActivity.line = MapsActivity.mMap.addPolyline(options);
        path.clear();
    }


    /**
     * Method Courtesy :
     * jeffreysambells.com/2010/05/27
     * /decoding-polylines-from-google-maps-direction-api-with-java
     */

    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private static void showDialog() {
        if (!MapsActivity.mapsActivityDialog.isShowing())
            MapsActivity.mapsActivityDialog.show();
    }

    private static void hideDialog() {
        if (MapsActivity.mapsActivityDialog.isShowing())
            MapsActivity.mapsActivityDialog.dismiss();
    }
}