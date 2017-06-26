package cirrbagent.com.cirrbagentapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrbagent.com.cirrbagentapp.R;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHolder> {

    ArrayList<JSONObject> historyarrayList;
    View.OnClickListener clickListener;
    Context context;


    public OrderHistoryAdapter(Context context, ArrayList<JSONObject> arrayList, View.OnClickListener clickListener) {
        this.context = context;
        this.historyarrayList = arrayList;
        this.clickListener = clickListener;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history, parent, false);
        OrderHolder imh = new OrderHolder(v);
        return imh;
    }

    @Override
    public void onBindViewHolder(OrderHolder viewHolder, int position) {

        try {
            String orderId = historyarrayList.get(position).getString("id");
            String total = historyarrayList.get(position).getString("total");
            String status = historyarrayList.get(position).getString("status");

            viewHolder.tvHashNo.setText(orderId);
            viewHolder.tvSrPrice.setText(total);
            viewHolder.tvStatus.setText(status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (historyarrayList != null) {
            return historyarrayList.size();
        }
        return 0;
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {

        TextView tvHashNo, tvSrPrice,tvStatus;

        OrderHolder(View itemView) {
            super(itemView);

            tvHashNo = (TextView) itemView.findViewById(R.id.tv_hash_no);
            tvSrPrice = (TextView) itemView.findViewById(R.id.sr_price);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);

        }
    }
}



   /* public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(String.valueOf(encodedString), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }*/



















/*
package com.example.yuva.instagramdemonew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public abstract class HomeFeedAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<String> arrayList;
    private Context context;


    public HomeFeedAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
    }

    public HomeFeedAdapter() {
        setHasStableIds(true);
    }


    public void clear() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        arrayList.remove(object);
        notifyDataSetChanged();
    }

    public String getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
*/
