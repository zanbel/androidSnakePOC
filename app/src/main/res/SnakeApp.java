/**
 * Created by roy on 26-Dec-14.
 */

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;

public class SnakeApp extends Application {

    PowerManager pm;
    PowerManager.WakeLock wl;
    public static long delay = 400;
    public static double threshold = 1.5;
    public static double range = 0.9;
    public static int userId = 1;
    public static int speed = 1;
    public static String fromDate;
    public static String toDate;

    public static String getFromDate() {
        return fromDate;
    }

    public static void setFromDate(String fromDate) {
        SnakeApp.fromDate = fromDate;
    }

    public static String getToDate() {
        return toDate;
    }

    public static void setToDate(String toDate) {
        SnakeApp.toDate = toDate;
    }

    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        SnakeApp.speed = speed;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        SnakeApp.userId = userId;
    }

    public static double getRange() {
        return range;
    }

    public static void setRange(double range) {
        SnakeApp.range = range;
    }

    public static double getThreshold() {
        return threshold;
    }

    public static void setThreshold(double threshold) {
        SnakeApp.threshold = threshold;
    }

    public static long getDelay() {
        return delay;
    }

    public static void setDelay(long delay) {
        SnakeApp.delay = delay;
    }

    public SnakeApp(){

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        wl.release();
    }
}
