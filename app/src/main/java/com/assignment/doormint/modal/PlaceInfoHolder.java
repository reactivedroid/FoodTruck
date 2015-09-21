package com.assignment.doormint.modal;

/**
 * Created by ashwiask on 9/20/2015.
 */
public class PlaceInfoHolder {


    // The names must be same as given in JSON file to fetch data
    String fooditems;

    String locationdescription;

    String address;

    Location location;

    public Location getLocation() {
        return location;
    }

    public String getFoodItems() {
        return fooditems;
    }

    public String getLocationDescription() {
        return locationdescription;
    }

    public String getAddress() {
        return address;
    }


}
