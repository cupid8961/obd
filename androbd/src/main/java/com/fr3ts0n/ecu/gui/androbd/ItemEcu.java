package com.fr3ts0n.ecu.gui.androbd;

import java.util.Date;

public class ItemEcu {

    public final static int TIME_DEADLINE_DELAY = 1 * 60 * 1000; // 종료 제한시간 1분 -> 추후 20분
    int no;
    String driver;
    int eventindex;
    String timenow;
    String phonenum;
    String obdmacaddress;
    int distance;
    int velocity;
    int velocityaver;
    double fuelefficiency;
    double fuelamount;
    double internaltem;
    double externaltem;
    String errorlog;


    public ItemEcu() {
    }

    public ItemEcu(int eventindex, int no, String driver, String timenow, String phonenum, String obdmacaddress, int distance, int velocity, int velocityaver, double fuelefficiency, double fuelamount, double internaltem, double externaltem, String errorlog) {
        this.eventindex = eventindex;
        this.no = no;
        this.driver = driver;
        this.timenow = timenow;
        this.phonenum = phonenum;
        this.obdmacaddress = obdmacaddress;
        this.distance = distance;
        this.velocity = velocity;
        this.velocityaver = velocityaver;
        this.fuelefficiency = fuelefficiency;
        this.fuelamount = fuelamount;
        this.internaltem = internaltem;
        this.externaltem = externaltem;
        this.errorlog = errorlog;

    }

    @Override
    public String toString() {
        return "ItemEcu{" +
                "no=" + no +
                ", driver='" + driver + '\'' +
                ", eventindex=" + eventindex +
                ", timenow='" + timenow + '\'' +
                ", phonenum='" + phonenum + '\'' +
                ", obdmacaddress='" + obdmacaddress + '\'' +
                ", distance=" + distance +
                ", velocity=" + velocity +
                ", velocityaver=" + velocityaver +
                ", fuelefficiency=" + fuelefficiency +
                ", fuelamount=" + fuelamount +
                ", internaltem=" + internaltem +
                ", externaltem=" + externaltem +
                ", errorlog='" + errorlog + '\'' +
                '}';
    }
}
