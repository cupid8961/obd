package real_bus_tablet;

/**
 * Created by KST03 on 2017-11-03.
 */

public class Tab_Second_Data {

    private String titlestr;
    private String descstr;
    private String late_typestr;
    private String kid_imagestr;
    private String late_contstr;

    public void setLate_typestr(String late_type ){late_typestr = late_type;}
    public void setTitlestr(String title){
        titlestr = title;
    }
    public void setDesc(String desc){
        descstr = desc;
    }
    public void setKid_imagestr(String kid_image){kid_imagestr = kid_image;}

    public String getLate_typestr(){return  this.late_typestr;}
    public String getTitle() {
        return this.titlestr ;
    }
    public String getDesc() {
        return this.descstr ;
    }
    public String getKid_imagestr(){return this.kid_imagestr;}

    public String getTitlestr() {
        return titlestr;
    }

    public String getDescstr() {
        return descstr;
    }

    public void setDescstr(String descstr) {
        this.descstr = descstr;
    }

    public String getLate_contstr() {
        return late_contstr;
    }

    public void setLate_contstr(String late_contstr) {
        this.late_contstr = late_contstr;
    }
}
