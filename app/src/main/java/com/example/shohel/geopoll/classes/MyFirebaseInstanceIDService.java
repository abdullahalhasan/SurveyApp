package com.example.shohel.geopoll.classes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        storeREgIdInPref(refreshedToken);



        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        Intent registrationIntent = new Intent(Config.REG_COMPLETE);
        registrationIntent.putExtra("token",refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationIntent);
    }


    private void sendRegistrationToServer(String refreshedToken) {

    }

    private void storeREgIdInPref(String refreshToken) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_OREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("regTokenID",refreshToken);
        editor.commit();
    }
}
