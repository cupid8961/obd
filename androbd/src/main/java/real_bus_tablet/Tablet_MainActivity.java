package real_bus_tablet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fr3ts0n.ecu.gui.androbd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tablet_MainActivity extends Activity implements View.OnClickListener {
    int sleep_time = 2000;
    ArrayList<String> mSelectedItems;
    private ImageView view1;
    private ImageView view3, iw1, iw2, iw3, iw4, iw5;
    private int last_count;
    private Button tab_first, tab_second, next_arrive_button, end_drive_btn;
    int for_check;
    private Button test;

    String arrive_next_station_id;
    private TextView t_route_name;
    private TextView s_n_1, s_n_2, s_n_3, s_n_4, s_n_5;
    private TextView a_t_1, a_t_2, a_t_3, a_t_4, a_t_5;
    private TextView n_s_1, n_s_2, n_a_1, n_a_2, n_b_1, n_b_2;
    private TextView board_count;


    int check_count = 0, thread_check = 0;

    private int board_cnt;
    Login_Get_Set lgs;

    private final int FRAGMENT1 = 1;
    private final int FRAGMENT2 = 2;

    Tablet_First_View_Get_Set[] fgs;

    int n_count = 0;

    boolean check_thread = true;

    String now_station = "";
    String now_arrive_time = "";
    int now_board_count = 0;
    int total_now_board_count = 0;

    String teach_buslist_type[], teach_buslist_cont[], check_list_count[];
    boolean text_checked[];
    int teach_buslist_cont_order[], teach_buslist_type_order[];

    Button ref_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet__main);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#16AFC5"));
        }
        init();
    }

    public void init() {
        Intent intent = getIntent();
        lgs = (Login_Get_Set) intent.getSerializableExtra("OBJECT");

        first_view_data();

        Log.i("OS123", "first_activity" + lgs.getRoute_id());

        view1 = (ImageView) findViewById(R.id.image1);
        view3 = (ImageView) findViewById(R.id.image3);

        tab_first = (Button) findViewById(R.id.bt_tab1);
        tab_second = (Button) findViewById(R.id.bt_tab2);
        tab_first.setOnClickListener(this);
        tab_second.setOnClickListener(this);
        next_arrive_button = (Button) findViewById(R.id.next_station_btn);
        next_arrive_button.setOnClickListener(this);
        end_drive_btn = (Button) findViewById(R.id.exit_drive);
        end_drive_btn.setOnClickListener(this);

        iw1 = (ImageView) findViewById(R.id.iw1);
        iw2 = (ImageView) findViewById(R.id.iw2);
        iw3 = (ImageView) findViewById(R.id.iw3);
        iw4 = (ImageView) findViewById(R.id.iw4);
        iw5 = (ImageView) findViewById(R.id.iw5);

        t_route_name = (TextView) findViewById(R.id.top_route_name);

        s_n_1 = (TextView) findViewById(R.id.station_name1);
        s_n_2 = (TextView) findViewById(R.id.station_name2);
        s_n_3 = (TextView) findViewById(R.id.station_name3);
        s_n_4 = (TextView) findViewById(R.id.station_name4);
        s_n_5 = (TextView) findViewById(R.id.station_name5);

        a_t_1 = (TextView) findViewById(R.id.a_t_1);
        a_t_2 = (TextView) findViewById(R.id.a_t_2);
        a_t_3 = (TextView) findViewById(R.id.a_t_3);
        a_t_4 = (TextView) findViewById(R.id.a_t_4);
        a_t_5 = (TextView) findViewById(R.id.a_t_5);

        n_a_1 = (TextView) findViewById(R.id.n_a_1);
        n_a_2 = (TextView) findViewById(R.id.n_a_2);
        n_b_1 = (TextView) findViewById(R.id.n_b_1);
        n_b_2 = (TextView) findViewById(R.id.n_b_2);
        n_s_1 = (TextView) findViewById(R.id.n_s_1);
        n_s_2 = (TextView) findViewById(R.id.n_s_2);

        board_count = (TextView) findViewById(R.id.board_count);


        ref_btn = (Button)findViewById(R.id.ref_btn);

        // 노선이 하원용 or 등원용인지 판단
        if (lgs.getRoute_type().equals("하원")) {
            new mThread().start();
            Toast.makeText(Tablet_MainActivity.this, "하원", Toast.LENGTH_SHORT).show();
        } else if (lgs.getRoute_type().equals("등원"))
            new mThread2().start();
        Toast.makeText(Tablet_MainActivity.this, "등원", Toast.LENGTH_SHORT).show();


        ref_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lgs.getRoute_type().equals("하원")) {
                    new mThread_1().start();
                    Toast.makeText(Tablet_MainActivity.this, "새로고침", Toast.LENGTH_SHORT).show();
                } else if (lgs.getRoute_type().equals("등원"))
                    new mThread2_1().start();
                Toast.makeText(Tablet_MainActivity.this, "새로고침", Toast.LENGTH_SHORT).show();
            }
        });



    }

    //초기 메인 화면을 구성하기 위해 사용되는 함수, 이전 화면에서 사용자가 선택한 노선의 정보가 fgs 변수에 저장 되어 있는데, 노선 ID를 통해서 해당 테블릿의 메인화면을 구성하기 위한 데이터를 가져온다.
    //또한 받아온 데이터는 각 항목에 맞게 fgs클래스의 변수에 저장된다.
    private void first_view_data() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"first_view_data", new Response.Listener<String>() {
            //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/first_view_data", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("OS", "first_view_data = " + response.toString());
                try {
                    JSONArray jarray = new JSONArray(response);
                    fgs = new Tablet_First_View_Get_Set[jarray.length() + 6];

                    for (int z = 0; z < 3; z++) {
                        fgs[z] = new Tablet_First_View_Get_Set();
                        fgs[z].setSch_time("");
                        fgs[z].setStation_id("");
                        fgs[z].setStation_name("");
                        fgs[z].setReal_time_id(-1);
                        fgs[z].setCnt(0);
                        fgs[z].setReal_time_type("");

                        if (z == 2) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
                            Date date = new Date();
                            String strDate = dateFormat.format(date);
                            String strDate2[] = strDate.split(":");
                            String strDate3 = strDate2[0] + ":" + strDate2[1];

                            fgs[z].setSch_time(strDate);
                            fgs[z].setStation_id(lgs.getCenter_code() + "S0_00");
                            fgs[z].setStation_name(lgs.getCenter_name());
                            fgs[z].setReal_time_id(0);
                            fgs[z].setCnt(0);
                            fgs[z].setReal_time_type("출발");
                        }
                    }
                    for (int i = 0; i < jarray.length() + 3; i++) {
                        fgs[i + 3] = new Tablet_First_View_Get_Set();
                        if (i > jarray.length()) {
                            fgs[i + 3].setSch_time("");
                            fgs[i + 3].setStation_id("");
                            fgs[i + 3].setStation_name("");
                            fgs[i + 3].setReal_time_id(-1);
                            fgs[i + 3].setCnt(0);
                            fgs[i + 3].setReal_time_type("");
                        } else if (i == jarray.length()) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
                            Date date = new Date();
                            String strDate = dateFormat.format(date);
                            String strDate2[] = strDate.split(":");
                            String strDate3 = strDate2[0] + ":" + strDate2[1];
                            fgs[i + 3].setArrive_time(strDate3);

                            fgs[i + 3].setSch_time(strDate);
                            fgs[i + 3].setStation_id(lgs.getCenter_code() + "S0_00");
                            fgs[i + 3].setStation_name(lgs.getCenter_name());
                            fgs[i + 3].setReal_time_id(0);
                            fgs[i + 3].setCnt(0);
                            fgs[i + 3].setReal_time_type("출발");
                        } else if (i < jarray.length() + 1) {
                            JSONObject jsonObject = jarray.getJSONObject(i);
                            String[] array = jsonObject.getString("sch_time").split(":");
                            fgs[i + 3].setSch_time(array[0] + ":" + array[1]);
                            fgs[i + 3].setStation_id(jsonObject.getString("station_id"));
                            fgs[i + 3].setStation_name(jsonObject.getString("station_name"));
                            fgs[i + 3].setReal_time_id(jsonObject.getInt("real_time_id"));
                            fgs[i + 3].setCnt(jsonObject.getInt("cnt"));
                            fgs[i + 3].setReal_time_type(jsonObject.getString("real_time_type"));
                            board_cnt += fgs[i + 3].getCnt();
                        }

                    }
                    set_first_data(0);
                    callFragment(FRAGMENT1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("OS", error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("route_id", lgs.getRoute_id().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }


    //first_view_data()를 통해 들고온 데이터를 화면 UI에 표시하기 위한 함수
    public void set_first_data(int count) {
        t_route_name.setText(lgs.getRoute_name() + "(" + lgs.getRoute_type() + ")"); // 타이틀
        board_count.setText("0명 / " + board_cnt + "명"); // 전체 탑승인원
        Log.i("TESTASD", count + " count");
        int x = count;
        iw1.setImageResource(R.mipmap.point_1_x2);
        iw2.setImageResource(R.mipmap.point_1_x2);
        iw3.setImageResource(R.mipmap.start_point_1_x2);

        // 하단 바 정류장 이름
        s_n_1.setText("-");
        s_n_2.setText("-");
        s_n_3.setText(fgs[x + 2].getStation_name());
        s_n_4.setText(fgs[x + 3].getStation_name());
        s_n_5.setText(fgs[x + 4].getStation_name());

        // 하단 바 도착 시간 및 이름
        a_t_1.setText("-");
        a_t_2.setText("-");
        a_t_3.setText("운행 시작");
        String at4[] = fgs[x + 3].getSch_time().split(":");
        a_t_4.setText("도착예정시간\n" + at4[0] + "시" + at4[1] + "분");
        String at5[] = fgs[x + 4].getSch_time().split(":");
        a_t_5.setText("도착예정시간\n" + at5[0] + "시" + at5[1] + "분");

        //중간 데이터
        n_s_1.setText(fgs[x + 2].getStation_name());
        n_s_2.setText(fgs[x + 3].getStation_name());
        arrive_next_station_id = fgs[x + 3].getStation_id();
        n_a_1.setText("운행 시작");
        String n_a_2_1[] = fgs[x + 3].getSch_time().split(":");
        n_a_2.setText(n_a_2_1[0] + "시" + n_a_2_1[1] + "분");

        n_b_1.setText("탑승 인원 없음");
        n_b_2.setText("탑승예정  " + fgs[x + 3].getCnt() + "  " + "명");
    }


    // 버튼 클릭 이벤트(탭1,탭2,운행종료,정류장지나가기)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 원아 리스트 프레그먼트 이동
            case R.id.bt_tab1:
                // '버튼1' 클릭 시 '프래그먼트1' 호출
                callFragment(FRAGMENT1);
                break;
            // 지각/결석 원아 조회 이동 프레크먼트
            case R.id.bt_tab2:
                // '버튼2' 클릭 시 '프래그먼트2' 호출
                callFragment(FRAGMENT2);
                break;
            // 유치원 종료 버튼 클릭시 이벤트
            case R.id.exit_drive:
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
                alt_bld.setMessage("유치원에 도착하셨다면 운행을 종료 해주세요").setCancelable(
                        false).setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new mThread4().start();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                alert.setTitle("운행을 종료 하시겠습니까?");
                // Icon for AlertDialog
                alert.show();
                break;
            // 정류장 이동하기 버튼 클릭시 mThread3() 쓰레드 실행
            case R.id.next_station_btn:
                new mThread3().start();
                break;


        }
    }


    //프레크 먼트 호출 탑승 예정 원아 리스트 and 지각 결석 원아 리스트 조회
    private void callFragment(int frament_no) {

        // 프래그먼트 사용을 위해
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (frament_no) {
            case 1:
                // '프래그먼트1' 호출
                Tab_First_Fragment fragment1 = (Tab_First_Fragment) fm.findFragmentByTag("lstfragment1");
                if (fragment1 == null) {
                    fragment1 = new Tab_First_Fragment();
                    transaction.replace(R.id.fragment_container, fragment1, "lstfragment1");
                    Bundle bundle = new Bundle();
                    bundle.putString("route_id", lgs.getRoute_id());
                    bundle.putString("route_type", lgs.getRoute_type());
                    fragment1.setArguments(bundle);
                    transaction.commit();
                }

                break;
            case 2:
                // '프래그먼트2' 호출
                Tab_Second_Fragment fragment2 = (Tab_Second_Fragment) fm.findFragmentByTag("lstfragment2");
                if (fragment2 == null) {
                    fragment2 = new Tab_Second_Fragment();
                    transaction.replace(R.id.fragment_container, fragment2, "lstfragment2");
                    Bundle bundle = new Bundle();
                    bundle.putString("route_id", lgs.getRoute_id());
                    bundle.putString("route_type", lgs.getRoute_type());
                    fragment2.setArguments(bundle);

                    transaction.commit();
                }
                break;
        }
    }


    //하원용 노선일 경우 데이터 통신
    class mThread extends Thread {
        @Override
        public void run() {
            check_thread = true;
            while (check_thread == true) {

                // 딜레이
                try {
                    mThread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    // Connection 설정 완료
                    URL url = new URL(Singleton.Spring_URL+"receive_change_data");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/receive_change_data");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 보내줄 데이터 담기
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(lgs.getRoute_id());

                    // 보내기
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();

                    // 받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream(in);
                        urlConnection.disconnect();

                    }
                    // 에러 발생 시 Toast
                    else {
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //하원용 노선일 경우 데이터 통신
    class mThread_1 extends Thread {
        @Override
        public void run() {


                try {
                    // Connection 설정 완료
                    URL url = new URL(Singleton.Spring_URL+"receive_change_data");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/receive_change_data");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 보내줄 데이터 담기
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(lgs.getRoute_id());

                    // 보내기
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();

                    // 받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream(in);
                        urlConnection.disconnect();

                    }
                    // 에러 발생 시 Toast
                    else {
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }




    public String readData(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream(InputStream in) {
        final String data = readData(in);


        try {
            JSONArray jarray = new JSONArray(data);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonObject = jarray.getJSONObject(i);
                if (jsonObject.getString("station_name").equals("null")) {
                    now_station = "Zero_Point!@#";
                } else {
                    now_station = jsonObject.getString("station_name");
                }
                now_arrive_time = jsonObject.getString("arrive_time");
                now_board_count = jsonObject.getInt("borad_nem");
                fgs[i + 3].setArrive_time(now_arrive_time);
                fgs[i + 3].setBorad_nem(now_board_count);
                total_now_board_count += now_board_count;
                Log.i("receive_change_data_fgs", "초기 받아오는 값 : " + now_station + "  " + now_arrive_time + "  " + now_board_count + " " + total_now_board_count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < fgs.length; x++) {


                    // 전체 정류장과 현재 정류장을 매칭 후 x 값을 구한다.
                    if (fgs[x].getStation_name().equals(now_station)) {
                        Log.i("receive_change_data_fgs", "기존의 정류장과 현재 정류장 매칭 : " + fgs[x].getStation_name() + "  " + fgs[x].getSch_time());
                        Singleton.now_station = now_station.toString();

                        Log.i("receive_change_data_fgs", "현재 정류장 값 : " + now_station + "  " + now_arrive_time + "  " + now_board_count + "" + x + " " + Singleton.now_station);

                        //현재정류장을 찾은 뒤 데이터를 뿌려줌
                        set_change_view(x);
                        break;
                    }
                }
            }
        });
    }


    //승차용 노선일 경우 데이터 통신
    class mThread2 extends Thread {
        public void run() {
            check_thread = true;
            while (check_thread == true) {

                // 딜레이
                try {
                    mThread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    // Connection 설정 완료
                    URL url = new URL(Singleton.Spring_URL+"receive_change_data2");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/receive_change_data2");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 보내줄 데이터 담기
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(lgs.getRoute_id());


                    // 보내기
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();


                    // 받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream2(in);
                        urlConnection.disconnect();

                    }
                    // 에러 발생 시 Toast
                    else {
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    //승차용 노선일 경우 데이터 통신
    class mThread2_1 extends Thread {
        public void run() {


                try {
                    // Connection 설정 완료
                    URL url = new URL(Singleton.Spring_URL+"receive_change_data2");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/receive_change_data2");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 보내줄 데이터 담기
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(lgs.getRoute_id());


                    // 보내기
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();


                    // 받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream2(in);
                        urlConnection.disconnect();

                    }
                    // 에러 발생 시 Toast
                    else {
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }




    public String readData2(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }
    private android.os.Handler handler2 = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream2(InputStream in) {
        final String data = readData2(in);


        try {
            JSONArray jarray = new JSONArray(data);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonObject = jarray.getJSONObject(i);
                now_station = jsonObject.getString("station_name");
                now_arrive_time = jsonObject.getString("arrive_time");
                now_board_count = jsonObject.getInt("borad_nem");
                fgs[i + 3].setArrive_time(now_arrive_time);
                fgs[i + 3].setBorad_nem(now_board_count);
                total_now_board_count += now_board_count;
                Singleton.now_station = jsonObject.getString("station_name");
                Log.i("receive_change_data_fgs", "초기 받아오는 값 : " + now_station + "  " + now_arrive_time + "  " + now_board_count + " " + total_now_board_count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        handler2.post(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < fgs.length; x++) {


                    // 전체 정류장과 현재 정류장을 매칭 후 x 값을 구한다.
                    if (fgs[x].getStation_name().equals(now_station)) {
                        Log.i("receive_change_data_fgs", "기존의 정류장과 현재 정류장 매칭 : " + fgs[x].getStation_name() + "  " + fgs[x].getSch_time());
                        Log.i("receive_change_data_fgs", "현재 정류장 값 : " + now_station + "  " + now_arrive_time + "  " + now_board_count + "" + x);

                        //현재정류장을 찾은 뒤 데이터를 뿌려줌
                        last_count = x;
                        set_change_view(x);
                        break;
                    }
                }
            }
        });
    }


    //주기적으로 노선 위치에 대한 정보를 받아와 운행의 정보를 사용자 인터페이스에 UI를 구성하는 함수
    public void set_change_view(int now_station_count) {
        // 상단의 탑승인원 변화
        if (now_station_count != 0) {
            // 상단의 전체 인원
            board_count.setText(total_now_board_count + "명 / " + board_cnt
                    + "명");
            // 전체 인원 reset
            total_now_board_count = 0;

            // 중단의 정류장 변화
            Log.i("receive_change_data_fgs", now_station_count + " now_station_count 값");
            n_s_1.setText(fgs[now_station_count].getStation_name());
            String n_1_1[] = fgs[now_station_count].getArrive_time().split(":");
            n_a_1.setText(n_1_1[0] + "시" + n_1_1[1] + "분");
            if (fgs[now_station_count].getCnt() == 0) {
                n_b_1.setText("탑승 인원 없음");
            } else {
                n_b_1.setText("탑승인원   " + fgs[now_station_count].getBorad_nem() + "명 / " + fgs[now_station_count].getCnt() + "명");
            }
            n_s_2.setText(fgs[now_station_count + 1].getStation_name());
            arrive_next_station_id = fgs[now_station_count + 1].getStation_id();

            if (fgs[now_station_count + 1].getCnt() == 0) {
                n_b_2.setText("탑승 인원 없음");
                n_a_2.setText("마지막 정류장");
                next_arrive_button.setEnabled(false);
                end_drive_btn.setEnabled(true);
            } else {
                n_b_2.setText("탑승예정  " + fgs[now_station_count + 1].getCnt() + "    명");
                String n_1_2[] = fgs[now_station_count + 1].getSch_time().split(":");
                n_a_2.setText(n_1_2[0] + "시" + n_1_2[1] + "분");
                next_arrive_button.setEnabled(true);
                end_drive_btn.setEnabled(false);
            }

            // 하단의 정류장 변화
            if (now_station_count == 3) {
                s_n_1.setText("-");
                s_n_2.setText(fgs[now_station_count - 1].getStation_name());
                s_n_3.setText(fgs[now_station_count].getStation_name());
                s_n_4.setText(fgs[now_station_count + 1].getStation_name());
                s_n_5.setText(fgs[now_station_count + 2].getStation_name());
                iw2.setImageResource(R.mipmap.start_point_2_x2);
                iw3.setImageResource(R.mipmap.point_3_x2);
                a_t_1.setText("-");
                a_t_2.setText("도착완료\n");
                String sp1[] = fgs[now_station_count].getArrive_time().split(":");
                a_t_3.setText("도착\n" + sp1[0] + "시" + sp1[1] + "분");
                String sp2[] = fgs[now_station_count + 1].getSch_time().split(":");
                a_t_4.setText("도착예정시간\n" + sp2[0] + "시" + sp2[1] + "분");
                String sp3[] = fgs[now_station_count + 2].getSch_time().split(":");
                a_t_5.setText("도착예정시간\n" + sp3[0] + "시" + sp3[1] + "분");
            } else if (now_station_count == 4) {
                s_n_1.setText(fgs[now_station_count - 2].getStation_name());
                s_n_2.setText(fgs[now_station_count - 1].getStation_name());
                s_n_3.setText(fgs[now_station_count].getStation_name());
                s_n_4.setText(fgs[now_station_count + 1].getStation_name());
                s_n_5.setText(fgs[now_station_count + 2].getStation_name());

                iw1.setImageResource(R.mipmap.start_point_2_x2);
                iw2.setImageResource(R.mipmap.point_2_x2);
                iw3.setImageResource(R.mipmap.point_3_x2);

                a_t_1.setText("도착완료\n");
                String sp4[] = fgs[now_station_count - 1].getArrive_time().split(":");
                String sp5[] = fgs[now_station_count].getArrive_time().split(":");
                String sp6[] = fgs[now_station_count + 1].getSch_time().split(":");
                String sp7[] = fgs[now_station_count + 2].getSch_time().split(":");

                a_t_2.setText("도착완료\n" + sp4[0] + "시" + sp4[1] + "분");
                a_t_3.setText("도착\n" + sp5[0] + "시" + sp5[1] + "분");
                a_t_4.setText("도착예정시간\n" + sp6[0] + "시" + sp6[1] + "분");
                a_t_5.setText("도착예정시간\n" + sp7[0] + "시" + sp7[1] + "분");

            } else if (now_station_count > 4 & now_station_count < fgs.length - 4) {
                s_n_1.setText(fgs[now_station_count - 2].getStation_name());
                s_n_2.setText(fgs[now_station_count - 1].getStation_name());
                s_n_3.setText(fgs[now_station_count].getStation_name());
                s_n_4.setText(fgs[now_station_count + 1].getStation_name());
                s_n_5.setText(fgs[now_station_count + 2].getStation_name());

                iw1.setImageResource(R.mipmap.point_2_x2);
                iw2.setImageResource(R.mipmap.point_2_x2);
                iw3.setImageResource(R.mipmap.point_3_x2);

                String sp4[] = fgs[now_station_count - 2].getArrive_time().split(":");
                String sp5[] = fgs[now_station_count - 1].getArrive_time().split(":");
                String sp6[] = fgs[now_station_count].getArrive_time().split(":");
                String sp7[] = fgs[now_station_count + 1].getSch_time().split(":");
                String sp8[] = fgs[now_station_count + 2].getSch_time().split(":");

                a_t_1.setText("도착완료\n" + sp4[0] + "시" + sp4[1] + "분");
                a_t_2.setText("도착완료\n" + sp5[0] + "시" + sp5[1] + "분");
                a_t_3.setText("도착\n" + sp6[0] + "시" + sp6[1] + "분");
                a_t_4.setText("도착예정시간\n" + sp7[0] + "시" + sp7[1] + "분");
                Log.i("정류장마지막입니다", fgs[now_station_count + 2].getStation_id());

                if (fgs[now_station_count + 2].getStation_id().contains("S0_00")) {
                    a_t_5.setText("마지막 정류장");
                    iw5.setImageResource(R.mipmap.start_point_1_x2);
                } else {
                    a_t_5.setText("도착예정시간\n" + sp8[0] + "시" + sp8[1] + "분");
                }
            }
            // 마지막 정류장 처리
            else if (now_station_count == fgs.length - 4) {
                s_n_1.setText(fgs[now_station_count - 2].getStation_name());
                s_n_2.setText(fgs[now_station_count - 1].getStation_name());
                s_n_3.setText(fgs[now_station_count].getStation_name());
                s_n_4.setText(fgs[now_station_count + 1].getStation_name());
                s_n_5.setText("-");

                iw1.setImageResource(R.mipmap.point_2_x2);
                iw2.setImageResource(R.mipmap.point_2_x2);
                iw3.setImageResource(R.mipmap.point_3_x2);
                iw4.setImageResource(R.mipmap.start_point_1_x2);
                iw5.setImageResource(R.mipmap.point_1_x2);

                String sp4[] = fgs[now_station_count - 2].getArrive_time().split(":");
                String sp5[] = fgs[now_station_count - 1].getArrive_time().split(":");
                String sp6[] = fgs[now_station_count].getArrive_time().split(":");
                String sp7[] = fgs[now_station_count + 1].getSch_time().split(":");

                a_t_1.setText("도착완료\n" + sp4[0] + "시" + sp4[1] + "분");
                a_t_2.setText("도착완료\n" + sp5[0] + "시" + sp5[1] + "분");
                a_t_3.setText("도착\n" + sp6[0] + "시" + sp6[1] + "분");
                a_t_4.setText("마지막 정류장");
                a_t_5.setText("-");
            }
            /*마지막 정류장 모두 탑승 완료 시 유치원을 어떻게 할 것인가?*/
            else if (now_station_count < fgs.length - 4) {
            }
        }
    }


    //다음 정류장 이동하기 버튼 클릭 시 데이터 통신
    class mThread3 extends Thread {
        public void run() {

            try {
                // Connection 설정 완료
                URL url = new URL(Singleton.Spring_URL+"click_next_station");
                //URL url = new URL("http://192.168.0.30:8080/tablet/click_next_station");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                // 보내줄 데이터 담기
                StringBuffer buffer = new StringBuffer();
                buffer.append("route_id").append("=").append(lgs.getRoute_id()).append("&");
                buffer.append("station_id").append("=").append(arrive_next_station_id);


                // 보내기
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outputStreamWriter);
                writer.write(buffer.toString());
                writer.flush();


                // 받아온 값 처리
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    readStream3(in);
                    urlConnection.disconnect();

                }
                // 에러 발생 시 Toast
                else {
                    Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String readData3(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }
    public void readStream3(InputStream in) {
        final String data = readData3(in);
        Log.i("정류장지나가기", data);

    }


    //운행 종료 버튼 클릭  시 데이터 통신
    class mThread4 extends Thread {
        public void run() {

            try {
                // Connection 설정 완료
                URL url = new URL(Singleton.Spring_URL+"end_drive");
                //URL url = new URL("http://192.168.0.30:8080/tablet/end_drive");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                // 보내줄 데이터 담기
                StringBuffer buffer = new StringBuffer();
                buffer.append("route_id").append("=").append(lgs.getRoute_id()).append("&");
                buffer.append("affiliation").append("=").append(lgs.getCenter_code());


                // 보내기
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outputStreamWriter);
                writer.write(buffer.toString());
                writer.flush();


                // 받아온 값 처리
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    readStream4(in);
                    urlConnection.disconnect();

                }
                // 에러 발생 시 Toast
                else {
                    Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String readData4(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine();
        s.close();
        return data;
    }
    private android.os.Handler handler4 = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream4(InputStream in) {
        final String data = readData4(in);

        handler4.post(new Runnable() {
            @Override
            public void run() {
                String items[] = {"item1", "item2"};
                Log.i("운행종료됨?", "data = " + data);
                if (data.equals("1")) {
                    next_arrive_button.setEnabled(false);
                    check_thread = false;
                    // 운행 종료가 완료 되었을떄
                    new mThread5().start();

                } else if (data.equals("0")) {
                    // 이미 운행 종료가 되었을때
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(Tablet_MainActivity.this);
                    alt_bld.setMessage("이미 해당 노선의 운행을 종료했습니다.").setCancelable(
                            false).setPositiveButton("네",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    alert.setTitle("운행 종료 실패");
                    alert.show();
                }
            }
        });


    }


    // 운행 종료 후, 일일 안전 점검표를 작성하기 위해, 일일 안전 점검표 리스트를 받아옴
    class mThread5 extends Thread {
        public void run() {

            try {
                // Connection 설정 완료
                URL url = new URL(Singleton.Spring_URL+"receive_bus_check_list_order");
                //URL url = new URL("http://192.168.0.30:8080/tablet/receive_bus_check_list_order");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                // 보내줄 데이터 담기
                StringBuffer buffer = new StringBuffer();
                buffer.append("center_code").append("=").append(lgs.getCenter_code());


                // 보내기
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outputStreamWriter);
                writer.write(buffer.toString());
                writer.flush();


                // 받아온 값 처리
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    readStream5(in);
                    urlConnection.disconnect();

                }
                // 에러 발생 시 Toast
                else {
                    Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String readData5(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine();
        s.close();
        return data;
    }
    private android.os.Handler handler5 = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream5(InputStream in) {
        final String data = readData5(in);
        next_arrive_button.setEnabled(false);
        handler5.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jarray = new JSONArray(data);
                    teach_buslist_cont_order = new int[jarray.length()];
                    teach_buslist_type_order = new int[jarray.length()];
                    teach_buslist_type = new String[jarray.length()];
                    teach_buslist_cont = new String[jarray.length()];
                    check_list_count = new String[jarray.length()];
                    text_checked = new boolean[jarray.length()];

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jsonObject = jarray.getJSONObject(i);
                        teach_buslist_cont_order[i] = jsonObject.getInt("teach_buslist_cont_order");
                        teach_buslist_type_order[i] = jsonObject.getInt("teach_buslist_type_order");
                        teach_buslist_type[i] = jsonObject.getString("teach_buslist_type");
                        teach_buslist_cont[i] = jsonObject.getString("teach_buslist_cont");
                        check_list_count[i] = "1";
                        text_checked[i] = true;
                        Log.i("리스트받아옴?", teach_buslist_cont[i] + " " + text_checked[i]);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(Tablet_MainActivity.this);
                    builder.setTitle("운행 종료 되었습니다. \n일일안전 점검표를 작성해주세요")
                            .setMultiChoiceItems(teach_buslist_cont, text_checked, new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if (isChecked) {
                                        check_list_count[which] = "1";
                                    } else if (isChecked == false) {
                                        check_list_count[which] = "0";
                                    }

                                }
                            })
                            .setPositiveButton("작성 완료", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new mThread6().start();
                                }
                            })
                            .setNegativeButton("나중에 작성", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    Toast.makeText(Tablet_MainActivity.this, "웹에서 점검표 작성이 가능 합니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.create();
                    builder.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //일일 안전점검표 작성 후 데이터를 서버에 저장
    class mThread6 extends Thread {

        public void run() {

            try {
                for (for_check = 0; for_check < teach_buslist_cont.length; for_check++) {
                    // Connection 설정 완료
                    URL url = new URL(Singleton.Spring_URL+"insert_bus_list_check_result");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/insert_bus_list_check_result");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 보내줄 데이터 담기
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("teach_buslist_cont_order").append("=").append(teach_buslist_cont_order[for_check]).append("&");
                    buffer.append("teach_buslist_type_order").append("=").append(teach_buslist_type_order[for_check]).append("&");
                    buffer.append("teach_check").append("=").append(check_list_count[for_check]).append("&");
                    buffer.append("staff_id").append("=").append(lgs.getStaff_id()).append("&");
                    buffer.append("affiliation").append("=").append(lgs.getCenter_code()).append("&");
                    buffer.append("route_id").append("=").append(lgs.getRoute_id()).append("&");

                    // 보내기
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();


                    // 받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream6(in);
                        urlConnection.disconnect();

                    }
                    // 에러 발생 시 Toast
                    else {
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String readData6(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }
    private android.os.Handler handler6 = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream6(InputStream in) {
        final String data = readData6(in);

        handler6.post(new Runnable() {
            @Override
            public void run() {
                if (for_check == teach_buslist_cont.length - 1) {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(Tablet_MainActivity.this);
                    alt_bld.setMessage("작성이 완료 되었습니다.").setCancelable(
                            false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    alert.setTitle("점검표 작성 완료");
                    alert.show();
                }
            }
        });

    }


    // 뒤로가기를 mThread2,mThread의 쓰레드가 정지됨 -> 플레그 값을 변경 시켜줌
    @Override
    public void onBackPressed() {
        //쓰레드 종료 함수
        check_thread = false;
        super.onBackPressed();
    }
}