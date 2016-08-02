package com.troublesome.findanyplace;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by troublesome on 8/15/15.
 */

public class AutoCompleteRequest {

    //Tag used to cancel the request
    public static String tag_json_obj = "json_obj_req";
    public static final String dropDownURL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAwJMcBAzDt2ij8YKUKLgtZJKIZVOJpbAA";
    static String name;

    public static void autoCompleteRequest(String input, final Context context) {
        String tempURL = dropDownURL;
        tempURL = tempURL + "&input=" + input;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                tempURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MapsActivity.searchList.clear();
                            JSONArray prediction = response.getJSONArray("predictions");
                            for (int i = 0; i < prediction.length(); i++) {
                                JSONObject place = prediction.getJSONObject(i);
                                name = place.getString("description");
                                MapsActivity.searchList.add(new String(name));
                                //MapsActivity.searchListPlaceId.add(new String(place.getString("place_id")));
                            }
                            //Getting the place id for the first prediction only
                            JSONObject place = prediction.getJSONObject(0);
                            MapsActivity.searchPlaceId=place.getString("place_id");
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