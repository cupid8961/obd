package real_bus_tablet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tablet_Drive_Start_Activity extends Activity implements AdapterView.OnItemSelectedListener {
    private Button s_btn;
    private Spinner route_spinner;
    Login_Get_Set lgs;

    private String route_id[];
    private String route_name[];
    private String course_id[];
    private String bus_id[];
    private String route_type[];
    private String route_title[];



    //새로 추가된 이력초기화 버튼
    Button clear_btn;

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet__drive__start_);
        init();
        receive_route_info_volley();




    }



    //초기 UI 선언
    public void init() {
        s_btn = (Button) findViewById(R.id.drive_s_btn);
        route_spinner = (Spinner) findViewById(R.id.route_spinner);
        route_spinner.setOnItemSelectedListener(this);
        route_spinner.setPrompt("운행 노선 선택");

       //새로추가된 이력초기화 버튼
        clear_btn = (Button)findViewById(R.id.clear_btn);

        Intent intent = getIntent();
        lgs = (Login_Get_Set) intent.getSerializableExtra("OBJECT");
    }


    //이력초기화 버튼 클릭시 발생하는 이벤트
    public void Clear_btn(View view){
        Toast.makeText(this, "이력 초기화", Toast.LENGTH_LONG).show();


        //쓰래드시작
        new mThread().start();

    }



    /*노선을 선택한 뒤 사용자가 운행 시작 버튼을 눌렀을때 호출되는 onclick 이벤트*/
    public void start_btn(View view) {
        Log.i("OS", "아이템선택" + route_spinner.getAdapter().getItemId(0));
        s_btn.setEnabled(false);
        //사용자가 운행 하는 노선에 대한 정보를 Login_Get_Set Class의 변수로 저장
        for (int i = 0; i < route_id.length; i++) {
            if (i == route_spinner.getSelectedItemPosition()) {
                lgs.setRoute_id(route_id[i]);
                lgs.setRoute_name(route_name[i]);
                lgs.setBus_id(bus_id[i]);
                lgs.setCourse_id(course_id[i]);
                lgs.setRoute_type(route_type[i]);
            }
        }


        insert_real_bus_info();

        Log.i("OS", lgs.getRoute_id() + "\n" + lgs.getRoute_name() + "\n" + lgs.getBus_id() + "\n" + lgs.getCourse_id() +
                "\n" + lgs.getStaff_id() + "\n" + lgs.getStaff_name() + "\n" + lgs.getStaff_charge() + "\n" + lgs.getStaff_type() + "\n" + lgs.getStaff_pw() +
                "\n" + lgs.getAdmin_area() + "\n" + lgs.getCenter_code());

        new mThread2().start();
    }
    // 초기 리스트에 뿌려질 노선의 정보들을 받아옴
    private void receive_route_info_volley() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"receive_route_info", new Response.Listener<String>() {
            //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/receive_route_info", new Response.Listener<String>() {
            //통신 성공 시
            @Override
            public void onResponse(String response) {
                Log.i("OS", "response = " + response.toString());
                try {
                    JSONArray jarray = new JSONArray(response);
                    route_id = new String[jarray.length()];
                    route_name = new String[jarray.length()];
                    course_id = new String[jarray.length()];
                    bus_id = new String[jarray.length()];
                    route_type = new String[jarray.length()];
                    route_title = new String[jarray.length()];

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jsonObject = jarray.getJSONObject(i);
                        /*데이터 받는 부분*/
                        route_id[i] = jsonObject.getString("route_id").toString();
                        route_name[i] = jsonObject.getString("route_name").toString();
                        course_id[i] = jsonObject.getString("course_id").toString();
                        bus_id[i] = jsonObject.getString("bus_id").toString();
                        route_type[i] = jsonObject.getString("route_type").toString();
                        route_title[i] = route_name[i] + "(" + route_type[i] + ")";
                        Log.i("OS", i + "번째 열 : " + route_id[i] + " " + route_name[i]);
                    }
                    //스피너 구성
                    adapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_time, route_title);
                    route_spinner.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        //통신 실패 시
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("OS", error.toString());
            }
        }) {
            //파라미터 정의
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("staff_id", lgs.getStaff_id().toString());
                return params;
            }
        };
        //통신 요청
        requestQueue.add(stringRequest);
    }

    //운행 시작 시 RDS의 Real_time 테이블에 노선의 수 만큼 칼럼이 insert가 됨, 이는 원아가 탑승을 하였을 때 차량 운행 위치 정보를 주기적으로 알기 위해 하루 기준으로 최소 운행 시작 시, DB의 Real_time 테이블에 데이터가 생성됨
    private void insert_real_bus_info() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"receive_real_bus_info", new Response.Listener<String>() {
            //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/receive_real_bus_info", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("받아온_데이터_값_초기초기", "response = " + response.toString());
                if(response.isEmpty()){
                    Log.i("받아온_데이터_값_초기초기1", "이미 운행을 시작함");
                }else{
                    Log.i("받아온_데이터_값_초기초기1", "운행 시작 완료");
                    //Thread를 사용해서 원아들에 대한 부모들의 Tocken 값을 받아옴
                    //new mThread5().start();
                }

                Intent intent = new Intent(getApplicationContext(), Tablet_MainActivity.class);
                intent.putExtra("OBJECT", lgs);
                startActivity(intent);
                s_btn.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("OS", error.toString());

                Intent intent = new Intent(getApplicationContext(), Tablet_MainActivity.class);
                intent.putExtra("OBJECT", lgs);
                startActivity(intent);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("route_id", lgs.getRoute_id());
                params.put("course_id", lgs.getCourse_id());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    /*스피너 오버라이드*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("OS", "아이템선택" + route_spinner.getAdapter().getItem(route_spinner.getSelectedItemPosition()).toString());
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    // 사용자 토큰 저장 관련 소스로, 그때 추후 개발 사항으로 도출 되었음(미완성) -> 운 행 시작 알림 관련 미완성 소스 추 후 추가 필요
    class mThread5 extends Thread {
        public void run() {

            try {
                // Connection 설정 완료
                URL url = new URL(Singleton.Spring_URL+"send_fcm_list_drive_start");
                //URL url = new URL("http://192.168.0.30:8080/tablet/send_fcm_list_drive_start");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                // 보내줄 데이터 담기
                StringBuffer buffer = new StringBuffer();
                buffer.append("route_id").append("=").append(lgs.getRoute_id()).append("&");
                buffer.append("route_type").append("=").append(URLEncoder.encode(lgs.getRoute_type(),"UTF-8"));


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
        handler5.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }









    class mThread extends Thread {
        @Override
        public void run() {

            try {
                //    try {
                Log.i("OS", "123");
                StringBuffer sb = new StringBuffer();
                Log.d("서버로 전달될값", sb.toString());
                Log.i("OS", "URL 수신확인");
               // java.net.URL url = new URL("http://192.168.0.40:8080/Smartbus_Tablet/clear_route"); //URL클래스의 생성자로 주소넘김
                java.net.URL url = new URL(Singleton.Spring_URL+"clear_route"); //URL클래스의 생성자로 주소넘김
                Log.i("OS", "전송중1");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //해당 페이지접속
                httpURLConnection.setRequestMethod("POST"); //데이터 전송방식
                httpURLConnection.setDoOutput(true); //InputStream으로 서버로부터 응답헤더와 메시지를 읽어들이겠다는 옵션
                httpURLConnection.setDoInput(true); //OutputStream으로 POST데이터를 넘겨주겠다는 옵션
                httpURLConnection.setDefaultUseCaches(false);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                Log.i("OS", "전송중2");
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
                pw.write(sb.toString());
                pw.flush(); //스트림 버퍼를 비워줌
                Log.i("OS", "전송완료");
                //pw.close(); //스트림을 닫음
                Log.d("보내기", sb.toString());


                int responseCode = httpURLConnection.getResponseCode();

                Log.d("값", "읽기");

                //서버에서 오는 데이터값 읽기 (투약의뢰서 작성 값)
                BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = bf.readLine()) != null) {
                    response.append(line);
                    Log.d("값", response.toString());


                    String json = response.toString();

                    try {

                        //  Log.d(json.toString(), "반환된값 저장");
                        JSONArray jsonArray = new JSONArray(json);
                        Log.d("jsonArray",jsonArray.toString());

                        //리스트별 갯수만큼



                    }catch (JSONException e){
                        Log.e("jsonErr", "제이슨 에러", e);
                    }catch (Exception e){
                        Log.e("exception", "파일이없다.",e);
                    }
                }
                bf.close();

                Message msg = haldler.obtainMessage();
                haldler.sendMessage(msg);


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("MalformedURLException", "정확함");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IOException e", "입력값 없음");

            }

            // JSONObject sendObject = new JSONObject();
            // JSONArray sendArray = new JSONArray();


        }
    }

    private android.os.Handler haldler = new android.os.Handler() {
        public void handleMessage(Message success) {

            Toast.makeText(Tablet_Drive_Start_Activity.this, "이력 초기화", Toast.LENGTH_LONG).show();


        }
    };




    class mThread2 extends Thread {
        @Override
        public void run() {

            try {
                //    try {
                Log.i("OS", "123");
                //String postData = "name=" + name + "&phone=" + phone + "&id=" + id + "&password=" + password + "&email=" + email;

                // JSONObject informationObject = new JSONObject();
                StringBuffer sb = new StringBuffer();

                sb.append("route_id").append("=").append(lgs.getRoute_id()).append("&");
                sb.append("route_type").append("=").append(lgs.getRoute_type());

                //  informationObject.put("name", name);
                //  informationObject.put("phone", phone);
                //  informationObject.put("id", id);
                // informationObject.put("password", password);
                // informationObject.put("email", email);

                // sendArray.put(informationObject);
                // sendObject.put("list",sendArray);

                //  sb.append(informationObject.toString());

                Log.d("서버로 전달될값", sb.toString());
                Log.i("OS", "URL 수신확인");
                java.net.URL url = new URL(Singleton.Spring_URL+"insert_route"); //URL클래스의 생성자로 주소넘김
                Log.i("OS", "전송중1");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //해당 페이지접속
                httpURLConnection.setRequestMethod("POST"); //데이터 전송방식
                httpURLConnection.setDoOutput(true); //InputStream으로 서버로부터 응답헤더와 메시지를 읽어들이겠다는 옵션
                httpURLConnection.setDoInput(true); //OutputStream으로 POST데이터를 넘겨주겠다는 옵션
                httpURLConnection.setDefaultUseCaches(false);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                Log.i("OS", "전송중2");
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
                pw.write(sb.toString());
                pw.flush(); //스트림 버퍼를 비워줌
                Log.i("OS", "전송완료");
                //pw.close(); //스트림을 닫음
                Log.d("보내기", sb.toString());


                int responseCode = httpURLConnection.getResponseCode();

                Log.d("값", "읽기");

                //서버에서 오는 데이터값 읽기 (투약의뢰서 작성 값)
                BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = bf.readLine()) != null) {
                    response.append(line);
                    Log.d("값", response.toString());

                    //  try{
                    //    JSONObject jsonObject = new JSONObject("medicineList");
                    //    JSONArray jsonArray = jsonObject.getJSONArray("medicineList");
                    //    ArrayList<String> medicine_list = new ArrayList<>();
                    //    medicine_list.add(response.toString());


                    //        Log.d(medicine_list.toString(), "어레이 리스트 값");
                    String json = response.toString();

                    try {

                        //  Log.d(json.toString(), "반환된값 저장");
                        JSONArray jsonArray = new JSONArray(json);
                        Log.d("jsonArray",jsonArray.toString());
                    }catch (JSONException e){
                        Log.e("jsonErr", "제이슨 에러", e);
                    }catch (Exception e){
                        Log.e("exception", "파일이없다.",e);
                    }
                }
                bf.close();


//                Message msg = haldler_additem.obtainMessage();
//                haldler_additem.sendMessage(msg);



            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("MalformedURLException", "정확함");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IOException e", "입력값 없음");

            }




        }
    }





}