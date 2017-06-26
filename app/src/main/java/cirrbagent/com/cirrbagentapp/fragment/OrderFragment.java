package cirrbagent.com.cirrbagentapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrbagent.com.cirrbagentapp.R;
import cirrbagent.com.cirrbagentapp.adapter.OrderHistoryAdapter;
import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.util.ConnectionDetector;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;
import cirrbagent.com.cirrbagentapp.util.ProgressHUD;

/**
 * Created by yuva on 13/6/17.
 */

public class OrderFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Context context;
    RecyclerView recyclerView;
    ProgressHUD progressHUD;
    ArrayList<JSONObject> orderHistoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    private void init() {
        ConnectionDetector cd = new ConnectionDetector(context);
        if (cd.isConnectingToInternet()) {
            int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
            showAllOrder(userId);
        } else {
            Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_ordr);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

    }


    private void showAllOrder(int usrId) {
        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aQuery = new AQuery(context);
        String url = Constant.BASE_URL + Constant.ORDER_HISTORY;
        final JSONObject jsonReq = new JSONObject();
        try {
            jsonReq.put("user_id", usrId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        aQuery.post(url, jsonReq, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                Log.e("Status order is", status.getCode() + "");

                if (status.getCode() == 200) {
                    try {

                        JSONArray jsonArray = object.getJSONArray("orders");
                        Log.e("Length is", jsonArray.length() + "");

                        if (jsonArray.length() == 0) {

                            orderHistoryList.clear();
                            recyclerView.setVisibility(View.GONE);
                            ((TextView) rootView.findViewById(R.id.tv_noOrder)).setText("No Order Found");
                            ((TextView) rootView.findViewById(R.id.tv_noOrder)).setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                recyclerView.setVisibility(View.VISIBLE);
                                ((TextView) rootView.findViewById(R.id.tv_noOrder)).setVisibility(View.GONE);
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                orderHistoryList.add(jsonObject);
                                OrderHistoryAdapter rvAdapter = new OrderHistoryAdapter(context, orderHistoryList, OrderFragment.this);
                                recyclerView.setAdapter(rvAdapter);
                                rvAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.method(AQuery.METHOD_POST).header("Content-Type", "application/json").header("Authorization", PreferenceClass.getStringPreferences(context, Constant.LOGINTOKEN)));

    }

    @Override
    public void onClick(View v) {


    }
}
