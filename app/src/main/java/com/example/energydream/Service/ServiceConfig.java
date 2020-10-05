package com.example.energydream.Service;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceConfig {

    private Context context;
    ActivityManager manager;

    public ServiceConfig(Context context){
        this.context = context;
        this.manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public boolean getServiceState()
    {
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }


}