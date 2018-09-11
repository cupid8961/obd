package real_bus_tablet;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KST03 on 2017-11-02.
 *
 */

public class Tab_Second_Fragment extends ListFragment {
    String route_id;
    int check = 0;
    Tab_Second_Adapter adapter = new Tab_Second_Adapter();
    Tablet_First_View_Get_Set fgs[] ;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    public Tab_Second_Fragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        route_id = bundle.getString("route_id");
        beacon_handler.sendEmptyMessage(0);
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.tab_second, container, false);
    }


    public void onListItemClick(ListView l, View view, int position, long id){

        ViewGroup viewGroup = (ViewGroup)view;

        TextView txt = (TextView)viewGroup.findViewById(R.id.textitem);
        TextView txt2 = (TextView)viewGroup.findViewById(R.id.textitem1);

    }
    public android.os.Handler beacon_handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            volley_set_info();
            beacon_handler.sendEmptyMessageDelayed(0, 5000);

        }
    };
    // 원아의 지각 결석 정보를 받아옴, 그후 adapter로 넘김
    public void volley_set_info(){
        requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest = new StringRequest(Request.Method.POST, Singleton.Spring_URL+"receive_late_kid",
                //stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.30:8080/tablet/receive_late_kid",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("OS", "response = " + response.toString());
                        //if (!response.equals("[]")) {
                            try {
                                adapter.clear_list_item();

                                JSONArray jarray = new JSONArray(response);
                                fgs = new Tablet_First_View_Get_Set[jarray.length()];

                                for (int i = 0; i < jarray.length(); i++) {

                                    JSONObject jsonObject = jarray.getJSONObject(i);

                                    fgs[i] = new Tablet_First_View_Get_Set();
                                    fgs[i].setPar_id(jsonObject.getString("par_id"));
                                    fgs[i].setPar_name(jsonObject.getString("par_name"));
                                    fgs[i].setKid_name(jsonObject.getString("kid_name"));
                                    fgs[i].setLate_abse_cont(jsonObject.getString("late_abse_cont"));
                                    fgs[i].setLate_abse_type(jsonObject.getString("late_abse_type"));
                                    fgs[i].setStation_id(jsonObject.getString("station_id"));
                                    fgs[i].setStation_name(jsonObject.getString("station_name"));
                                    fgs[i].setCreatetime(jsonObject.getString("createtime"));
                                    fgs[i].setKid_pho_url(jsonObject.getString("kid_pho"));
                                    String create_time = fgs[i].getCreatetime().replace("\n","");
                                    String create_time2 = create_time.replace("\t","");
                                    adapter.addItem(fgs[i].getKid_name(), fgs[i].getStation_name(), fgs[i].getLate_abse_type() + " : " + create_time2, fgs[i].getKid_pho_url(), "\"" + fgs[i].getLate_abse_cont() + "\"");

                                    Log.i("원아_지각_데이터", "원아_지각_데이터" + fgs[0].getPar_name() + fgs[0].getCreatetime());
                                }
                                if(check == 0) {
                                    setListAdapter(adapter);
                                    check = 1;

                                }
                                else if(check == 1){
                                    adapter.notifyDataSetChanged();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        //}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("OS", error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("route_id",route_id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void onStop(){
        super.onStop();
        beacon_handler.removeMessages(0);
    }
}