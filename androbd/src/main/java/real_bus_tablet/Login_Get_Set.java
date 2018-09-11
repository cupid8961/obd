package real_bus_tablet;

import java.io.Serializable;

/**
 * Created by KST03 on 2017-11-08.
 */

public class Login_Get_Set implements Serializable {
    private String staff_id;
    private String staff_pw;
    private String staff_name;
    private String staff_charge;
    private String staff_type;
    private String center_code;
    private String route_id;
    private String route_name;
    private String course_id;
    private String bus_id;
    private int admin_area;
    private String center_name;
    private String route_type;
    private String off_time;
    private String station_id_off;
    private String kid_system_code;


    public Login_Get_Set() {
    }

    public String getCenter_name() {
        return center_name;
    }

    public String getRoute_type() {
        return route_type;
    }

    public String getKid_system_code() {
        return kid_system_code;
    }

    public void setKid_system_code(String kid_system_code) {
        this.kid_system_code = kid_system_code;
    }

    public String getOff_time() {
        return off_time;
    }

    public void setOff_time(String off_time) {
        this.off_time = off_time;
    }

    public String getStation_id_off() {
        return station_id_off;
    }

    public void setStation_id_off(String station_id_off) {
        this.station_id_off = station_id_off;
    }

    public void setRoute_type(String route_type) {
        this.route_type = route_type;
    }

    public void setCenter_name(String center_name) {
        this.center_name = center_name;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getStaff_pw() {
        return staff_pw;
    }

    public void setStaff_pw(String staff_pw) {
        this.staff_pw = staff_pw;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }

    public String getStaff_charge() {
        return staff_charge;
    }

    public void setStaff_charge(String staff_charge) {
        this.staff_charge = staff_charge;
    }

    public String getStaff_type() {
        return staff_type;
    }

    public void setStaff_type(String staff_type) {
        this.staff_type = staff_type;
    }

    public String getCenter_code() {
        return center_code;
    }

    public void setCenter_code(String center_code) {
        this.center_code = center_code;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public int getAdmin_area() {
        return admin_area;
    }

    public void setAdmin_area(int admin_area) {
        this.admin_area = admin_area;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }
}
