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
 * Created by troublesome on 8/16/15.
 */
public class NearByPlaceRequest {

    //Tag used to cancel the request
    public static String tag_json_obj = "json_obj_req";
    private static final String placeURL = "https://maps.googleapis.com/maps/api/place/search/json?key=AIzaSyAwJMcBAzDt2ij8YKUKLgtZJKIZVOJpbAA";
    private static String location = "&location=";
    private static String types = "&types=";
    private static String radius = "&radius=";
    static Double rating;


    public static void nearByPlaceRequest(String type, final boolean refreshing) {
        ListViewActivity.placeArrayList.clear();    //removing previous data if available

        String tempURL = placeURL + location + MapsActivity.mLatitude + "," + MapsActivity.mLongitude;
        tempURL = tempURL + types + type;
        tempURL = tempURL + radius +MapsActivity.radiusValue;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                tempURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);
                                JSONObject geometry = place.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                Double lat = location.getDouble("lat");
                                Double lng = location.getDouble("lng");
                                String name = place.getString("name");

                                String placeId = place.getString("place_id");
                                if (place.has("rating")) {
                                    rating = place.getDouble("rating");
                                }
                                else
                                    rating=3.0;
                                String vicinity = place.getString("vicinity");

                                Location myLocation = new Location("");
                                myLocation.setLatitude(lat);
                                myLocation.setLongitude(lng);
                                ListViewActivity.placeArrayList.add(new Place(myLocation, name, placeId, rating, vicinity));
                            }
                            if(refreshing){
                               ListViewActivity.adapter.notifyDataSetChanged();
                            }
                            else
                                ListViewActivity.listView.setAdapter(ListViewActivity.adapter);

                            // stopping swipe refresh
                            ListViewActivity.swipeRefreshLayout.setRefreshing(false);

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
}
