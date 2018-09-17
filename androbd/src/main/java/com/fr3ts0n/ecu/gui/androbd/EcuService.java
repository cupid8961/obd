package com.fr3ts0n.ecu.gui.androbd;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import static com.fr3ts0n.ecu.gui.androbd.SettingsActivity.ELM_TIMING_SELECT;

public class EcuService extends Service {
    private Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public EcuService(){
    }

    public EcuService(Context myContext){
        mContext = myContext;

    }

    @Override
    public void onCreate(){
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }




}
