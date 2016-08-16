/*
 * Copyright 2013-2014 Andrea De Cesare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.support.android.musicindia.application;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.support.android.musicindia.dto.DTOProviderSONG;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsFilesData;
import com.support.android.musicindia.services.MyIntentService;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerApplication extends Application {
	private static Context context;
    public int currentPage = -1;
	private String lastSearch;
    public SongsFilesData fd;
    public static List<Songs> queuedDownloads;
    public static boolean splashLoadedTiny = false;
    public static boolean splashLoadedTinyMainActivity = true;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        fd = new SongsFilesData();
        queuedDownloads = new ArrayList<Songs>();

        boolean runningService = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyIntentService.class.getName().equals(service.service.getClassName())) {
                //your service is running
                runningService = true;
            }
        }
        if(!runningService){

            DTOProviderSONG.deleteCompletedSongsfromDatabase(getApplicationContext());
            DTOProviderSONG.updateALLRUNNINGSONGIteminDatabase(getApplicationContext());
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("MusicPlayer", "Low memory condition!");

    }

    public static Context getContext() {
        return context;
    }
    

	public void setLastSearch(String lastSearch) {
		this.lastSearch = lastSearch;
	}
	
	public String getLastSearch() {
		return lastSearch;
	}

    @Override
    public void onTerminate(){
        super.onTerminate();
    }
}
