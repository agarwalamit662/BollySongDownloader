package com.support.android.musicindia.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.support.android.musicindia.R;

import java.io.IOException;

public class RegistrationService extends IntentService {
    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("IN REGSERVICE","IN REGSERVICE");
        Log.e("IN REGSERVICE","IN REGSERVICE");
        Log.e("IN REGSERVICE","IN REGSERVICE");
        InstanceID myID = InstanceID.getInstance(this);

        try {
            String registrationToken = myID.getToken(
                    getString(R.string.gcm_SenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );



            Log.e("RegisToken: ",registrationToken);
            Log.e("RegisToken: ",registrationToken);
            Log.e("RegisToken: ", registrationToken);

            GcmPubSub subscription = GcmPubSub.getInstance(this);
            subscription.subscribe(registrationToken, "/topics/music_india", null);

            SharedPreferences settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("regToken",true);
            editor.commit();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
