package com.example.shohel.geopoll;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    TextView totalBalanceTextView;
    TextView totalWithdrawalTextView;
    TextView availableBalanceTextView;
    TextView userMobileTextView;

    SharedPreferences sharedPreferences;
    String userID;
    String userMobile;
    String amount;
    String method;
    String totalBalance;
    String totalWithdrwal;
    String availableBalance;
    double balance = 0.0, withdrawBalance = 0.0, available_balance = 0.0;

    RequestQueue queue;
    JsonObjectRequest objRequest;
    JSONObject paymentObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_withdrawal);

        totalBalance = String.valueOf(balance);
        totalWithdrwal = String.valueOf(withdrawBalance);
        availableBalance = String.valueOf(available_balance);

        totalBalanceTextView = findViewById(R.id.total_earnings_withdraw);
        totalWithdrawalTextView = findViewById(R.id.total_withdraw_withdraw);
        availableBalanceTextView = findViewById(R.id.current_balance_withdraw);
        userMobileTextView = findViewById(R.id.user_mobile_payment_request);
        paymentRequestButton = findViewById(R.id.payment_request_button);
        withdrawalAmountEditText = findViewById(R.id.withdrawal_amount);
        payment_method = (Spinner) findViewById(R.id.withdrawal_method_spinner);

        /*totalBalance = String.valueOf(MainPageActivity.balance);
        totalWithdrwal = String.valueOf(MainPageActivity.withdrawBalance);
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BalanceWithdrawalActivity.this);
        userID = sharedPreferences.getString("user_id","");
        userMobile = sharedPreferences.getString("user_mobile","");



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
                    withdrawalAmountEditText.setError("Input Withdrawal Amount!!");
                } else {
                    amount = withdrawalAmountEditText.getText().toString().trim();
                    double withdraw = Double.valueOf(amount);
                    if (withdraw < 200.0 || Double.valueOf(availableBalance) < withdraw || (withdraw > 1000 && method.contains("TalkTime"))) {
                        if (withdraw < 200) {
                            withdrawalAmountEditText.setError("Minimum Withdrawal is 200tk!");
                            //Toast.makeText(this, "Minimum Withdrawal is 200tk!", Toast.LENGTH_SHORT).show();
                        } else if(withdraw > 1000 && method.contains("TalkTime")){
                            withdrawalAmountEditText.setError("Maximum TalkTime Withdrawal is 1000tk!");
                            //Toast.makeText(this, "You balance is too low!", Toast.LENGTH_SHORT).show();
                        } /*else {
                            withdrawalAmountEditText.setError("Requested amount is lower than current balance!");
                            //Toast.makeText(this, "You balance is too low!", Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        //Toast.makeText(BalanceWithdrawalActivity.this, amount, Toast.LENGTH_SHORT).show();
                        paymentHistoryUpload();
                    }
                }
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentBalanceRequest();

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

        JSONObject postObject = new JSONObject();
        try {
            paymentObject.put("user_id",userID);
            paymentObject.put("user_mobile", userMobile);
            paymentObject.put("payment_method",method);
            paymentObject.put("amount",amount);
            postObject.put("userinfo", paymentObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        objRequest = new JsonObjectRequest(Request.Method.POST, URL_PAYMENT,postObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("OnResponse",String.valueOf(response));
                        Toast.makeText(BalanceWithdrawalActivity.this, R.string.payment_request_success, Toast.LENGTH_LONG).show();
                        withdrawalAmountEditText.getText().clear();
                        withdrawalAmountEditText.setEnabled(false);
                        payment_method.setEnabled(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("On Error", String.valueOf(error.getMessage()));
            }
        });

        queue.add(objRequest);

        Log.d("Payment Request",postObject.toString());
    }

    public void currentBalanceRequest(){
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
                                    for (int i = 0; i<paymentArray.length(); i++){
                                        JSONObject objectData = paymentArray.getJSONObject(i);
                                        withdrawBalance = Double.parseDouble(objectData.getString("total_amount"));
                                        //Toast.makeText(getApplicationContext(), "merchant_name: "+objectData.getString("merchant_name"), Toast.LENGTH_SHORT).show();
                                    }



                                    available_balance = balance - withdrawBalance;
                                    totalBalance = String.valueOf(balance);
                                    totalWithdrwal = String.valueOf(withdrawBalance);
                                    availableBalance = String.valueOf(available_balance);
                                    totalBalanceTextView.setText(totalBalance+" BDT");
                                    totalWithdrawalTextView.setText(totalWithdrwal+" BDT");
                                    availableBalanceTextView.setText(availableBalance+" BDT");
                                    Log.e("Balance",""+available_balance+" "+balance+" "+ withdrawBalance);
                                }
                                //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                if (!sharedPreferences.getString("current_balance","").equals(String.valueOf(balance))){
                                    availableBalanceTextView.setText("  Current Balance:- "+String.valueOf(balance)+" BDT ");
                                    sharedPreferences.edit().putString("current_balance",String.valueOf(balance)).commit();
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
                Toast.makeText(getApplicationContext(),"Check Internet Connection",Toast.LENGTH_SHORT).show();
            }
        });
        // 3. Dodanie żądania na kolejkę.
        queue.add(jsonObjectRequest);
    }

    public void mobileNumberBalanceWithdrawalPrompt(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Alert!!");
        dialogBuilder.setMessage(R.string.mobile_cant_be_changed_alert);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setIcon(R.drawable.paypoll_symble);
        dialogBuilder.show();
    }

    public void paymentMethodAlert(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Caution!!");
        dialogBuilder.setMessage(R.string.payment_request);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setIcon(R.drawable.paypoll_symble);
        dialogBuilder.show();
    }
}