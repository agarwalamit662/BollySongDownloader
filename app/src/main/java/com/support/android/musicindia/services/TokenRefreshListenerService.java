package com.support.android.musicindia.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

public class TokenRefreshListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {

        SharedPreferences settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("regToken",false);
        editor.commit();
        Intent i = new Intent(this, RegistrationService.class);
        startService(i);
    }
}
