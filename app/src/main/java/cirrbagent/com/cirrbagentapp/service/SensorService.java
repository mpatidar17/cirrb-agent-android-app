package cirrbagent.com.cirrbagentapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cirrbagent.com.cirrbagentapp.OrderProcressActivity;
import cirrbagent.com.cirrbagentapp.OrderResponseActivity;
import cirrbagent.com.cirrbagentapp.R;
import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    public int counter = 0;
    JSONObject jsonObject;

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 5000);
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                System.out.println("SystemClock is: " + Calendar.getInstance().getTimeInMillis());
                Log.i("in timer", "in timer ++++  " + (counter++));

                final int userId = PreferenceClass.getIntegerPreferences(getApplicationContext(), Constant.LOGINEMPID);
                getNearestOrder(userId);
            }
        };
    }

    /**
     * not needed
     */

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getNearestOrder(int partnerId) {

        AQuery aQuery = new AQuery(getApplicationContext());
        String url = Constant.BASE_URL + "getnearestorder?partner_id=" + partnerId;

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                Log.e("Nearest status", status.getCode() + "");
                if (status.getCode() == 200) {
                    Log.e("Nearest Json objct", object + "");
                    try {

                        if (object instanceof JSONObject) {
                            jsonObject = object.getJSONObject("details");
                            int orderId = jsonObject.getInt("id");
                            String userId = jsonObject.getString("user_id");
                            String restId = jsonObject.getString("restaurant_id");
                            String restName = jsonObject.getString("restaurant_name");
                            String restPhn = jsonObject.getString("restaurant_phone");
                            String branchLat = jsonObject.getString("branch_lat");
                            String branchLong = jsonObject.getString("branch_long");
                            String subTotal = jsonObject.getString("sub_total");
                            String partnerId = jsonObject.getString("partner_id");
                            String ordstatus = jsonObject.getString("status");
                            String dlvryFees = jsonObject.getString("delivery_fees");
                            String total = jsonObject.getString("total");
                            String phoneno = jsonObject.getString("restaurant_phone");

                            PreferenceClass.setStringPreference(getApplicationContext(), Constant.BRANCH_LATITUDE, branchLat);
                            PreferenceClass.setStringPreference(getApplicationContext(), Constant.BRANCH_LONGITUDE, branchLong);
                            PreferenceClass.setIntegerPreference(getApplicationContext(), Constant.ORDER_ID, orderId);

                        } else {
                            JSONArray jsonObject = object.getJSONArray("details");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Exception is", e + "");
                    }

                }
            }
        }.method(AQuery.METHOD_GET).header("Content-Type", "application/json").header("Authorization", PreferenceClass.getStringPreferences(getApplicationContext(), Constant.LOGINTOKEN)));
    }

    public void processStartNotification() {
        //System.out.print("Notifiaction Title is"+feed_title);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("New Feed Received")
                .setAutoCancel(true)
                // .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("")
                .setSmallIcon(R.drawable.logo_small);

        Intent mainIntent = new Intent(getApplicationContext(), OrderProcressActivity.class);
        int count = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                count,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        PreferenceClass.setBooleanPreference(getApplicationContext(), Constant.NOTIFICATION, true);
        final NotificationManager manager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(count, builder.build());
        builder.setAutoCancel(true);
    }
}