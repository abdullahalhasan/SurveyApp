package com.example.shohel.geopoll.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shohel.geopoll.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BDDL-102 on 11/14/2018.
 */

public class BalanceChecker {

    private Context context;
    private String userId;
    private String TAG = "BalanceChecker";

    private SharedPreferences sharedPref;

    private double userTotalBalance = 0;
    private double userAvailableBalance = 0;
    private double userTotalWithdrawalBalance = 0;
    private double totalRequestPayment = 0.0;

    private final String URL_BALANCE = "https://mindscape.com.bd/api/v1/get/user/activities";
    public BalanceChecker(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        withdrawalBalanceRequest();
        totalBalanceRequest();


        Log.e("TotalPaymentReqSF",sharedPref.getString("user_total_withdrawal_balance","0.0")+" BDT");
    }

    public double currentBalance(){
        String currentBalSP = sharedPref.getString("user_available_balance","0");
        Log.e(TAG,"CurrentBalSP:"+currentBalSP);
        return Double.valueOf(currentBalSP);
    }

    public double totalBalance() {
        String totalBalSP = sharedPref.getString("user_total_balance","0");
        Log.e(TAG,"TotalBalSP:"+totalBalSP);
        return Double.valueOf(totalBalSP);
    }

    public double totalWithdrawalBalance() {
        String withdrawBalSP = sharedPref.getString("user_total_withdrawal_balance","0");
        Log.e(TAG,"WithdrawBalSP:"+withdrawBalSP);
        return Double.valueOf(withdrawBalSP);
    }

    /*private void currentBalanceRequest(){


        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject balanceRequestObject = new JSONObject();
        JSONObject userIdObject = new JSONObject();

        try {
            userIdObject.put("user_id",userId);
            balanceRequestObject.put("userinfo",userIdObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_BALANCE, balanceRequestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(ServeyListActivity.this, "Response: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("Survey List",response.toString());

                        try {
                            JSONObject balanceJsonObject =new JSONObject(String.valueOf(response));
                            JSONArray balanceJsonArray = balanceJsonObject.getJSONArray("activities");
                            JSONArray paymentArray = balanceJsonObject.getJSONArray("payments");
                            JSONObject getBalanceJsonObject = response.getJSONObject("success");
                            if (getBalanceJsonObject.getString("statusCode").trim().equals("200")) {
                                if (balanceJsonArray.length() ==0){
                                }else {

                                    for (int i = 0; i<balanceJsonArray.length(); i++){
                                        JSONObject objectData = balanceJsonArray.getJSONObject(i);
                                        userTotalBalance+=Double.parseDouble(objectData.getString("campaign_incentive_amount"));

                                    }

                                    userTotalWithdrawalBalance = Double.valueOf(sharedPref.getString("withdrawalRequest", "0"));

                                    Log.e("TotalWith","Withdrawal:"+userTotalWithdrawalBalance);

                                    userAvailableBalance = userTotalBalance - userTotalWithdrawalBalance;

                                    sharedPref.edit().putString("user_total_balance",String.valueOf(userTotalBalance)).commit();
                                    sharedPref.edit().putString("user_available_balance",String.valueOf(userAvailableBalance)).commit();
                                    sharedPref.edit().putString("user_total_withdrawal_balance",String.valueOf(userTotalWithdrawalBalance)).commit();
                                    Log.e("BalanceHistory","Available:"+userAvailableBalance+" Total:"+userTotalBalance+" Withdrawal:"+ userTotalWithdrawalBalance);
                                }


                                if (!sharedPref.getString("user_total_balance","").equals(String.valueOf(userTotalBalance))){
                                    sharedPref.edit().putString("user_total_balance",String.valueOf(userTotalBalance)).commit();
                                    sharedPref.edit().putString("user_available_balance",String.valueOf(userAvailableBalance)).commit();
                                    sharedPref.edit().putString("user_total_withdrawal_balance",String.valueOf(userTotalWithdrawalBalance)).commit();
                                }
                            }else {
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.e(error.getMessage());
                Toast.makeText(context, R.string.check_internent,Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }*/

    public void totalBalanceRequest(){


        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject balanceRequestObject = new JSONObject();
        JSONObject userIdObject = new JSONObject();

        try {
            userIdObject.put("user_id",userId);
            balanceRequestObject.put("userinfo",userIdObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_BALANCE, balanceRequestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(ServeyListActivity.this, "Response: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("Survey List",response.toString());

                        try {
                            JSONObject balanceJsonObject =new JSONObject(String.valueOf(response));
                            JSONArray balanceJsonArray = balanceJsonObject.getJSONArray("activities");
                            //JSONArray paymentArray = balanceJsonObject.getJSONArray("payments");
                            JSONObject getBalanceJsonObject = response.getJSONObject("success");
                            if (getBalanceJsonObject.getString("statusCode").trim().equals("200")) {
                                if (balanceJsonArray.length() ==0){
                                }else {

                                    for (int i = 0; i<balanceJsonArray.length(); i++){
                                        JSONObject objectData = balanceJsonArray.getJSONObject(i);
                                        userTotalBalance+=Double.parseDouble(objectData.getString("campaign_incentive_amount"));

                                    }
                                    Log.e(TAG,"Total:"+userTotalBalance);
                                    sharedPref.edit()
                                            .putString("user_total_balance",String.valueOf(userTotalBalance))
                                            .commit();
                                    double withdrawal = Double.valueOf(sharedPref.getString("user_total_withdrawal_balance","0"));
                                    Log.e(TAG,"Withdrawal:"+withdrawal);
                                    userAvailableBalance = userTotalBalance - withdrawal;
                                    Log.e(TAG,"Available:"+userAvailableBalance);
                                    sharedPref.edit()
                                            .putString("user_available_balance",String.valueOf(userAvailableBalance))
                                            .commit();

                                }

                            }else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.e(error.getMessage());
                Toast.makeText(context, R.string.check_internent,Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }




    public void withdrawalBalanceRequest() {

        RequestQueue queue = Volley.newRequestQueue(context);

        String userMobile = sharedPref.getString("user_mobile","");
        String PAYMENT_HISTORY_URL = "https://mindscape.com.bd/api/v1/app/get/payment_history/"+userMobile;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PAYMENT_HISTORY_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(ServeyListActivity.this, "Response: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("PaymentHistoryList", response.toString());

                        try {
                            JSONObject paymentHistoryJsonObject = new JSONObject(String.valueOf(response));
                            JSONArray paymentHistoryJsonArray = paymentHistoryJsonObject.getJSONArray("payment_history");
                            JSONObject getResponseJsonObject = response.getJSONObject("success");

                            if (getResponseJsonObject.getString("statusCode").trim().equals("200")) {
                                Log.e(TAG,getResponseJsonObject.getString("statusCode"));
                                if (paymentHistoryJsonArray.length() == 0) {

                                } else {
                                    Log.e(TAG, "TotalRqBFPay:" + totalRequestPayment + " BDT");
                                    for (int i = 0; paymentHistoryJsonArray.length() > 0; i++) {
                                        JSONObject transactionJsonObject = paymentHistoryJsonArray.getJSONObject(i);

                                        totalRequestPayment += Double.valueOf(transactionJsonObject.getString("amount"));
                                        sharedPref.edit()
                                                .putString("user_total_withdrawal_balance", String.valueOf(totalRequestPayment))
                                                .commit();
                                        Log.e(TAG, "TotalRqPay:" + totalRequestPayment + " BDT");

                                    } //for loop end
                                    Log.e(TAG, "TotalRqAFPay:");
                                } //if array length end
                            }//if success code end
                        } catch (Exception e) {
                            e.printStackTrace();
                        } //try catch end

                    }//OnResponse end
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.e(error.getMessage());
                Log.e("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });//ResponseListener end

        queue.add(jsonObjectRequest);
    }//WithdrawalBalanceRequest method end
}
