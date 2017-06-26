package cirrbagent.com.cirrbagentapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cirrbagent.com.cirrbagentapp.OrderProcressActivity;
import cirrbagent.com.cirrbagentapp.R;
import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.service.SimpleIntentService;
import cirrbagent.com.cirrbagentapp.util.CircleImageView;
import cirrbagent.com.cirrbagentapp.util.ConnectionDetector;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;
import cirrbagent.com.cirrbagentapp.util.ProgressHUD;

/**
 * Created by yuva on 13/6/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    ProgressHUD progressHUD;
    Context context;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    private void init() {
        String name = PreferenceClass.getStringPreferences(context, Constant.LOGINNAME) + " " + PreferenceClass.getStringPreferences(context, Constant.LOGINLASTNAME);
        ((TextView) rootView.findViewById(R.id.prfl_name)).setText(name);

        CircleImageView imageView = (CircleImageView) rootView.findViewById(R.id.circleView);

        String imge = PreferenceClass.getStringPreferences(context, Constant.LOGINIMAGE);
        Picasso.with(context)
                .load(imge)
                .into(imageView);

        ConnectionDetector cd = new ConnectionDetector(context);
        if (cd.isConnectingToInternet()) {
            int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
            getDetails(userId);
        } else {
            Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
            ((TextView) rootView.findViewById(R.id.tv_inhand)).setText(PreferenceClass.getIntegerPreferences(context, "INHAND") + "");
            ((TextView) rootView.findViewById(R.id.tv_limit)).setText(PreferenceClass.getIntegerPreferences(context, "LIMIT") + "");
        }

        ((Button) rootView.findViewById(R.id.btn_accept_order)).setOnClickListener(this);
    }

    private void getDetails(int userId) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aQuery = new AQuery(context);

        String url = Constant.BASE_URL + "customerDetails?user_id=" + userId;

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                Log.e("deatails Status is", status.getCode() + "");
                if (status.getCode() == 200) {

                    try {
                        JSONObject jsonObject = object.getJSONObject("details");
                        Log.e("Jsonobj  is", jsonObject + "");
                        int inHand = jsonObject.getInt("cash_in_hand");
                        int limit = jsonObject.getInt("order_limit");

                        PreferenceClass.setIntegerPreference(context, "INHAND", inHand);
                        PreferenceClass.setIntegerPreference(context, "LIMIT", limit);

                        ((TextView) rootView.findViewById(R.id.tv_inhand)).setText(inHand + "");
                        ((TextView) rootView.findViewById(R.id.tv_limit)).setText(limit + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.method(AQuery.METHOD_GET).header("Content-Type", "application/json").header("Authorization", PreferenceClass.getStringPreferences(context, Constant.LOGINTOKEN)));
    }


    private void setStatus(int partnerId, String partnerStatus) {
        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aquery = new AQuery(context);
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

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                if (status.getCode() == 200) {
                    startActivity(new Intent(getActivity(), OrderProcressActivity.class));
                }
            }
        }.method(AQuery.METHOD_POST).header("Authorization", PreferenceClass.getStringPreferences(getActivity(), Constant.LOGINTOKEN)))
        ;
    }

    private void acceptOrder(int userId) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aQuery = new AQuery(context);

        String url = Constant.BASE_URL + Constant.ACCEPT_ORDER;

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                Log.e("accept order status", status.getCode() + "");
                if (status.getCode() == 200) {
                    try {
                        JSONObject jsonObject = object.getJSONObject("details");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.method(AQuery.METHOD_GET).header("Content-Type", "application/json"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept_order:
                int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
                ConnectionDetector cd = new ConnectionDetector(context);
                if (cd.isConnectingToInternet()) {
                    setStatus(userId, "free");
                } else {
                    Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
                }

        }
    }
}
