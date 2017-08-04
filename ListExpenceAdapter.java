package com.expensemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.expensemanager.entities.Expense;
import java.util.ArrayList;

public class ListExpenceAdapter extends ArrayAdapter<Expense> implements View.OnClickListener {
    private ArrayList<Expense> dataSet;
    Context mContext;
    private static class ViewHolder {
        TextView type;
        TextView date;
        TextView total;
        TextView userid;
    }

    public ListExpenceAdapter(ArrayList<Expense> data, Context context) {
        super(context, R.layout.custom_listview_expense, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Expense dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_listview_expense, parent, false);
            viewHolder.type = (TextView) convertView.findViewById(R.id.etype);
            viewHolder.date = (TextView) convertView.findViewById(R.id.edate);
            viewHolder.total = (TextView) convertView.findViewById(R.id.eTotal);
            viewHolder.userid = (TextView) convertView.findViewById(R.id.useridExpense);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.type.setText(dataModel.getCategory());
        viewHolder.date.setText(dataModel.getDate());
        viewHolder.total.setText("$ "+String.valueOf(dataModel.getTotal()));
        viewHolder.userid.setText(String.valueOf(dataModel.getId()));
        return convertView;
    }
}
