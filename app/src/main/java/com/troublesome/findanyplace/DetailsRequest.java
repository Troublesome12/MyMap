package com.troublesome.findanyplace;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by troublesome on 8/17/15.
 */

public class DetailsRequest {

    //Tag used to cancel the request
    public static String tag_json_obj = "json_obj_req";
    static final String detailsURL = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAwJMcBAzDt2ij8YKUKLgtZJKIZVOJpbAA";
    static final String streetViewURL = "https://maps.googleapis.com/maps/api/streetview?size=600x400";
    static final String imageURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&key=AIzaSyAwJMcBAzDt2ij8YKUKLgtZJKIZVOJpbAA";
    static String name, phoneNumber, webSite;
    static double rating;
    static int user_rating;
    static String photoReference;
    static boolean hasPhotoReference;

    public static void detailsRequest(final String placeId, final Context context) {
        showDialog();

        String tempURL = detailsURL + "&placeid=" + placeId;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                tempURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            JSONObject geometry = result.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            Double lat = location.getDouble("lat");
                            Double lng = location.getDouble("lng");
                            String address = result.getString("formatted_address");
                            String name = result.getString("name");
                            if (result.has("photos")) {
                                hasPhotoReference=true;
                                JSONArray photos = result.getJSONArray("photos");
                                //only taking the first photo
                                JSONObject photo = photos.getJSONObject(0);
                                photoReference = photo.getString("photo_reference");
                            } else
                                hasPhotoReference = false;    //no photoReference available

                            if (result.has("formatted_phone_number")) {
                                phoneNumber = result.getString("formatted_phone_number");
                            } else {
                                phoneNumber = "";
                                DetailsActivity.callTextView.setVisibility(View.GONE);
                            }

                            if (result.has("rating")) {
                                rating = result.getDouble("rating");
                            } else
                                rating = 0.0;

                            if (result.has("user_ratings_total")) {
                                user_rating = result.getInt("user_ratings_total");
                            } else {
                                user_rating = 0;
                            }

                            if (result.has("website")) {
                                webSite = result.getString("website");
                            } else {
                                webSite = "";
                                DetailsActivity.webTextView.setVisibility(View.GONE);
                            }

                            Location myLocation = new Location("");
                            myLocation.setLatitude(lat);
                            myLocation.setLongitude(lng);
                            DetailsActivity.selectedPlace = new Place(myLocation, name, placeId, rating, address);
                            DetailsActivity.selectedPlace.setPhoneNumber(phoneNumber);
                            DetailsActivity.selectedPlace.setWebSite(webSite);

                            populatingView(context);

                            DrivingDetailsRequest.getDrivingDetailsRequest(myLocation);
                            WalkingDetailsRequest.getWalkingDetailsRequest(myLocation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private static void populatingView(Context context) {

        DetailsActivity.nameTextView.setText(DetailsActivity.selectedPlace.getName());
        double temp = DetailsActivity.selectedPlace.getRating();
        DetailsActivity.ratingBar.setRating((float) temp);
        DetailsActivity.ratingTextView.setText(rating + "");
        DetailsActivity.reviewTextView.setText(user_rating + " reviews");
        DetailsActivity.vicinityTextView.setText(DetailsActivity.selectedPlace.getVicinity());


        if (hasPhotoReference) {
            String tempImageURL = imageURL + "&photoreference=" + photoReference;
            Picasso.with(context).load(tempImageURL).error(R.drawable.error).into(DetailsActivity.imageView);
        } else {
            double lat = DetailsActivity.selectedPlace.getLocation().getLatitude();
            double lng = DetailsActivity.selectedPlace.getLocation().getLongitude();
            String tempImageURL = streetViewURL + "&location=" + lat + "," + lng;
            Picasso.with(context).load(tempImageURL).error(R.drawable.error).into(DetailsActivity.imageView);

        }
    }

    private static void showDialog() {
        if (!DetailsActivity.progressDialog.isShowing())
            DetailsActivity.progressDialog.show();
    }

    private static void hideDialog() {
        if (DetailsActivity.progressDialog.isShowing())
            DetailsActivity.progressDialog.dismiss();
    }
}
