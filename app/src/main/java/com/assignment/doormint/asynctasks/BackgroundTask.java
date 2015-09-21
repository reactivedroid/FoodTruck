package com.assignment.doormint.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.assignment.doormint.common.Constants;
import com.assignment.doormint.foodtruck.R;
import com.assignment.doormint.listeners.DataTaskListener;
import com.assignment.doormint.listeners.IApiMethods;
import com.assignment.doormint.modal.Location;
import com.assignment.doormint.modal.PlaceInfoHolder;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by ashwiask on 9/20/2015.
 */
public class BackgroundTask extends AsyncTask<Void, Void, List<PlaceInfoHolder>> {

    RestAdapter restAdapter = null;

    DataTaskListener mTaskListner = null;
    ProgressDialog mDialog = null;
    private Context mContext = null;

    public BackgroundTask(Context context, DataTaskListener listener) {
        mContext = context;
        mDialog = new ProgressDialog(mContext);
        mTaskListner = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mDialog.setMessage(mContext.getString(R.string.progress_dialog_message));
        mDialog.setIndeterminate(true);
        mDialog.show();

        restAdapter = new RestAdapter.Builder().setEndpoint("https://data.sfgov.org").setClient(new OkClient(new OkHttpClient())).build();
    }

    @Override
    protected List<PlaceInfoHolder> doInBackground(Void... params) {

        IApiMethods apiMethods = restAdapter.create(IApiMethods.class);
        List<PlaceInfoHolder> infoHolders = apiMethods.getPlaceHolders();

        return infoHolders;
    }

    @Override
    protected void onPostExecute(List<PlaceInfoHolder> infoList) {
        super.onPostExecute(infoList);

        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
        ArrayList<String> locationList = new ArrayList<>();
        HashMap<String, String> locationMap = new HashMap<>();
        HashMap<String, String> foodMap = new HashMap<>();

        for (PlaceInfoHolder info :
                infoList) {
            String locationDescription = info.getAddress();
            Location location = info.getLocation();
            if (location != null && info.getAddress() != null) {

                String locationString = location.getLatitude() + Constants.ST_COMMA + location.getLongitude();
                locationMap.put(locationDescription, locationString);
                foodMap.put(locationDescription, info.getFoodItems());
                locationList.add(locationDescription);
            }

        }

        mTaskListner.onDataTaskCompleted(locationList, locationMap, foodMap);

    }
}
