package real_bus_tablet;
/*
*
* 보육교사 로그인
*
* */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fr3ts0n.ecu.gui.androbd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.NULL;

public class Tablet_LoginActivity extends Activity {
    private EditText login_edit;
    private EditText password_edit;
    private Button login_btn;
    private InputMethodManager imm;


    // 로그인 관련 필요 변수 선언 class 선언
    Login_Get_Set lgs = new Login_Get_Set();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet__login);
        init();
    }

    // 초기 UI 선언
    public void init() {
        login_edit =(EditText)findViewById(R.id.login_edit);                // ID 입력 창
        password_edit = (EditText) findViewById(R.id.password_edit);        // PW 입력 창
        login_btn = (Button) findViewById(R.id.login_btn);                  // 로그인 버튼
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);      //keyboard 제어어

    }

    //사용자 입력 정보 초기화
    public void get_set_new(){
        lgs.setStaff_id("");
        lgs.setStaff_pw("");
        lgs.setStaff_name("");
        lgs.setStaff_charge("");
        lgs.setStaff_type("");
        lgs.setAdmin_area(NULL);
        lgs.setCenter_code("");
    }


    /* Volley 사용
    * 아이디 비밀번호 입력 후 로그인 완료 시 */
    public void Connect_Login(View view) {

        if(login_edit.getText().toString().equals("") || password_edit.getText().toString().equals("")){
            //아이디 및 비밀번호 없음
            Toast.makeText(Tablet_LoginActivity.this,"미입력", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("check_login","1");
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Log.i("check_login","2");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"login_check", new Response.Listener<String>() {
                //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/login_check", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("OS", response.toString() + "");
                    if (!response.toString().equals("[]")) {
                        try {

                            JSONArray jarray = new JSONArray(response);

                            for (int i = 0; i < 1; i++) {
                                JSONObject jsonObject = jarray.getJSONObject(i);

                                lgs.setStaff_id(jsonObject.getString("staff_id").toString());
                                lgs.setStaff_pw(jsonObject.getString("staff_pw"));
                                lgs.setStaff_name(jsonObject.getString("staff_name"));
                                lgs.setStaff_charge(jsonObject.getString("staff_charge"));
                                lgs.setStaff_type(jsonObject.getString("staff_type"));
                                lgs.setAdmin_area(jsonObject.getInt("admin_area"));
                                lgs.setCenter_code(jsonObject.getString("center_code"));
                                lgs.setCenter_name(jsonObject.getString("center_name"));
                                Log.i("OS", i + "번째 열 : " + lgs.getStaff_name() + " " + lgs.getStaff_pw() + " " + lgs.getStaff_charge()+" "+lgs.getStaff_id());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(), Tablet_Drive_Start_Activity.class);
                        intent.putExtra("OBJECT",lgs);
                        startActivity(intent);
                        //finish();

                    } else{
                        get_set_new();
                        Toast.makeText(Tablet_LoginActivity.this, "아이디 비밀번호 불일치", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("OS",error.getMessage()+"");

                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userid", login_edit.getText().toString());
                    params.put("password", password_edit.getText().toString());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            requestQueue.add(stringRequest);
            Log.i("check_login","3");

        }


    }

    // EditText에서 화면 클릭시 키보드 내림
    public void linearOnClick(View view) {
        imm.hideSoftInputFromWindow(login_edit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password_edit.getWindowToken(), 0);
    }
}