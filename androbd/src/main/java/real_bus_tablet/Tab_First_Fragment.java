package real_bus_tablet;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tab_First_Fragment extends Fragment {
    int set_count = 0;
    int sleep_time = 3000;
    public ExpandableListView expandableListView;
    public Tab_First_Adatper mCustomExpListViewAdapter;
    public ArrayList<String> parentList;
    static ChildListData list_p = new ChildListData();
    public ArrayList<ChildListData> fruit[];
    public HashMap<String, ArrayList<ChildListData>> childlist;
    String route_id;
    String route_type;
    boolean check_thread = true;
    int width_list;
    int top;

    ArrayList<String> oldparentList;


    public Tab_First_Fragment() {

    }

    // 운행 노선이 등원인지 하원인지를 구분하여 receive_volley2,receive_volley의 스레드를 시작함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Bundle bundle = getArguments();
        route_id = bundle.getString("route_id");
        route_type = bundle.getString("route_type");
        Log.i("OS_FIRST_LOG", list_p.getList_width() + "" + route_type);

        View v = inflater.inflate(R.layout.tab_first, container, false);
        expandableListView = (ExpandableListView) v.findViewById(R.id.expandablelist);

        if (route_type.equals("등원")) {
            Log.i("OS_FIRST_LOG", "등원 들어옴");
            receive_volley2();
            new mThread2().start();
        } else if (route_type.equals("하원")) {
            receive_volley1();
            new mThread().start();
        }

        return v;
    }


    //운행 노선이 하원 노선일 경우 원아들의 하차 예정 리스트를 받아옴
    public void receive_volley1() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"receive_first_kid_data",
                //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/receive_first_kid_data",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("OS_Fi" +
                                "rst_Tab", "first_view_data = " + response.toString());
                        try {

                            JSONArray jarray = new JSONArray(response);
                            parentList = new ArrayList<String>();
                            oldparentList = new ArrayList<String>();
                            ChildListData apple[] = new ChildListData[jarray.length()];
                            childlist = new HashMap<String, ArrayList<ChildListData>>();

                            //정류장을 받음
                            for (int z = 0; z < jarray.length(); z++) {
                                JSONObject jsonObject = jarray.getJSONObject(z);
                                oldparentList.add(jsonObject.getString("station_name"));
                            }

                            //정류장의 중복 값을 제거함
                            for (int i = 0; i < oldparentList.size(); i++) {
                                if (!parentList.contains(oldparentList.get(i))) {
                                    parentList.add(oldparentList.get(i));
                                }
                            }


                            //Expandable ListView 구성
                            fruit = new ArrayList[parentList.size()];

                            for (int a = 0; a < parentList.size(); a++) {
                                fruit[a] = new ArrayList<ChildListData>();

                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    if (parentList.get(a).equals(jsonObject.getString("station_name"))) {
                                        if (jsonObject.getString("off_time").equals("1999-01-01 00:00:00.0") || jsonObject.getString("off_time").equals("null")) {
                                            if (jsonObject.getString("kid_pho").equals("null")) {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 예정", " - ", "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                            } else {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 예정", " - ", jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                            }

                                        } else {
                                            String onoff_time_1[] = jsonObject.getString("off_time").split(" ");
                                            String onoff_time_2[] = onoff_time_1[1].split(":");
                                            String onof_time_3 = onoff_time_2[0] + "시" + onoff_time_2[1] + "분";
                                            if (jsonObject.getString("kid_pho").equals("null")) {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 완료", onof_time_3, "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                            } else {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 완료", onof_time_3, jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                            }
                                        }
                                        fruit[a].add(apple[k]);
                                    }
                                }
                                Log.i("OS+fruit_size", fruit.length + "");
                            }

                            // Expandable Listview 데이터를 받아옴
                            for (int m = 0; m < parentList.size(); m++) {
                                childlist.put(parentList.get(m), fruit[m]);
                                Log.i("OS_get", fruit[m].get(0).getKid_n() + "");
                            }

                            // Listview에 데이터를 뿌림
                            mCustomExpListViewAdapter = new Tab_First_Adatper(getActivity(), parentList, childlist);
                            expandableListView.setAdapter(mCustomExpListViewAdapter);

                            //자식 List를 항상 열어둠
                            for (int i = 0; i < mCustomExpListViewAdapter.getGroupCount(); i++) {
                                expandableListView.expandGroup(i);
                            }

                            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                @Override
                                public void onGroupExpand(int groupPosition) {

                                }
                            });
                            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                    // Doing nothing

                                    return true;
                                }
                            });
                            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                                @Override
                                public void onGroupCollapse(int groupPosition) {

                                }
                            });

                            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                    Log.i("선택합니다", "선택");
                                    return false;
                                }
                            });

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
                params.put("route_id", route_id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //운행 노선이 등원 노선일 경우 원아들의 탑승 예정 리스트를 받아옴
    public void receive_volley2() {
        Log.i("OS_FIRST_LOG", "등원 volley2 들어옴");
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"receive_first_kid_data2",
                //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/receive_first_kid_data2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("OS_Fi" +
                                "rst_Tab", "first_view_data = " + response.toString());
                        try {
                            JSONArray jarray = new JSONArray(response);
                            parentList = new ArrayList<String>();
                            ArrayList<String> oldparentList = new ArrayList<String>();
                            ChildListData apple[] = new ChildListData[jarray.length()];
                            childlist = new HashMap<String, ArrayList<ChildListData>>();
                            //정류장을
                            for (int z = 0; z < jarray.length(); z++) {
                                JSONObject jsonObject = jarray.getJSONObject(z);
                                oldparentList.add(jsonObject.getString("station_name"));
                            }
                            //정류장의 중복 값을 제거함
                            for (int i = 0; i < oldparentList.size(); i++) {
                                if (!parentList.contains(oldparentList.get(i))) {
                                    parentList.add(oldparentList.get(i));
                                }
                            }
                            fruit = new ArrayList[parentList.size()];

                            for (int a = 0; a < parentList.size(); a++) {
                                fruit[a] = new ArrayList<ChildListData>();

                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    if (parentList.get(a).equals(jsonObject.getString("station_name"))) {
                                        if (jsonObject.getString("on_time").equals("1999-01-01 00:00:00.0") || jsonObject.getString("on_time").equals("null")) {

                                            if (jsonObject.getString("kid_pho").equals("null")) {
                                                if (jsonObject.getString("late_abse_type").equals("null")) {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "No_LATE");
                                                } else {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                                }
                                            } else {
                                                if (jsonObject.getString("late_abse_type").equals("null")) {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "NO_LATE");
                                                } else {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                                }
                                            }


                                        } else {
                                            String onoff_time_1[] = jsonObject.getString("on_time").split(" ");
                                            String onoff_time_2[] = onoff_time_1[1].split(":");
                                            String onof_time_3 = onoff_time_2[0] + "시" + onoff_time_2[1] + "분";
                                            if (jsonObject.getString("kid_pho").equals("null")) {
                                                if (jsonObject.getString("late_abse_type").equals("null")) {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onof_time_3, "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "NO_LATE");
                                                } else {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onof_time_3, "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                                }
                                            } else {
                                                if (jsonObject.getString("late_abse_type").equals("null")) {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onof_time_3, jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "NO_LATE");
                                                } else {
                                                    apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onof_time_3, jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                                }

                                            }

                                        }
                                        fruit[a].add(apple[k]);
                                    }
                                }
                                Log.i("OS+fruit_size", fruit.length + "");
                            }

                            for (int m = 0; m < parentList.size(); m++) {
                                childlist.put(parentList.get(m), fruit[m]);
                                Log.i("OS_get", fruit[m].get(0).getKid_n() + "");
                            }

                            mCustomExpListViewAdapter = new Tab_First_Adatper(getActivity(), parentList, childlist);
                            expandableListView.setAdapter(mCustomExpListViewAdapter);
                            for (int i = 0; i < mCustomExpListViewAdapter.getGroupCount(); i++) {
                                expandableListView.expandGroup(i);
                            }

                            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                @Override
                                public void onGroupExpand(int groupPosition) {

                                }
                            });
                            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                    // Doing nothing
                                    return true;
                                }
                            });
                            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                                @Override
                                public void onGroupCollapse(int groupPosition) {

                                }
                            });

                            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                    Log.i("제발되라", "제발되라");
                                    return false;
                                }
                            });

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
                params.put("route_id", route_id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //운행 노선이 하원 노선일 경우 receive_volley1()이 실행 후 주기적으로 운행 관련 정보를 받아오기 위해 mThtread가 실행됨
    class mThread extends Thread {

        public void run() {
            check_thread = true;
            while (check_thread == true) {
                try {
                    mThread.sleep(sleep_time);
                    width_list = expandableListView.getFirstVisiblePosition();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    URL url = new URL(Singleton.Spring_URL+"receive_change_kid");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/receive_change_kid");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(route_id);
                    Log.i("set_change_kid", route_id);

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();


                    //받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream(in);
                        urlConnection.disconnect();
                    } else {
                        Toast.makeText(getContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String readData(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine();
        Log.i("set_change_kid", "data = " + data);
        s.close();
        return data;
    }
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream(InputStream in) {

        final String data = readData(in);

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (!data.equals("")) {
                    try {
                        JSONArray jarray = new JSONArray(data);
                        //parentList = new ArrayList<String>();
                        ArrayList<String> oldparentList = new ArrayList<String>();
                        ChildListData apple[] = new ChildListData[jarray.length()];
                        //childlist = new HashMap<String, ArrayList<ChildListData>>();
                        parentList.clear();
                        childlist.clear();


                        //정류장을 받음
                        for (int z = 0; z < jarray.length(); z++) {
                            JSONObject jsonObject = jarray.getJSONObject(z);
                            oldparentList.add(jsonObject.getString("station_name"));
                        }

                        //정류장의 중복 값을 제거함
                        for (int i = 0; i < oldparentList.size(); i++) {
                            if (!parentList.contains(oldparentList.get(i))) {
                                parentList.add(oldparentList.get(i));
                            }
                        }

                        fruit = new ArrayList[parentList.size()];

                        for (int a = 0; a < parentList.size(); a++) {
                            fruit[a] = new ArrayList<ChildListData>();

                            for (int k = 0; k < jarray.length(); k++) {
                                JSONObject jsonObject = jarray.getJSONObject(k);

                                if (parentList.get(a).equals(jsonObject.getString("station_name"))) {
                                    if (jsonObject.getString("off_time").equals("1999-01-01 00:00:00.0") || jsonObject.getString("off_time").equals("null")) {
                                        if (jsonObject.getString("kid_pho").equals("null")) {
                                            apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 예정", " - ", "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                        } else {
                                            apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 예정", " - ", jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                        }

                                    } else {
                                        String onoff_time_1[] = jsonObject.getString("off_time").split(" ");
                                        String onoff_time_2[] = onoff_time_1[1].split(":");
                                        String onoof_time_3 = onoff_time_2[0] + "시" + onoff_time_2[1] + "분";
                                        if (jsonObject.getString("kid_pho").equals("null")) {
                                            apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 완료", onoof_time_3, "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                        } else {
                                            apple[k] = new ChildListData(jsonObject.getString("kid_name"), "하차 완료", onoof_time_3, jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("kid_system_code"));
                                        }
                                    }
                                    fruit[a].add(apple[k]);
                                }
                            }
                        }
                        for (int m = 0; m < parentList.size(); m++) {
                            childlist.put(parentList.get(m), fruit[m]);
                        }
                        //clear를 하면 전체 데이터가 삭제가됨.. 해당 값만 어떻게 바꿀수 있어야된다.
                        mCustomExpListViewAdapter.notifyDataSetChanged();
                        //expandableListView.setSelection(width_list);

                        for (int i = 0; i < mCustomExpListViewAdapter.getGroupCount(); i++) {
                            expandableListView.expandGroup(i);
                        }

                        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            @Override
                            public void onGroupExpand(int groupPosition) {

                            }
                        });
                        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                return true;
                            }
                        });
                        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                            @Override
                            public void onGroupCollapse(int groupPosition) {

                            }
                        });
                        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                Log.i("선택합니다", "선택");
                                return false;
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    //운행 노선이 등원 노선일 경우 receive_volley2()이 실행 후 주기적으로 운행 관련 정보를 받아오기 위해 mThread2가 실행됨
    class mThread2 extends Thread {
        public void run() {
            check_thread = true;
            while (check_thread == true) {
                try {
                    mThread.sleep(sleep_time);
                    width_list = expandableListView.getFirstVisiblePosition();
                    View v = expandableListView.getChildAt(0);
                    top = (v == null) ? 0 : (v.getTop() - expandableListView.getPaddingTop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    URL url = new URL(Singleton.Spring_URL+"receive_change_kid2");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/receive_change_kid2");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(route_id);
                    Log.i("set_change_kid", route_id);

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outputStreamWriter);
                    writer.write(buffer.toString());
                    writer.flush();


                    //받아온 값 처리
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        readStream2(in);
                        urlConnection.disconnect();
                    } else {
                        Toast.makeText(getContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public String readData2(InputStream is) {
        String data = "";
        Scanner s = new Scanner(is);
        while (s.hasNext()) data += s.nextLine();
        Log.i("set_change_kid", "data = " + data);
        s.close();
        return data;
    }
    private android.os.Handler handler2 = new android.os.Handler() {
        public void handleMessage(Message success) {

        }
    };
    public void readStream2(InputStream in) {
        final String data = readData2(in);
        handler2.post(new Runnable() {
            @Override
            public void run() {

                if (!data.equals("")) {
                    try {

                        JSONArray jarray = new JSONArray(data);
                        //parentList = new ArrayList<String>();
                        ArrayList<String> oldparentList = new ArrayList<String>();
                        ChildListData apple[] = new ChildListData[jarray.length()];
                        //childlist = new HashMap<String, ArrayList<ChildListData>>();
                        //expandableListView.setSelectionFromTop(width_list,top);
                        parentList.clear();
                        childlist.clear();

                        //정류장을 받음
                        for (int z = 0; z < jarray.length(); z++) {
                            JSONObject jsonObject = jarray.getJSONObject(z);
                            oldparentList.add(jsonObject.getString("station_name"));

                        }

                        //정류장의 중복 값을 제거함
                        for (int i = 0; i < oldparentList.size(); i++) {
                            if (!parentList.contains(oldparentList.get(i))) {
                                parentList.add(oldparentList.get(i));
                            }
                        }

                        fruit = new ArrayList[parentList.size()];
                        for (int a = 0; a < parentList.size(); a++) {
                            fruit[a] = new ArrayList<ChildListData>();

                            for (int k = 0; k < jarray.length(); k++) {
                                JSONObject jsonObject = jarray.getJSONObject(k);
                                Log.i("이미지어베이스", jsonObject.getString("late_abse_type") + " " + jsonObject.getString("late_abse_cont") + " " + jsonObject.getString("station_name"));

                                if (parentList.get(a).equals(jsonObject.getString("station_name"))) {
                                    if (jsonObject.getString("on_time").equals("1999-01-01 00:00:00.0") || jsonObject.getString("on_time").equals("null")) {

                                        if (jsonObject.getString("kid_pho").equals("null")) {
                                            if (jsonObject.getString("late_abse_type").equals("null")) {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "No_LATE");
                                            } else {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                            }
                                        } else {
                                            if (jsonObject.getString("late_abse_type").equals("null")) {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "NO_LATE");
                                            } else {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 예정", " - ", jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                            }
                                        }

                                    } else {
                                        String onoff_time_1[] = jsonObject.getString("on_time").split(" ");
                                        String onoff_time_2[] = onoff_time_1[1].split(":");
                                        String onoof_time_3 = onoff_time_2[0] + "시" + onoff_time_2[1] + "분";
                                        if (jsonObject.getString("kid_pho").equals("null")) {
                                            if (jsonObject.getString("late_abse_type").equals("null")) {

                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onoof_time_3, "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "NO_LATE");
                                            } else {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onoof_time_3, "null", jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                            }
                                        } else {

                                            if (jsonObject.getString("late_abse_type").equals("null")) {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onoof_time_3, jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), "NO_LATE");
                                            } else {
                                                apple[k] = new ChildListData(jsonObject.getString("kid_name"), "탑승 완료", onoof_time_3, jsonObject.getString("kid_pho"), jsonObject.getString("route_id"), jsonObject.getString("station_id"), jsonObject.getString("beacon_id"), jsonObject.getString("late_abse_type"));
                                            }

                                        }

                                    }
                                    fruit[a].add(apple[k]);
                                    Log.i("푸르츠 값 1", fruit[a].get(0).getKid_n() + "");

                                }
                            }
                        }
                        for (int m = 0; m < parentList.size(); m++) {
                            childlist.put(parentList.get(m), fruit[m]);
                        }
                        mCustomExpListViewAdapter.notifyDataSetChanged();


                        for (int i = 0; i < mCustomExpListViewAdapter.getGroupCount(); i++) {
                            expandableListView.expandGroup(i);
                        }

                        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            @Override
                            public void onGroupExpand(int groupPosition) {

                            }
                        });
                        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                return true;
                            }
                        });
                        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                            @Override
                            public void onGroupCollapse(int groupPosition) {

                            }
                        });
                        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                Log.i("선택합니다", "선택");
                                Log.i(" 션", parentList.get(groupPosition).toString() + " " + fruit[groupPosition].get(childPosition).getKid_n()
                                        + " " + fruit[groupPosition].get(childPosition).getOnoff_time() + " " + Singleton.now_station + " 123");
                                return false;
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    // 뒤로가기 혹은 화면 전환 시, 쓰레드의 호출을 종료 함
    public void onStop() {
        super.onStop();
        check_thread = false;
    }

    @Override
    public void onPause() {
        Log.d("TAG", "saving listview state @ onPause");
        list_p.setList_width(expandableListView.onSaveInstanceState());
        Log.d("TAG", "saving" + list_p.getList_width());
        super.onPause();
    }

    @Override
    public void onResume() {
        if (list_p.getList_width() != null) {
            Log.d("TAG", "trying to restore listview state..1");
            expandableListView.onRestoreInstanceState(list_p.getList_width());
        }
        super.onResume();
    }

}