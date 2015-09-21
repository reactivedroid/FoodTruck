package com.assignment.doormint.foodtruck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.assignment.doormint.common.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by ashwiask on 7/31/2015.
 * <p/>
 * This activity displays the Google map on the screen with the marker dropped on the specified location.
 * Marker displays th Location description and Food Items available at that location
 */
public class MapActivity extends Activity {
    /**
     * Google map
     */
    private GoogleMap map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialise google map
        initMap();

        map.getUiSettings().setMyLocationButtonEnabled(true);

        Intent intent = getIntent();
        String location = intent.getStringExtra(Constants.LOCATION);
        String food = intent.getStringExtra(Constants.FOOD);
        String locationDescription = intent.getStringExtra(Constants.LOCATION_DESCRIPTION);
        markFoodAvailableAtLocation(location, food, locationDescription);


    }

    /**
     * Marks the location on map and displays it as a marker with Location Description and Food Items
     *
     * @param location       - Location to be mked on map
     * @param food           - Food items corresponding to the given location
     * @param locDescription -Location dscription of the location
     */
    private void markFoodAvailableAtLocation(final String location, String food, String locDescription) {
        String latLng[] = location.split(Constants.ST_COMMA);

        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(12).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions options = new MarkerOptions().position(new LatLng(latitude, longitude)).title(locDescription).snippet(food);
        final Marker foodMarker = map.addMarker(options);
        foodMarker.showInfoWindow();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.location) + Constants.ST_COLON + location, Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        initMap();
    }

    /**
     * Initialising the map
     */
    private void initMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.map_not_created), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
