package cirrbagent.com.cirrbagentapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;
import cirrbagent.com.cirrbagentapp.util.ProgressHUD;

/**
 * Created by yuva on 22/6/17.
 */

public class OrderResponseActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressHUD progressHUD;
    Context context;
    TextView timer;
    Runnable updater;
    Toolbar mToolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_response);
        context = this;
        init();
    }

    private void init() {

        ((Button) findViewById(R.id.btn_accept)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_denied)).setOnClickListener(this);
        final TextView textic = (TextView) findViewById(R.id.tv_timer);

        CountDownTimer Count = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                textic.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                finish();
            }
        };
        Count.start();
    }

    /*private void setActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_res);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("AcceptOrder");
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }*/

    private void orderResponse(int partnerId, int orderId, final String orderstatus) {
        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aquery = new AQuery(this);
        final JSONObject jsonObject = new JSONObject();
        String url = Constant.BASE_URL + Constant.ORDER_RESPONSE;
        try {

            jsonObject.putOpt("partner_id", partnerId);
            jsonObject.putOpt("order_id", orderId);
            jsonObject.putOpt("approval", orderstatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        aquery.post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                if (status.getCode() == 200) {

                    if (Objects.equals(orderstatus, "accept")) {

                        try {
                            JSONObject jsnDetails = object.getJSONObject("details");
                            JSONObject jsnOrder = object.getJSONObject("order");
                            String ordeId = jsnOrder.getString("id");
                            String orderStatus = jsnOrder.getString("status");
                            double delivryFees = jsnOrder.getDouble("delivery_fees");
                            double total = jsnOrder.getDouble("total");
                            double commision = jsnOrder.getDouble("commission");
                            double paybla = jsnOrder.getDouble("order_payable");

                            JSONObject jsnUser = jsnOrder.getJSONObject("user");
                            double userLat = jsnUser.getDouble("lat");
                            double userong = jsnUser.getDouble("long");

                            JSONObject jsnBranch = jsnOrder.getJSONObject("branch");
                            double branchLat = jsnUser.getDouble("bl_lat");
                            double branchLong = jsnUser.getDouble("bl_long");

                            JSONArray jsnOrdrList = jsnOrder.getJSONArray("order_list");
                            for (int i = 0; i < jsnOrdrList.length(); i++) {
                                JSONObject jsnOrderList = jsnOrdrList.getJSONObject(i);
                                String name = jsnOrderList.getString("name");
                                String quantity = jsnOrderList.getString("quantity");
                                double cost = jsnOrderList.getDouble("quantity");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(OrderResponseActivity.this, AcceptOrderActiivty.class));
                        finish();
                    } else {
                        onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                //startActivity(new Intent(OrderResponseActivity.this, AcceptOrderActiivty.class));
                int partnetId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
                int orderId = PreferenceClass.getIntegerPreferences(context, Constant.ORDER_ID);
                orderResponse(partnetId, orderId, "accept");

                break;
            case R.id.btn_denied:
                int partnetI = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
                int orderIdD = PreferenceClass.getIntegerPreferences(context, Constant.ORDER_ID);
                orderResponse(partnetI, orderIdD, "deny");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
