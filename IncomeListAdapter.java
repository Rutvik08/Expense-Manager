package com.expensemanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.expensemanager.entities.Income;
import java.util.ArrayList;


public class IncomeListAdapter extends ArrayAdapter<Income> implements View.OnClickListener {
    private ArrayList<Income> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView type;
        TextView date;
        TextView total;
        TextView userid;

    }

    public IncomeListAdapter(ArrayList<Income> data, Context context) {
        super(context, R.layout.custom_listview_income, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Income dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_listview_income, parent, false);
            viewHolder.type = (TextView) convertView.findViewById(R.id.itype);
            viewHolder.date = (TextView) convertView.findViewById(R.id.idate);
            viewHolder.total = (TextView) convertView.findViewById(R.id.itotal);
            viewHolder.userid = (TextView) convertView.findViewById(R.id.useridIncome);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.type.setText(dataModel.getDescription());
        viewHolder.date.setText(dataModel.getDate());
        viewHolder.total.setText("$ "+String.valueOf(dataModel.getTotal()));
        viewHolder.userid.setText(dataModel.getId());
        return convertView;
    }
}
