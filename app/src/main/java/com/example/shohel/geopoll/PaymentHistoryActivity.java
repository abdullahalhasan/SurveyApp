package com.example.shohel.geopoll;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shohel.geopoll.classes.BalanceChecker;
import com.example.shohel.geopoll.classes.HistoryListAdapter;
import com.example.shohel.geopoll.classes.TransactionData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.ECField;
import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    private BalanceChecker balanceChecker;
    private SharedPreferences sharedPref;
    private String userId;
    private String userMobile;
    private ArrayList<TransactionData> historyList;
    private ArrayAdapter historyAdapter;

    private TextView totalBalanceTV;
    private TextView curentBalanceTV;
    private ListView paymentHistoryListView;

    private String PAYMENT_HISTORY_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.geopoll_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setTitle("");



        if (!NetworkChecker.isInternetAvailable(getApplicationContext())){
            //Snackbar.make(totalBalanceTV, R.string.check_internent, Snackbar.LENGTH_LONG).show();
        } else {
            init();
            paymentHistoryRequest();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void init(){
        totalBalanceTV = (TextView) findViewById(R.id.balanceViewPHET);
        curentBalanceTV = (TextView) findViewById(R.id.currentBalanceViewPHET);
        paymentHistoryListView = (ListView) findViewById(R.id.balanceHistoryListView);
        historyList = new ArrayList();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPref.getString("user_id","");
        userMobile = sharedPref.getString("user_mobile","");
        balanceChecker = new BalanceChecker(this,userId);
        PAYMENT_HISTORY_URL = "https://mindscape.com.bd/api/v1/app/get/payment_history/"+userMobile.trim();
    }

    public void balanceHistoryRefresh(View view) {
        paymentHistoryRequest();
        Log.e("URL",PAYMENT_HISTORY_URL);
    }

    public void redeemPH(View view) {
        startActivity(new Intent(PaymentHistoryActivity.this,BalanceWithdrawalActivity.class));
        finish();
    }

    private void paymentHistoryRequest(){


        RequestQueue queue = Volley.newRequestQueue(this);
        //vectorData.clear();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PAYMENT_HISTORY_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(ServeyListActivity.this, "Response: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("PaymentHistoryList", response.toString());

                        try {
                            JSONObject paymentHistoryJsonObject = new JSONObject(String.valueOf(response));
                            JSONArray paymentHistoryJsonArray = paymentHistoryJsonObject.getJSONArray("payment_history");


                            if (paymentHistoryJsonArray.length() != 0) {
                                double totalPayment = 0.0;
                                for (int i = 0; paymentHistoryJsonArray.length()>0; i++) {
                                    JSONObject transactionJsonObject = paymentHistoryJsonArray.getJSONObject(i);
                                    TransactionData transactionData = new TransactionData();

                                    transactionData.setPaymentMethod(transactionJsonObject.getString("payment_method"));
                                    transactionData.setAmount(transactionJsonObject.getString("amount"+" bdt"));
                                    transactionData.setPaymentStatus(transactionJsonObject.getString("status"));
                                    transactionData.setPaymentTime(transactionJsonObject.getString("updated_at"));

                                    historyList.add(transactionData);
                                    totalPayment = Double.valueOf(transactionJsonObject.getString("amount"));
                                }
                                historyAdapter = new HistoryListAdapter(PaymentHistoryActivity.this,historyList);
                                paymentHistoryListView.setAdapter(historyAdapter);
                                curentBalanceTV.setText(String.valueOf(totalPayment)+" BDT");
                                //Log.e("TotalPaymentReq",);
                            }

                        } catch (Exception e) {

                        }
                        /*try {

                            if (jsonArray.length() == 0) {
                                Toast.makeText(PaymentHistoryActivity.this, "Wish List Empty!", Toast.LENGTH_LONG).show();
                            }
                            Log.d("Json", jsonArray + "\n\n\n" + campArray);


                            for (int j = 0; j < campArray.length(); j++) {
                                campDataID = campArray.getString(j);
                                //campIDList.add(campDataID);
                                idList.add(campDataID);
                                Log.e("NewKeys", ""+idList);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objectData = jsonArray.getJSONObject(i);

                                DataTransfer dataTransfer = new DataTransfer();
                                dataTransfer.setCampaign_name(objectData.getString("campaign_title"));
                                dataTransfer.setCampaign_id(objectData.getString("id"));
                                dataTransfer.setActive_date(objectData.getString("active_date"));
                                dataTransfer.setExpire_date(objectData.getString("expire_date"));
                                dataTransfer.setCampaign_incentive_amount(objectData.getString("campaign_incentive_amount"));
                                dataTransfer.setCampaign_incentive_point(objectData.getString("campaign_incentive_point"));
                                dataTransfer.setCategory_name(objectData.getString("name"));

                                //vectorData.add(dataTransfer);

                                String campaignID = objectData.getString("id");
                                idListAll.add(campaignID);
                                if(!(idList.contains(campaignID))) {
                                    vectorData.add(dataTransfer);
                                    Log.e("DataVector", " "+idList+" "+campaignID);
                                    Log.e("DataVector Logic", " "+idList.contains(campaignID));
                                }

                                //Toast.makeText(getApplicationContext(), "merchant_name: "+objectData.getString("merchant_name"), Toast.LENGTH_SHORT).show();
                            }


                            *//*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BrowserActivity.this);
                            SharedPreferences.Editor editor = sp.edit();
                            Gson gson = new Gson();
                            String jsonSet = gson.toJson(boli_dataVecForVideosList);
                            editor.putString("boli_video_lists", jsonSet);
                            editor.commit();*//*
                            //sp.edit().putString("video_lists_update", "").commit();
                            survey_list.setAdapter(new CustomSurveyListAdapter(ServeyListActivity.this, vectorData));
                            survey_list.deferNotifyDataSetChanged();
                            survey_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    DataTransfer item = (DataTransfer) parent.getItemAtPosition(position);
                                    //Toast.makeText(ServeyListActivity.this, item.getCampaign_id(), Toast.LENGTH_SHORT).show();
                                    sp = PreferenceManager.getDefaultSharedPreferences(ServeyListActivity.this);
                                    if (sp.getString("mobile_verified", "").isEmpty()) {
                                        Intent i = new Intent(ServeyListActivity.this, ProfileOTPConfirm.class);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(ServeyListActivity.this, ServeyAnswerActivity.class);
                                        i.putExtra("campaign_id", item.getCampaign_id());
                                        startActivityForResult(i, 1);
                                        //startActivity(i);
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(error.getMessage());
                Log.e("VolleyError",error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // 3. Dodanie żądania na kolejkę.
        queue.add(jsonObjectRequest);
    }
}
