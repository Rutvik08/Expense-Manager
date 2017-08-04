package com.expensemanager;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.expensemanager.entities.Expense;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BarChartFragment extends Fragment implements View.OnClickListener{

    private TextView tvBcCategoriesEmpty;
    private BarChart bcCategories;
    private SQLiteDatabase db;
    private List<Expense> expenses, expensesFilter;
    private Date todate,fromdate;
    private TextView user;
    EditText from, to;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        bcCategories = (BarChart) rootView.findViewById(R.id.bc_categories);
        tvBcCategoriesEmpty = (TextView)rootView.findViewById(R.id.tv_bar_chart_category_empty);
        user = (TextView) getActivity().findViewById(R.id.userid);
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        from = (EditText) rootView.findViewById(R.id.fromDateB);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        fromdate = cal.getTime(); //setting one month before
        from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy"));
        from.setOnClickListener(this);
        to = (EditText) rootView.findViewById(R.id.toDateB);
        todate = new Date();
        to.setText(Util.formatDateToString(todate,"MM/dd/yyyy"));
        to.setOnClickListener(this);
        expenses = new ArrayList<Expense>();
        expensesFilter = new ArrayList<Expense>();
        expenses = getUsersDataFromDatabase();
        try {
            filterExpenses(fromdate,todate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.close();
        return rootView;
    }

    public List<Expense> getUsersDataFromDatabase() {
        String userid = user.getText().toString();
        String selectQuery = "SELECT  * FROM expense where userid=" + "'" + userid + "'";
        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(mCursor.getString(mCursor.getColumnIndexOrThrow("id")));
                expense.setCategory(mCursor.getString(mCursor.getColumnIndexOrThrow("type")));
                expense.setDescription(mCursor.getString(mCursor.getColumnIndexOrThrow("description")));
                expense.setDate(mCursor.getString(mCursor.getColumnIndexOrThrow("date")));
                expense.setTotal(Float.parseFloat(mCursor.getString(mCursor.getColumnIndexOrThrow("total"))));
                expense.setUserid(mCursor.getString(mCursor.getColumnIndexOrThrow("userid")));
                expenses.add(expense);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return expenses;
    }

    public float filterExpenses(String category) {
        float total = 0f;
        for (int i = 0; i < expensesFilter.size(); i++) {
            if(expensesFilter.get(i).getCategory().equals(category)){
                total = total + expensesFilter.get(i).getTotal();
            }
        }
    return total;
    }

    public Date convertDateToDate(Date date){
        SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
        String stringDate = sd.format(date);
        Date result = new Date();
        try {
            result = sd.parse(stringDate);
        } catch(ParseException e){
        } catch(Exception e){
        }
        return result;
    }

    public  boolean between(String date1, Date dateStart1, Date dateEnd1) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
        Date date = sd.parse(date1);
        Date dateStart = convertDateToDate(dateStart1);
        Date dateEnd = convertDateToDate(dateEnd1);
        boolean result =false;
        if (date != null && dateStart != null && dateEnd != null) {
            if (!date.before(dateStart) && !date.after(dateEnd)) {
                result =  true;
            }
            else{
                result=false;
            }
        }
        return result;
    }

    public void filterExpenses(Date from, Date to) throws ParseException {
        if(!(expensesFilter.size()==0)){
            expensesFilter.clear();
        }

        for(int i=0;i<expenses.size();i++){
            if(between(expenses.get(i).getDate(),from,to)){
                expensesFilter.add(expenses.get(i));
            }
        }
        setupCharts();
    }

    public BarChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupCharts() {
        bcCategories.setDescription("");
        bcCategories.setNoDataText("");
        bcCategories.animateY(2000);
        bcCategories.getAxisLeft().setDrawGridLines(false);
        bcCategories.getXAxis().setDrawGridLines(false);
        bcCategories.getAxisRight().setDrawGridLines(false);
        bcCategories.getAxisRight().setDrawLabels(false);
        bcCategories.notifyDataSetChanged();
        bcCategories.invalidate();
        setCategoriesBarChart();
    }

    private void setCategoriesBarChart() {
        List<String> categoriesNames = new ArrayList<>();
        List<BarEntry> entryPerCategory = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        categories.add("Cloths");
        categories.add("Eating Out");
        categories.add("Entertainment");
        categories.add("Gifts");
        categories.add("General");
        categories.add("Holidays");
        categories.add("Kids");
        categories.add("Shopping");
        categories.add("Sports");
        categories.add("Travel");
        categories.add("Fuel");
        for (int i=0; i < categories.size(); i++) {
           float value = filterExpenses(categories.get(i));
            if (value > 0) {
                categoriesNames.add(categories.get(i));
                entryPerCategory.add(new BarEntry(value, categoriesNames.size()-1));
            }
        }
        if (categoriesNames.isEmpty()) {
            tvBcCategoriesEmpty.setVisibility(View.VISIBLE);
            bcCategories.setVisibility(View.GONE);
        } else {
            tvBcCategoriesEmpty.setVisibility(View.GONE);
            bcCategories.setVisibility(View.VISIBLE);
        }
        BarDataSet dataSet = new BarDataSet(entryPerCategory, "Categories");
        dataSet.setColors(Util.getListColors());
        BarData barData = new BarData(categoriesNames, dataSet);
        bcCategories.setData(barData);
        bcCategories.invalidate();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fromDateB) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromdate);
            DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(year, month, day);
                    fromdate = calendar.getTime();
                    from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy" ));
                    try {
                        filterExpenses(fromdate, todate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, calendar);
        }else if(view.getId() == R.id.toDateB) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(todate);
            DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(year, month, day);
                    todate = calendar.getTime();
                    to.setText(Util.formatDateToString(todate,"MM/dd/yyyy" ));
                    try {
                        filterExpenses(fromdate, todate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, calendar);
        }
    }
}
