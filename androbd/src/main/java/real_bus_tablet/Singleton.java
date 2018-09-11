package real_bus_tablet;

/**
 * Created by KST03 on 2018-02-28.
 */

public class Singleton {
    private static final Singleton ourInstance = new Singleton();

    static String now_station = null;

    static String Spring_URL = "http://13.125.170.255:8080/Smartbus_Tablet/";

    public static Singleton getInstance(){
        return ourInstance;
    }
    private Singleton(){
    }
}
