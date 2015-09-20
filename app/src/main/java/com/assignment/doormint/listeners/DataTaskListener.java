package com.assignment.doormint.listeners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ashwiask on 9/20/2015.
 */
public interface DataTaskListener {
    void onDataTaskCompleted(ArrayList<String> locationList, HashMap<String, String> locMap, HashMap<String, String> foodMap);
}
