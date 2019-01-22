package com.example.shohel.geopoll;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shohel.geopoll.classes.BalanceChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BalanceWithdrawalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText withdrawalAmountEditText;
    Button paymentRequestButton;
    Spinner payment_method;
    ArrayAdapter<CharSequence> paymentMethodAdapter;
    String URL_PAYMENT = "https://www.mindscape.com.bd/api/v1/user/payment";
    String URL_BALANCE = "https://mindscape.com.bd/api/v1/get/user/activities";

    BalanceChecker balanceChecker;

    TextView totalBalanceTextView;
    TextView totalWithdrawalTextView;
    TextView availableBalanceTextView;
    TextView userMobileTextView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID;
    String userMobile;
    String requestedAmount;
    String method;
    String totalBalanceString;
    String totalWithdrwalString;
    String availableBalanceString;
    double totalBalanceAmount = 0.0, withdrawBalanceSP, availableBalanceAmount = 0.0, withdrawalBalanceAmount = 0.0;

    String TAG = "BalanceWithAct";
    RequestQueue queue;
    JsonObjectRequest objRequest;
    JSONObject paymentObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_withdrawal);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BalanceWithdrawalActivity.this);
        editor = sharedPreferences.edit();

        userID = sharedPreferences.getString("user_id","");
        userMobile = sharedPreferences.getString("user_mobile","");

        balanceChecker = new BalanceChecker(BalanceWithdrawalActivity.this,userID);

        totalBalanceTextView = (TextView) findViewById(R.id.total_earnings_withdraw);
        totalWithdrawalTextView = (TextView) findViewById(R.id.total_withdraw_withdraw);
        availableBalanceTextView = (TextView) findViewById(R.id.current_balance_withdraw);
        userMobileTextView = (TextView) findViewById(R.id.user_mobile_payment_request);
        paymentRequestButton = (Button) findViewById(R.id.payment_request_button);
        withdrawalAmountEditText = (EditText) findViewById(R.id.withdrawal_amount);
        payment_method = (Spinner) findViewById(R.id.withdrawal_method_spinner);

        /*totalBalance = String.valueOf(MainPageActivity.balance);
        totalWithdrwal = String.valueOf(MainPageActivity.withdrawBalanceSP);
        availableBalance = String.valueOf(MainPageActivity.available_balance);*/

        /*paymentRequestButton.setEnabled(false);
        withdrawalAmountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentRequestButton.setEnabled(true);
            }
        });*/


        queue =  Volley.newRequestQueue(this);
        paymentObject = new JSONObject();

        //currentBalanceRequest();

        setBalances();

        userMobileTextView.setText(userMobile);

        paymentMethodAdapter = ArrayAdapter.createFromResource(this,R.array.withdrwarl_method_arrays,
                R.layout.custom_spnnier);
        paymentMethodAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        payment_method.setAdapter(paymentMethodAdapter);

        payment_method.setOnItemSelectedListener(this);

        paymentRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withdrawalAmountEditText.getText().toString().isEmpty()){
                    withdrawalAmountEditText.setError(getString(R.string.input_withdrawal_amount));
                } else {
                    requestedAmount = withdrawalAmountEditText.getText().toString().trim();
                    double withdraw = Double.valueOf(requestedAmount);
                    if (method.equals("Select")) {
                        Toast.makeText(BalanceWithdrawalActivity.this, R.string.selectpaymentalert, Toast.LENGTH_SHORT).show();
                    } else {
                        if (withdraw < 200.0 || Double.valueOf(availableBalanceString) < withdraw || (withdraw > 1000 && method.contains("TalkTime"))) {
                            if (withdraw < 200) {
                                withdrawalAmountEditText.setError(getString(R.string.minimum_withdrawal_200));
                                //Toast.makeText(this, "Minimum Withdrawal is 200tk!", Toast.LENGTH_SHORT).show();
                            } else if (withdraw > 1000 && method.contains("TalkTime")) {
                                withdrawalAmountEditText.setError(getString(R.string.maximum_talktime_1000));
                                //Toast.makeText(this, "You balance is too low!", Toast.LENGTH_SHORT).show();
                            } else {
                            withdrawalAmountEditText.setError(getString(R.string.req_lower_than_available));
                            //Toast.makeText(this, "You balance is too low!", Toast.LENGTH_SHORT).show();
                        }
                        } else {
                            //Toast.makeText(BalanceWithdrawalActivity.this, requestedAmount, Toast.LENGTH_SHORT).show();
                            //paymentHistoryUpload();


                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BalanceWithdrawalActivity.this);
                            dialogBuilder.setTitle(R.string.paypoll);
                            dialogBuilder.setMessage(R.string.confrim);
                            dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    paymentHistoryUpload();
                                }
                            });
                            dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialogBuilder.setIcon(R.drawable.paypoll_symble);
                            dialogBuilder.show();
                        }
                    }
                }
            }

        });


    }

    private void setBalances() {
        BalanceChecker balanceChecker = new BalanceChecker(BalanceWithdrawalActivity.this,userID);
        totalBalanceAmount = balanceChecker.totalBalance();
        withdrawalBalanceAmount = balanceChecker.totalWithdrawalBalance();
        availableBalanceAmount = balanceChecker.currentBalance();

        totalBalanceString = String.valueOf(totalBalanceAmount);
        totalWithdrwalString = String.valueOf(withdrawalBalanceAmount);
        availableBalanceString = String.valueOf(availableBalanceAmount);

        Log.e(TAG,totalWithdrwalString+","+totalWithdrwalString+","+availableBalanceString);

        totalBalanceTextView.setText(totalBalanceString+" BDT");
        totalWithdrawalTextView.setText(totalWithdrwalString+" BDT");
        availableBalanceTextView.setText(availableBalanceString+" BDT");

    }

    @Override
    protected void onStart() {
        super.onStart();
        //currentBalanceRequest();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        method = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(this, ""+method, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*public void paymentRequest(View view) {

    }*/

    private void paymentHistoryUpload() {
        Log.e(TAG,"PaymentHistoryUploadIn");
        JSONObject postObject = new JSONObject();
        try {
            paymentObject.put("user_id",userID);
            paymentObject.put("user_mobile", userMobile);
            paymentObject.put("payment_method",method);
            paymentObject.put("amount", requestedAmount);
            postObject.put("userinfo", paymentObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("PaymentRequest:",postObject.toString());
        objRequest = new JsonObjectRequest(Request.Method.POST, URL_PAYMENT,postObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("OnResponse",String.valueOf(response));

                        try {
                            JSONObject paymentSucJsonObj = response.getJSONObject("success");
                            Log.e("paymentSucJsonObj",paymentSucJsonObj.getString("statusCode"));
                            String statusCode = paymentSucJsonObj.getString("statusCode");
                            if (statusCode.contains("2000")) {
                                Log.e("paymentSucJsonObj","True in if condition");
                                //sharedpreferenceWithdrwalTracking(Double.valueOf(requestedAmount));
                                //currentBalanceRequest();
                                availableBalanceTextView.setText("0.0 BDT");
                                totalWithdrawalTextView.setText("0.0 BDT");
                                totalBalanceTextView.setText("0.0 BDT");

                                setBalances();
                                Toast.makeText(BalanceWithdrawalActivity.this, R.string.payment_request_success, Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(BalanceWithdrawalActivity.this,MainPageActivity.class));
                            } else {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BalanceWithdrawalActivity.this);
                                dialogBuilder.setTitle("Alert!!");
                                dialogBuilder.setMessage(R.string.server_error);
                                dialogBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialogBuilder.setIcon(R.drawable.paypoll_symble);
                                dialogBuilder.show();
                            }
                            Log.e("OnResponse",String.valueOf(response));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        withdrawalAmountEditText.getText().clear();
                        withdrawalAmountEditText.setEnabled(false);
                        payment_method.setEnabled(false);
                        //currentBalanceRequest();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("On Error", String.valueOf(error.getMessage()));
            }
        });

        queue.add(objRequest);

        Log.d("PaymentRequest",postObject.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BalanceWithdrawalActivity.this,MainPageActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void mobileNumberBalanceWithdrawalPrompt(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.caution);
        dialogBuilder.setMessage(R.string.mobile_cant_be_changed_alert);
        dialogBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setIcon(R.drawable.paypoll_symble);
        dialogBuilder.show();
    }

    public void paymentMethodAlert(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Caution!!");
        dialogBuilder.setMessage(R.string.payment_request);
        dialogBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setIcon(R.drawable.paypoll_symble);
        dialogBuilder.show();
    }

    /*private double sharedpreferenceWithdrwalTracking(double amountRequest) {
        double withdrawalRequestAmount = Double.valueOf(sharedPreferences.getString("withdrawalRequest","0"));
        Log.e("SPWithdrawalAmountGet",""+withdrawalRequestAmount);
        withdrawalRequestAmount += amountRequest;
        editor.putString("withdrawalRequest",String.valueOf(withdrawalRequestAmount));
        editor.commit();
        editor.putString("user_available_balance",String.valueOf(withdrawalRequestAmount)).commit();
        Log.e("SPWithdrawalAmountPut",""+sharedPreferences.getString("withdrawalRequest","0"));
        return withdrawalRequestAmount;

    }*/



    /*public void currentBalanceRequest(){
        Log.e("BalWithAct","CurrentBalanceReqIn");
        balance = 0.0;
        available_balance = 0.0;
        withdrawBalanceSP = Double.valueOf(sharedPreferences.getString("withdrawalRequest","0"));

        RequestQueue queue = Volley.newRequestQueue(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        JSONObject obj = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONObject obj3 = new JSONObject();
        try {
            obj2.put("user_id",sharedPreferences.getString("user_id",""));
            obj.put("userinfo",obj2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 2. Utworzenie żądania
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_BALANCE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(ServeyListActivity.this, "Response: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("Survey List",response.toString());

                        //mProgressDialog.dismiss();
                        try {
                            JSONObject jsonObject =new JSONObject(String.valueOf(response));
                            JSONArray jsonArray = jsonObject.getJSONArray("activities");
                            JSONArray paymentArray = jsonObject.getJSONArray("payments");
                            JSONObject jsonObject2 = response.getJSONObject("success");
                            if (jsonObject2.getString("statusCode").trim().equals("200")) {
                                if (jsonArray.length() ==0){
                                }else {

                                    for (int i = 0; i<jsonArray.length(); i++){
                                        JSONObject objectData = jsonArray.getJSONObject(i);
                                        balance+=Double.parseDouble(objectData.getString("campaign_incentive_amount"));
                                        //Toast.makeText(getApplicationContext(), "merchant_name: "+objectData.getString("merchant_name"), Toast.LENGTH_SHORT).show();
                                    }
                                    *//*for (int i = 0; i<paymentArray.length(); i++){
                                        JSONObject objectData = paymentArray.getJSONObject(i);
                                        withdrawalBalance = Double.parseDouble(objectData.getString("total_amount"));
                                        //Toast.makeText(getApplicationContext(), "merchant_name: "+objectData.getString("merchant_name"), Toast.LENGTH_SHORT).show();
                                    }*//*

                                    //withdrawBalanceSP = Double.valueOf(sharedPreferences.getString("withdrawalRequest","0"));
                                    withdrawBalanceSP = Double.valueOf(sharedPreferences.getString("user_total_withdrawal_balance","0"));
                                    *//*Log.e("CheckBalance:",withdrawalBalance+","+withdrawBalanceSP);
                                    if (withdrawalBalance >= (withdrawBalanceSP+withdrawalBalance)) {
                                        totalWithdrwal = String.valueOf(withdrawalBalance);
                                    } else {
                                        Log.e("ElseTotalWithdrawal:",withdrawalBalance+"+"+withdrawBalanceSP);
                                        totalWithdrwal =  String.valueOf(withdrawBalanceSP+withdrawalBalance);
                                    }*//*



                                    available_balance = balance - withdrawBalanceSP;
                                    totalBalance = String.valueOf(balance);
                                    availableBalance = String.valueOf(available_balance);
                                    totalWithdrwal = String.valueOf(withdrawBalanceSP);

                                    Log.e("balanceReqString",availableBalance);
                                    totalBalanceTextView.setText(totalBalance+" BDT");
                                    totalWithdrawalTextView.setText(totalWithdrwal+" BDT");
                                    availableBalanceTextView.setText(availableBalance+" BDT");

                                    Log.e("Balance","available:"+available_balance+" balance:"+balance+" withdrawal:"+ totalWithdrwal);
                                }
                                //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                if (!sharedPreferences.getString("total_balance","").equals(String.valueOf(balance))){

                                    sharedPreferences.edit().putString("total_balance",availableBalance).commit();
                                    availableBalanceTextView.setText(String.valueOf(availableBalance)+" BDT ");
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
                Toast.makeText(getApplicationContext(), R.string.check_internent,Toast.LENGTH_SHORT).show();
            }
        });
        // 3. Dodanie żądania na kolejkę.
        queue.add(jsonObjectRequest);
    }*/
}