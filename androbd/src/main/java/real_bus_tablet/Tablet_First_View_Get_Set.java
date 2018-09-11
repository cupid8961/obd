package real_bus_tablet;

/**
 * Created by KST03 on 2017-11-16.
 */

/*변수 선언 클래스*/
public class Tablet_First_View_Get_Set {
    private String station_id;
    private String station_name;
    private String sch_time;
    private int real_time_id;
    private int cnt;
    private String real_time_type;

    private String par_id;
    private String par_name;
    private String late_abse_type;
    private String late_abse_cont;
    private String createtime;
    private String arrive_time;
    private int borad_nem;
    private String kid_name;
    private String kid_pho_url;



    public Tablet_First_View_Get_Set() {

    }

    public String getKid_pho_url() {
        return kid_pho_url;
    }

    public void setKid_pho_url(String kid_pho_url) {
        this.kid_pho_url = kid_pho_url;
    }

    public String getKid_name() {
        return kid_name;
    }

    public void setKid_name(String kid_name) {
        this.kid_name = kid_name;
    }

    public int getBorad_nem() {
        return borad_nem;
    }

    public void setBorad_nem(int borad_nem) {
        this.borad_nem = borad_nem;
    }

    public String getArrive_time() {
        return arrive_time;
    }

    public void setArrive_time(String arrive_time) {
        this.arrive_time = arrive_time;
    }

    public String getPar_id() {
        return par_id;
    }

    public void setPar_id(String par_id) {
        this.par_id = par_id;
    }

    public String getPar_name() {
        return par_name;
    }

    public void setPar_name(String par_name) {
        this.par_name = par_name;
    }

    public String getLate_abse_type() {
        return late_abse_type;
    }

    public void setLate_abse_type(String late_abse_type) {
        this.late_abse_type = late_abse_type;
    }

    public String getLate_abse_cont() {
        return late_abse_cont;
    }

    public void setLate_abse_cont(String late_abse_cont) {
        this.late_abse_cont = late_abse_cont;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getStation_id() {

        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getSch_time() {
        return sch_time;
    }

    public void setSch_time(String sch_time) {
        this.sch_time = sch_time;
    }

    public int getReal_time_id() {
        return real_time_id;
    }

    public void setReal_time_id(int real_time_id) {
        this.real_time_id = real_time_id;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getReal_time_type() {
        return real_time_type;
    }

    public void setReal_time_type(String real_time_type) {
        this.real_time_type = real_time_type;
    }
}
