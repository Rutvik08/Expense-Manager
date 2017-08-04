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
import com.expensemanager.entities.Expense;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PieChartFragment extends Fragment implements View.OnClickListener{

    private PieChart pcCategories;
    private TextView tvPcCategoriesEmpty;
    private TextView user ;
    private Date todate,fromdate;
    EditText from, to;
    private SQLiteDatabase db;
    private List<Expense> expenses, expenseFilter;

    public PieChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pcCategories = (PieChart) rootView.findViewById(R.id.pc_categories);
        tvPcCategoriesEmpty = (TextView)rootView.findViewById(R.id.tv_pie_categories_chart_empty);
        user = (TextView) getActivity().findViewById(R.id.userid);
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        from = (EditText) rootView.findViewById(R.id.fromDateP);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        fromdate = cal.getTime();
        from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy"));
        from.setOnClickListener(this);
        to = (EditText) rootView.findViewById(R.id.toDateP);
        todate = new Date();
        to.setText(Util.formatDateToString(todate,"MM/dd/yyyy"));
        to.setOnClickListener(this);
        expenses = new ArrayList<Expense>();
        expenses = getUsersDataFromDatabase();
        expenseFilter = new ArrayList<Expense>();
        try {
            filterExpenses(fromdate,todate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.close();
        return rootView;
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
        if(!(expenseFilter.size()==0)){
            expenseFilter.clear();
        }
        for(int i=0;i<expenses.size();i++){
            if(between(expenses.get(i).getDate(),from,to)){
                expenseFilter.add(expenses.get(i));
            }
        }
        setupCharts();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupCharts();
    }

    private void setupCharts() {
        pcCategories.setCenterText("");
        pcCategories.setCenterTextSize(10f);
        pcCategories.setHoleRadius(50f);
        pcCategories.setTransparentCircleRadius(55f);
        pcCategories.setUsePercentValues(true);
        pcCategories.setDescription("");
        pcCategories.setNoDataText("");
        Legend l = pcCategories.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        pcCategories.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        setCategoriesPieChart();
    }

    public float filterExpenses(String category) {
        float total = 0f;
        for (int i = 0; i < expenseFilter.size(); i++) {
            if(expenseFilter.get(i).getCategory().equals(category)){
                total+=expenseFilter.get(i).getTotal();
            }
        }
        return total;
    }

    private void setCategoriesPieChart() {
        List<String> categoriesNames = new ArrayList<>();
        List<Entry> categoryPercentagesEntries = new ArrayList<>();
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
        List<String> blank =  new ArrayList<>();
        for (int i=0; i < categories.size(); i++) {
            float percentage = filterExpenses(categories.get(i));
            if( percentage > 0) {
                categoriesNames.add(categories.get(i));
                blank.add("");
                Entry pieEntry = new Entry(percentage, categoriesNames.size()-1);
                categoryPercentagesEntries.add(pieEntry);
            }
        }
        if (categoriesNames.isEmpty()) {
            tvPcCategoriesEmpty.setVisibility(View.VISIBLE);
        } else {
            tvPcCategoriesEmpty.setVisibility(View.GONE);
        }

        PieDataSet dataSet = new PieDataSet(categoryPercentagesEntries, "Categories");
        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Util.getListColors());
        PieData data = new PieData(categoriesNames, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(getResources().getColor(R.color.black));
        pcCategories.setData(data);
        pcCategories.invalidate();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fromDateP) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromdate);
            DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(year, month, day);
                    fromdate = calendar.getTime();
                    from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy" ));
                    try {                filterExpenses(fromdate, todate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, calendar);
        }else if(view.getId() == R.id.toDateP) {
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
