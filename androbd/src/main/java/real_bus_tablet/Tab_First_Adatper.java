package real_bus_tablet;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fr3ts0n.ecu.gui.androbd.R;

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
import java.util.Scanner;

/**
 * Created by KST03 on 2017-11-03.
 *
 */
public class Tab_First_Adatper extends BaseExpandableListAdapter {

    private String route_id,station_id,beacon_id;
    private String[] route_id_s,station_id_s,kid_system_code_s;

    private Context mContext;
    private ArrayList<String> mParentList;
    private ArrayList<ChildListData> mChildList;
    private ChildListViewHolder mChildListViewHolder;
    private HashMap<String,ArrayList<ChildListData>> mChildHashMap;

    public Tab_First_Adatper(Context context, ArrayList<String> parentList, HashMap<String,ArrayList<ChildListData>> childHashMap){
        this.mContext = context;
        this.mParentList = parentList;
        this.mChildHashMap = childHashMap;
    }


    /*BaseExpandableListAdapter를 extends 할 경우 생성되는 오버라이드, 자식의 값, 부모의 값들을 제어하는데 사용(현재 여기서는 기본값으로 적용 되어있음),getchildview 제외*/
    @Override
    public int getGroupCount() {
        return mParentList.size();
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildHashMap.get(this.mParentList.get(groupPosition)).size();
    }
    @Override
    public String getGroup(int groupPosition) {
        return mParentList.get(groupPosition);
    }
    @Override
    public ChildListData getChild(int groupPosition, int childPosition) {
        return this.mChildHashMap.get(this.mParentList.get(groupPosition)).get(childPosition);
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater groupinfla = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = groupinfla.inflate(R.layout.first_row_layout,parent,false);
        }

        TextView parentText = (TextView)convertView.findViewById(R.id.parenttext);
        parentText.setText(getGroup(groupPosition));
        return convertView;
    }

    // Expandable Listsview의 자식 Listview의 UI를 선언하며, 해당 데이터의 값에 맞게 각 정보들을 표시함
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildListData childData = (ChildListData)getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater childinfla = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = childinfla.inflate(R.layout.first_child_row_layout,null);

            // VIewholder 선언
            mChildListViewHolder = new ChildListViewHolder();

            mChildListViewHolder.mListText02Holder = (TextView)convertView.findViewById(R.id.tab_kid_name);
            mChildListViewHolder.mListText03Holder = (TextView)convertView.findViewById(R.id.tab_kid_state);
            mChildListViewHolder.mListText04Holder = (TextView)convertView.findViewById(R.id.tab_kid_time);
            mChildListViewHolder.mListImage05Holder = (ImageView) convertView.findViewById(R.id.kid_image_arrive);
            mChildListViewHolder.mListButton06Holder = (Button)convertView.findViewById(R.id.hand_check);
            mChildListViewHolder.mListImage07Holder = (ImageView)convertView.findViewById(R.id.late_image_check);

            convertView.setTag(mChildListViewHolder);
        }else{
            mChildListViewHolder = (ChildListViewHolder)convertView.getTag();
        }


        //원아의 이름, 탑승 예정시간, 탑승 시간을 표시
        mChildListViewHolder.mListText02Holder.setText(getChild(groupPosition,childPosition).kid_n);
        mChildListViewHolder.mListText03Holder.setText(getChild(groupPosition,childPosition).onoff_type);
        mChildListViewHolder.mListText04Holder.setText("("+getChild(groupPosition,childPosition).onoff_time+")");



        //원아 사진을 넣음,
        if(getChild(groupPosition,childPosition).kid_image_arrive.equals("null")){
            Log.i("이미지 들어옴?","이미지 들어옴 "+getChild(groupPosition,childPosition).kid_image_arrive+"");
           mChildListViewHolder.mListImage05Holder.setImageResource(R.mipmap.tablet_ity_50x50);
        }else{
            Log.i("이미지 들어옴?","이미지 들어옴 "+getChild(groupPosition,childPosition).kid_image_arrive+"");
            Glide.with(mContext).load(getChild(groupPosition,childPosition).kid_image_arrive).into(mChildListViewHolder.mListImage05Holder);
        }
        route_id = getChild(groupPosition,childPosition).route_id;



        //원아의 지각/결석에 대한 정보를 조회하여 지각 일경우 노란색 동그라미, 결석일 경우 빨간색 동그라미를 선언함
        if(!getChild(groupPosition,childPosition).kid_system_code.equals("NO_LATE")) {
            if (getChild(groupPosition, childPosition).kid_system_code.equals("지각")) {
                mChildListViewHolder.mListImage07Holder.setImageResource(R.drawable.fb);
            } else if (getChild(groupPosition, childPosition).kid_system_code.equals("결석")) {
                mChildListViewHolder.mListImage07Holder.setImageResource(R.drawable.fc);
            }
        }

        //사용자가 원아들의 수동 출석 버튼을 누를 경우 실행됨, 이 이벤트는 현재 있는 정류장 기준으로 이전의 원아들만 이벤트가 발생되며, 도착예정 정류장에 한해서는 버튼 이벤트가 발생하지않음
        //이벤트가 발생하면 하원 노선 및 등원 노선 여부에 따라 각 thread가 실행됨
        mChildListViewHolder.mListButton06Holder.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("버튼 클릭 완료","버튼 클릭 완료");
                route_id = getChild(groupPosition,childPosition).route_id;
                beacon_id = getChild(groupPosition,childPosition).beacon_id;
                station_id = getChild(groupPosition,childPosition).station_id;

                Log.i("현재정류장adapter",Singleton.now_station+"  "+groupPosition+" "+getGroupCount());
                long check = -1;

                for(int i = 0 ; i< getGroupCount();i++){
                    if(getGroup(i).equals(Singleton.now_station)){
                        check = i+1;
                        Log.i("마지막정류장ID",getGroupId(groupPosition)+""+check);
                    }
                }
                if(check == -1){
                    Toast.makeText(v.getContext(),"도착 예정 정류장입니다.", Toast.LENGTH_SHORT).show();
                }
                else if(check < groupPosition && check != -1){
                    Toast.makeText(v.getContext(),"도착 예정 정류장입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(getChild(groupPosition,childPosition).getOnoff_time().equals(" - ")) {
                        if(getChild(groupPosition,childPosition).getOnoff_type().contains("탑승")) {
                            new hand_update().start();
                        }
                        else if(getChild(groupPosition,childPosition).getOnoff_type().contains("하차")){
                            new hand_update2().start();
                            Log.i("비콘아이디",beacon_id+" 123");
                        }
                    }else{
                        Toast.makeText(v.getContext(),"이미 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    // 사용자가 수동으로 원아의 출석을 하고자 할 경우 사용되는 thread 함수로, hand_upate와 hand_update2로 나뉘며, 각각 등원/하원의 경우 해당된다.
    class hand_update extends Thread {
        @Override
        public void run() {
            super.run();
            Log.i("쓰레드 시작(수동)","쓰레드 수동");

                try {
                    URL url = new URL(Singleton.Spring_URL+"update_hand_kid");
                    //URL url = new URL("http://192.168.0.30:8080/tablet/update_hand_kid");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("route_id").append("=").append(route_id).append("&");
                    buffer.append("station_id").append("=").append(station_id).append("&");
                    buffer.append("beacon_id").append("=").append(beacon_id);

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

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    public String readData(InputStream is){
        String data = "";
        Scanner s = new Scanner(is);
        while(s.hasNext()) data += s.nextLine();
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

            }
        });
    }

    class hand_update2 extends Thread {
        @Override
        public void run() {
            super.run();
            Log.i("쓰레드 시작(수동)","쓰레드 수동");

            try {
                URL url = new URL(Singleton.Spring_URL+"update_hand_kid2");
                //URL url = new URL("http://192.168.0.30:8080/tablet/update_hand_kid2");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                StringBuffer buffer = new StringBuffer();
                buffer.append("route_id").append("=").append(route_id).append("&");
                buffer.append("station_id").append("=").append(station_id).append("&");
                buffer.append("beacon_id").append("=").append(beacon_id);

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

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String readData2(InputStream is){
        String data = "";
        Scanner s = new Scanner(is);
        while(s.hasNext()) data += s.nextLine();
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

            }
        });
    }

}