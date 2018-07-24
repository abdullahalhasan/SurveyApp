package com.example.shohel.geopoll.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shohel.geopoll.NetworkChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by BDDL-102 on 5/20/2018.
 */

public class TrafficMonitor {

    long latest_stx;
    long latest_srx;
    long latest_smtx;
    long latest_smrx;
    long latest_swtx;
    long latest_swrx;

    String URL_USAGE = "https://www.mindscape.com.bd/api/v1/data/usage";
    JSONObject historyObject;
    JSONArray historyArray;

    RequestQueue queue;
    JsonObjectRequest objRequest;


    //Created Class TrafficSnapshot
    TrafficSnapshot latest;
    TrafficSnapshot previous;
    Context context;

    String netConnectivity;
    String netType;
    String dataVolume;

    public TrafficMonitor(Context context) {
        this.context = context;
        previous = latest;
        latest = new TrafficSnapshot(context);
        queue =  Volley.newRequestQueue(context);
        historyObject = new JSONObject();
        historyArray = new JSONArray();
    }

    public void dataSnapshot() {

        //rx= received data, tx = sent data
        //m = mobile w=wifi

        latest_srx = latest.device.rx;
        latest_stx = latest.device.tx;
        latest_smtx = latest.device.mtx;
        latest_smrx = latest.device.mrx;
        latest_swtx = latest.device.wtx;
        latest_swrx = latest.device.wrx;


        //Save in Shared Preferences

        if (NetworkChecker.isInternetAvailable(context)) {


            if (NetworkChecker.isMobileNetAvailable(context)) {

                //Mobile Data Usages
                Log.e("Received Mobile Data", latest_smrx + "kb");
                Log.e("Sent Mobile Data", latest_smtx + "kb");
                netConnectivity = "Mobile";
                netType = "Mobile Data";
                dataVolume = String.valueOf(latest_smrx+latest_smtx);
                dataUsageUpload(netConnectivity,netType,dataVolume);

                //Wi-Fi Data Usages
                Log.e("Received WiFi Data", latest_swrx + "kb");
                Log.e("Sent WiFi Data", latest_swtx + "kb");
                netConnectivity = "WiFi";
                netType = "WiFi Data";
                dataVolume = String.valueOf(latest_swrx+latest_swtx);
                dataUsageUpload(netConnectivity,netType,dataVolume);
            } else {

                //Total Data Usages
                Log.e("Received Total Data", latest_srx + "kb");
                Log.e("Sent Total Data", latest_stx + "kb");
                netConnectivity = "Total";
                netType = "Mobile + WiFi";
                dataVolume = String.valueOf(latest_srx+latest_stx);
                dataUsageUpload(netConnectivity,netType,dataVolume);
            }



        }
    }

    private void dataUsageUpload(String connectivity,String type,String volume) {
        JSONObject postObject = new JSONObject();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userID = sharedPreferences.getString("user_id","");
        String userMobile = sharedPreferences.getString("user_mobile","");
        try {
            historyObject.put("user_id",userID);
            historyObject.put("user_mobile", userMobile);
            historyObject.put("connectivity",connectivity);
            historyObject.put("type",type);
            historyObject.put("volume",volume);
            postObject.put("userinfo",historyObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        historyArray.put(postObject);

        objRequest = new JsonObjectRequest(Request.Method.POST, URL_USAGE,postObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("OnResponse",String.valueOf(response));
                        //Toast.makeText(context, String.valueOf(response), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("On Error", String.valueOf(error.getMessage()));
            }
        });

        queue.add(objRequest);

        Log.e("DataUsage",objRequest.toString());
    }
}
