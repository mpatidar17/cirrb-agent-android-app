package cirrbagent.com.cirrbagentapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cirrbagent.com.cirrbagentapp.MainActivity;
import cirrbagent.com.cirrbagentapp.OrderProcressActivity;
import cirrbagent.com.cirrbagentapp.OrderResponseActivity;
import cirrbagent.com.cirrbagentapp.R;
import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;

/**
 * Created by yuva on 17/6/17.
 */

public class SimpleIntentService extends IntentService {


    public SimpleIntentService() {
        super("MyIntentServiceName");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("Time", "Running");
                final int userId = PreferenceClass.getIntegerPreferences(getApplicationContext(), Constant.LOGINEMPID);
                getNearestOrder(userId);
            }
        });
    }

    @Override
    public boolean stopService(Intent name) {
        Log.e("Time", "stopping");
        return super.stopService(name);

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
                            JSONObject jsonObject = object.getJSONObject("details");
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

                            PreferenceClass.setIntegerPreference(getApplicationContext(), Constant.ORDER_ID, orderId);
                            PreferenceClass.setStringPreference(getApplicationContext(), Constant.BRANCH_LATITUDE, branchLat);
                            PreferenceClass.setStringPreference(getApplicationContext(), Constant.BRANCH_LONGITUDE, branchLong);
                            startActivity(new Intent(getApplicationContext(), OrderResponseActivity.class));
                            stopSelf();
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