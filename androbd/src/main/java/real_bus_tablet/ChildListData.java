package real_bus_tablet;

import android.os.Parcelable;

/**
 * Created by KST03 on 2017-11-21.
 */

public class ChildListData {

    // TextView01에 상응
    public String station_n;
    // TextView02에 상응
    public String kid_n;
    public String onoff_type;
    public String onoff_time;
    public Parcelable list_width;
    public String kid_image_arrive;
    public String route_id;
    public String station_id;
    public String beacon_id;
    public String kid_system_code;

    /*생성자 생성안해서 2시간 삽질함, 개노답, 등신임*/
    public ChildListData(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {

        this.kid_n = s1;
        this.onoff_type = s2;
        this.onoff_time = s3;
        this.kid_image_arrive = s4;
        this.route_id = s5;
        this.station_id = s6;
        this.beacon_id = s7;
        this.kid_system_code = s8;

    }

    public ChildListData(){

    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(String beacon_id) {
        this.beacon_id = beacon_id;
    }

    public String getKid_image_arrive() {
        return kid_image_arrive;
    }

    public void setKid_image_arrive(String kid_image_arrive) {
        this.kid_image_arrive = kid_image_arrive;
    }

    public Parcelable getList_width() {
        return list_width;
    }

    public void setList_width(Parcelable list_width) {
        this.list_width = list_width;
    }

    public String getStation_n() {
        return station_n;
    }

    public void setStation_n(String station_n) {
        this.station_n = station_n;
    }

    public String getKid_n() {
        return kid_n;
    }

    public void setKid_n(String kid_n) {
        this.kid_n = kid_n;
    }

    public String getOnoff_type() {
        return onoff_type;
    }

    public void setOnoff_type(String onoff_type) {
        this.onoff_type = onoff_type;
    }

    public String getOnoff_time() {
        return onoff_time;
    }

    public void setOnoff_time(String onoff_time) {
        this.onoff_time = onoff_time;
    }
}
