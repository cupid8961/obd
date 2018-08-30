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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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
    void send_db(String my_errorlog){
        Log.i("aobd","send_db");

        long time_now = System.currentTimeMillis();

        Log.i("aobd","time_now : "+time_now);

        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String time_str = dayTime.format(new Date(time_now));

        Calendar oCalendar = Calendar.getInstance( );  // 현재 날짜/시간 등의 각종 정보 얻기
/*
        System.out.println("현재 년: " +  oCalendar.get(Calendar.YEAR));
        System.out.println("현재 월: " + (oCalendar.get(Calendar.MONTH) + 1));
        System.out.println("현재 일: " +  oCalendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(); // 다음줄로 행갈이 하기

        System.out.println("현재 시: " +  oCalendar.get(Calendar.HOUR_OF_DAY)); // 24시간제
        System.out.println("현재 분: " +  oCalendar.get(Calendar.MINUTE));
        System.out.println("현재 초: " +  oCalendar.get(Calendar.SECOND));
        System.out.println();
*/
        Random random = new Random();
        int velocity_rand = random.nextInt(40)+0; //1~15까지 랜덤수
        int distance_rand = 2302 ;

        String time = ""+time_str;
        String velociy = ""+velocity_rand;
        String distance = ""+(distance_rand+oCalendar.get(Calendar.MINUTE)/2);
        String fuel = ""+(50-oCalendar.get(Calendar.MINUTE)/5);
        

        InsertData task = new InsertData();
        task.execute(IP_ADDRESS,time,velociy,distance,fuel,my_errorlog);


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
            String errorlog = (String) params[5];

            String serverURL = (String) params[0];

            String postParameters = "time=" + time + "&velocity=" + velocity + "&distance=" + distance + "&fuel=" + fuel+ "&errorlog=" + errorlog;

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
