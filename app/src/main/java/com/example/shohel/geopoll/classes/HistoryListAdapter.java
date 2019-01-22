package com.example.shohel.geopoll.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.shohel.geopoll.R;

import java.util.ArrayList;

/**
 * Created by BDDL-102 on 11/19/2018.
 */

public class HistoryListAdapter extends ArrayAdapter {

    private ArrayList<TransactionData> transactionList;
    private Context context;

    public HistoryListAdapter(Context context, ArrayList<TransactionData> transactionList) {
        super(context, R.layout.payment_history_list);
        this.context = context;
        this.transactionList = transactionList;
    }

    static class ViewHolder {
        TextView paymentMethodTV;
        TextView amountTV;
        TextView paymentStatusTV;
        TextView paymentTimeTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.payment_history_list,null);
            viewHolder = new ViewHolder();

            viewHolder.paymentMethodTV = convertView.findViewById(R.id.paymentMethodPHTV);
            viewHolder.amountTV = convertView.findViewById(R.id.amountPHTV);
            viewHolder.paymentStatusTV = convertView.findViewById(R.id.paymentStatusPHTV);
            viewHolder.paymentTimeTV = convertView.findViewById(R.id.paymentTimePHTV);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (transactionList!=null) {
            viewHolder.paymentMethodTV.setText(transactionList.get(position).getPaymentMethod());
            viewHolder.amountTV.setText(transactionList.get(position).getAmount());
            viewHolder.paymentStatusTV.setText(transactionList.get(position).getPaymentStatus());
            viewHolder.paymentTimeTV.setText(transactionList.get(position).getPaymentTime());
        }

        return convertView;
    }
}