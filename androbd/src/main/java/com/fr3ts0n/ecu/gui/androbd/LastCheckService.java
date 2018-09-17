package com.fr3ts0n.ecu.gui.androbd;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class LastCheckService extends Service {
    private Context mContext;
    private PerfManager pm;
    private static final int SEND_RACE_DATA_LAST_DELAY = 5*1000; //5초후 최종정보날림.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public LastCheckService(){
        Log.i("aobd","LastCheckService/LastCheckService(Context myContext)");
        mContext = getBaseContext();
        pm = new PerfManager(mContext);
    }

    @Override
    public void onCreate(){
        Log.i("aobd","LastCheckService/onCreate");
        //@@끝날때 서비스 하나 만든후에 5분지났는지 체크하고 racefinal 통신날리기, eventindex++ 프레퍼런스 저장
        Handler dshandler = new Handler(Looper.getMainLooper());
        dshandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 내용

                ItemEcu ie01 = pm.get_pref_last_itemEcu();
                Toast.makeText(mContext, "5초 있다가 데이터날리고 서비스꺼짐.", Toast.LENGTH_LONG).show();
                Log.i("aobd","LastCheckService/ie01:"+ie01.toString());
            }
        }, SEND_RACE_DATA_LAST_DELAY);

    }
    @Override
    public void onDestroy() {
        Toast.makeText(mContext, "LastCheckService Destroyed", Toast.LENGTH_LONG).show();
    }




}
