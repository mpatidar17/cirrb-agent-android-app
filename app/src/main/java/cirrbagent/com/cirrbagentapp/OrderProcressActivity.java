package cirrbagent.com.cirrbagentapp;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.service.SensorService;
import cirrbagent.com.cirrbagentapp.service.SimpleIntentService;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;
import cirrbagent.com.cirrbagentapp.util.ProgressHUD;
import cirrbagent.com.cirrbagentapp.util.PulsatorLayout;

/**
 * Created by yuva on 22/6/17.
 */

public class OrderProcressActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    ProgressHUD progressHUD;
    PulsatorLayout mPulsator;
    Handler handler;
    Runnable runnable;
    Intent mServiceIntent;
    private SensorService mSensorService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_process);
        context = this;
        init();
    }

    private void init() {

        ((Button) findViewById(R.id.btn_stop_order)).setOnClickListener(this);
        mPulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        mPulsator.start();

        mSensorService = new SensorService(context);
        mServiceIntent = new Intent(context, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    @Override
    protected void onDestroy() {
        // stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Runnable on", "Runnable Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*final Handler h = new Handler();
        final int delay = 1000;
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                startService(new Intent(getApplicationContext(), SimpleIntentService.class));
                h.postDelayed(this, delay);
            }
        }, delay);*/
    }

    private void getNearestOrder(int partnerId) {

        AQuery aQuery = new AQuery(context);
        String url = Constant.BASE_URL + "getnearestorder?partner_id=" + partnerId;

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                Log.e("Nearest status", status.getCode() + "");
                if (status.getCode() == 200) {
                    Log.e("Nearest Json objct", object + "");
                    try {

                        if (object instanceof JSONObject) {
                            JSONObject jsonObject = object.getJSONObject("details");
                            String orderId = String.valueOf(jsonObject.getInt("id"));
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

                            PreferenceClass.setStringPreference(context, Constant.ORDER_ID, orderId);
                            PreferenceClass.setStringPreference(context, Constant.BRANCH_LATITUDE, branchLat);
                            PreferenceClass.setStringPreference(context, Constant.BRANCH_LONGITUDE, branchLong);

                        } else {
                            JSONArray jsonObject = object.getJSONArray("details");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Exception is", e + "");
                    }

                }
            }
        }.method(AQuery.METHOD_GET).header("Content-Type", "application/json").header("Authorization", PreferenceClass.getStringPreferences(context, Constant.LOGINTOKEN)));
    }


    private void setStatus(int partnerId, String partnerStatus) {
        AQuery aquery = new AQuery(this);
        final JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.UPDATE_STATUS;
        try {
            jsonObject.putOpt("partner_id", partnerId);
            jsonObject.putOpt("partner_status", partnerStatus);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        aquery.post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (status.getCode() == 200) {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop_order:
                int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
                setStatus(userId, "busy");
                break;
        }
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
