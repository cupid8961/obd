package com.fr3ts0n.ecu.gui.androbd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class PerfManager {
    SharedPreferences prefs;
    Context mContext;

    public PerfManager() {
    }

    public PerfManager(Context myContext) {
        mContext = myContext;
        prefs = mContext.getSharedPreferences("pref", MODE_PRIVATE);
    }


    public void eventindex() {


        return ;
    }

    public ItemEcu get_pref_last_itemEcu() {
        Log.i("aobd_i","get_pref_last_itemEcu");
        int eventindex;
        int no;
        String driver;
        String timenow;
        String phonenum;
        String obdmacaddress;
        int distance;
        int velocity;
        int velocityaver = 0;
        double fuelefficiency = 0;
        double fuelamount;
        double internaltem;
        double externaltem;
        String errorlog;

        eventindex = prefs.getInt("eventindex_last", 0);
        no = prefs.getInt("no_last_" + eventindex, 0);
        driver = prefs.getString("driver", "ID_NULL");

        long time_now = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time_str = dayTime.format(new Date(time_now));
        timenow = prefs.getString("timenow", time_str);

        phonenum = prefs.getString("phonenum", "PN_NULL");
        obdmacaddress = prefs.getString("obdmacaddress", "OMA_NULL");

        distance = prefs.getInt("distance_" + eventindex + "_" + no, 0);
        velocity = prefs.getInt("velocity_" + eventindex + "_" + no, 0);


        fuelamount = (double) prefs.getFloat("fuelamount_" + eventindex + "_" + no, 0);
        internaltem = (double) prefs.getFloat("internaltem_" + eventindex + "_" + no, 0);
        externaltem = (double) prefs.getFloat("externaltem_" + eventindex + "_" + no, 0);
        errorlog = prefs.getString("errorlog_" + eventindex + "_" + no, "EL_NULL");

        return new ItemEcu(eventindex, no, driver, timenow, phonenum, obdmacaddress, distance, velocity, velocityaver, fuelefficiency, fuelamount, internaltem, externaltem, errorlog);

    }

    public void no_last_plus() {

        int eventindex = prefs.getInt("eventindex_last", 0);
        int no = prefs.getInt("no_last_"+eventindex, 0);
        no++;

        //write
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("no_last_"+eventindex, no);
        editor.commit();
        Log.i("aobd","plused / no_last : "+no);

    }

    public void eventindex_last_plus() {

        int eventindex = prefs.getInt("eventindex_last", 0);
        eventindex++;

        //write
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("eventindex_last", eventindex);
        editor.commit();
        Log.i("aobd","plused / eventindex_last : "+eventindex);

    }
}
