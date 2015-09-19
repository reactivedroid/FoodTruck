package com.assignment.doormint.foodtruck;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import com.assignment.doormint.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ashwiask on 7/31/2015.
 * <p/>
 * This activity deals with data retrieval from he given URL and displaying the loczaions on the list
 */

public class FoodTruckActivity extends Activity {
    /**
     * UI
     */
    private Button btnFetchData = null;
    private TextView tvLocHeader = null;
    private ListView lvLocations = null;
    private EditText etLocSearch = null;

    /**
     * Async task for data retrieval from URL
     */
    private RequestDataTask mRequestDataTask = null;

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
        btnFetchData = (Button) findViewById(R.id.btn_fetch_data);
        tvLocHeader = (TextView) findViewById(R.id.tvLocations);
        lvLocations = (ListView) findViewById(R.id.listview_locations);
        etLocSearch = (EditText) findViewById(R.id.etLocationSearch);

        btnFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {

                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                } else {
                    resetData();
                    mRequestDataTask = new RequestDataTask(FoodTruckActivity.this);
                    mRequestDataTask.execute(Constants.JSON_URL);
                }
            }
        });


        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        });

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

    /**
     * Async Task that handles the data retrieval from the URL. It fetches and parses the JSON data and populate them in the list and map.
     */
    private class RequestDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog mDialog = null;
        private Context mContext = null;

        public RequestDataTask(Context context) {
            mContext = context;
            mDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage(getString(R.string.progress_dialog_message));
            mDialog.setIndeterminate(true);
            mDialog.show();

        }

        @Override
        protected String doInBackground(String... uri) {
            StringBuilder builder = new StringBuilder("");
            java.net.URL url;
            BufferedReader reader = null;

            try {
                url = new URL(uri[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                String response;

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                while ((response = reader.readLine()) != null) {
                    builder.append(response + "\n");
                }

            } catch (MalformedURLException ex) {
                Log.d(TAG, "URL is not proper " + ex.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "URL not found " + e.getMessage());
            } finally {
                try {

                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }

            try {
                JSONArray jsonArr = new JSONArray(result);

                mLocationList = new ArrayList<>();
                mLocationMap = new HashMap<>();
                mFoodMap = new HashMap<>();

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jObj = jsonArr.optJSONObject(i);
                    String location = jObj.optString(Constants.LATITUDE_TAG) + Constants.ST_COMMA + jObj.optString(Constants.LONGITUDE_TAG);
                    String locationDescription = jObj.optString(Constants.LOCATION_DESRIPTION_TAG);
                    String foodItems = jObj.optString(Constants.FOOD_ITEMS_TAG);
                    mLocationMap.put(locationDescription, location);
                    mFoodMap.put(locationDescription, foodItems);
                    mLocationList.add(locationDescription);
                }

                if (mLocationList != null && mLocationList.size() > 0) {
                    tvLocHeader.setVisibility(View.VISIBLE);
                    lvLocations.setVisibility(View.VISIBLE);
                    etLocSearch.setVisibility(View.VISIBLE);
                    mAdapter = new ArrayAdapter<>(mContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, mLocationList);

                    lvLocations.setAdapter(mAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
}
