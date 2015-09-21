package com.assignment.doormint.listeners;

import com.assignment.doormint.modal.PlaceInfoHolder;

import java.util.List;

import retrofit.http.GET;

/**
 * Created by ashwiask on 9/20/2015.
 */
public interface IApiMethods {

    @GET("/resource/rqzj-sfat.json")
    List<PlaceInfoHolder> getPlaceHolders();
}
