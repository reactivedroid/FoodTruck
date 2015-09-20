package com.assignment.doormint.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.assignment.doormint.common.Constants;
import com.assignment.doormint.foodtruck.R;
import com.assignment.doormint.listeners.DataTaskListener;

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

/**
 * Created by ashwiask on 9/20/2015.
 * <p/>
 * Async Task that handles the data retrieval from the URL. It fetches and parses the JSON data and populate them in the list and map.
 */
public class RequestDataTask extends AsyncTask<String, Void, String> {

    private static final String TAG = RequestDataTask.class.getSimpleName();

    ProgressDialog mDialog = null;
    private Context mContext = null;

    private DataTaskListener mDataTaskListener = null;

    public RequestDataTask(Context context, DataTaskListener listener) {
        mContext = context;
        mDialog = new ProgressDialog(mContext);
        mDataTaskListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mContext.getString(R.string.progress_dialog_message));
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

            ArrayList<String> locationList = new ArrayList<>();
            HashMap<String, String> locationMap = new HashMap<>();
            HashMap<String, String> foodMap = new HashMap<>();

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jObj = jsonArr.optJSONObject(i);
                String location = jObj.optString(Constants.LATITUDE_TAG) + Constants.ST_COMMA + jObj.optString(Constants.LONGITUDE_TAG);
                String locationDescription = jObj.optString(Constants.LOCATION_DESRIPTION_TAG);
                String foodItems = jObj.optString(Constants.FOOD_ITEMS_TAG);
                if (!locationDescription.equals("")) {
                    locationMap.put(locationDescription, location);
                    foodMap.put(locationDescription, foodItems);
                    locationList.add(locationDescription);
                }
            }

            mDataTaskListener.onDataTaskCompleted(locationList, locationMap, foodMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
