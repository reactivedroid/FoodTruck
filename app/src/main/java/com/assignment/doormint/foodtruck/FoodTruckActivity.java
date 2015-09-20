package com.assignment.doormint.foodtruck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.doormint.asynctasks.RequestDataTask;
import com.assignment.doormint.common.Constants;
import com.assignment.doormint.listeners.DataTaskListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by ashwiask on 7/31/2015.
 * <p/>
 * This activity deals with data retrieval from he given URL and displaying the loczaions on the list
 */

public class FoodTruckActivity extends Activity implements DataTaskListener {
    /**
     * UI
     */
    @InjectView(R.id.btn_fetch_data)
    Button btnFetchData;
    @InjectView(R.id.tvLocations)
    TextView tvLocHeader;
    @InjectView(R.id.listview_locations)
    ListView lvLocations;
    @InjectView(R.id.etLocationSearch)
    EditText etLocSearch;


    /**
     * Tag for logging
     */
    private static final String TAG = FoodTruckActivity.class.getSimpleName();

    /**
     * Location  HashMap [key - Location Description, value - Location]
     */
    private HashMap<String, String> mLocationMap = null;
    /**
     * List of locations
     */
    private ArrayList<String> mLocationList = null;
    /**
     * Food HashMap [key = Location Description, value - FoodItems]
     */
    private HashMap<String, String> mFoodMap = null;

    /**
     * Array adpator to populate the list view
     */
    private ArrayAdapter<String> mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_truck);

        ButterKnife.inject(this);

        etLocSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.btn_fetch_data)
    public void fetchData() {

        if (!isInternetAvailable()) {

            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        } else {
            resetData();
            new RequestDataTask(FoodTruckActivity.this, FoodTruckActivity.this).execute(Constants.JSON_URL);

        }
    }

    @OnItemClick(R.id.listview_locations)
    public void showOnMap(int position) {
        String locationDescription = (String) lvLocations.getItemAtPosition(position);
        String location = mLocationMap.get(locationDescription);
        if (location != null && !location.equalsIgnoreCase("")) {
            if (!isInternetAvailable()) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "Location clicked: " + location);
                // Passing data to Map acitvity to populate it on map
                String food = mFoodMap.get(locationDescription);
                Intent intent = new Intent(FoodTruckActivity.this, MapActivity.class);
                intent.putExtra(Constants.LOCATION, location);
                intent.putExtra(Constants.FOOD, food);
                intent.putExtra(Constants.LOCATION_DESCRIPTION, locationDescription);
                Log.i(TAG, "Location Description: " + locationDescription);
                Log.i(TAG, "Food Available at that Location: " + food);
                startActivity(intent);
            }
        }
    }


    /**
     * Check for the internet connection availability
     *
     * @return - true if internet is available
     * else flase
     */
    private boolean isInternetAvailable() {
        boolean isInternetPresent = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        isInternetPresent = true;
                    }
                }
            }
        }
        return isInternetPresent;
    }

    /**
     * Reset the list and map for fresh data retrieval
     */
    private void resetData() {
        if (mLocationList != null) {
            mLocationList.clear();
            mLocationList = null;
        }
        if (mLocationMap != null) {
            mLocationMap.clear();
            mLocationMap = null;
        }
        if (mFoodMap != null) {
            mFoodMap.clear();
            mFoodMap = null;
        }
    }

    private void setListView() {
        if (mLocationList != null && mLocationList.size() > 0) {
            tvLocHeader.setVisibility(View.VISIBLE);
            lvLocations.setVisibility(View.VISIBLE);
            etLocSearch.setVisibility(View.VISIBLE);
            mAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, mLocationList);

            lvLocations.setAdapter(mAdapter);
        }

    }

    @Override
    public void onDataTaskCompleted(ArrayList<String> locationList, HashMap<String, String> locMap, HashMap<String, String> foodMap) {
        mLocationList = locationList;
        mLocationMap = locMap;
        mFoodMap = foodMap;

        setListView();

    }
}
