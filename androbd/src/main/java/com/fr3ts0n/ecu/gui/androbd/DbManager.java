package com.fr3ts0n.ecu.gui.androbd;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbManager {

    private static String IP_ADDRESS = "http://seen.aliensoft.gethompy.com/kst/insert.php";
    private static String TAG = "aobd";
    private Context mContext;

    public DbManager(Context context){
        mContext = context;
        return ;
    }

    void okdm(){

        Log.i("aobd","okdm");
    }
    void send_db(){
        Log.i("aobd","send_db");

        long time_now = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String time_str = dayTime.format(new Date(time_now));


        String time = ""+time_str;
        String velociy = "v_"+time_str;
        String distance = "d_"+time_str;
        String fuel = "f_"+time_str;

        InsertData task = new InsertData();
        task.execute(IP_ADDRESS,time,velociy,distance,fuel);


    }



    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(mContext, "Please Wait", Toast.LENGTH_SHORT).show();


            //progressDialog = ProgressDialog.show(mContext,"Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {


            String time = (String) params[1];
            String velocity = (String) params[2];
            String distance = (String) params[3];
            String fuel = (String) params[4];

            String serverURL = (String) params[0];

            String postParameters = "time=" + time + "&velocity=" + velocity + "&distance=" + distance + "&fuel=" + fuel;

            Log.d(TAG, "serverURL : " + serverURL);
            Log.d(TAG, "postParameters : " + postParameters);

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
