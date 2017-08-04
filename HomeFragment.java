package com.expensemanager;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.expensemanager.entities.Expense;
import com.expensemanager.entities.Income;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private Spinner month;
    ArrayList<Expense> expenses;
    ArrayList<Income> incomes;
    private TextView userid;
    SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
    Date currentDate;
    Calendar cal;
    float total_income=0f;
    float total_expense=0f;
    TextView expense, income, bal, limit, modeEx, modeIn;
    int itemPos;
    SQLiteDatabase db;
    String user = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        month = (Spinner) rootView.findViewById(R.id.monthSpinner);
        expense = (TextView) rootView.findViewById(R.id.expenseText);
        income = (TextView) rootView.findViewById(R.id.incomeText);
        modeEx = (TextView) getActivity().findViewById(R.id.expensemode);
        modeIn = (TextView) getActivity().findViewById(R.id.incomemode);
        bal = (TextView) rootView.findViewById(R.id.balText);
        limit = (TextView) rootView.findViewById(R.id.limitText);
        userid = (TextView) getActivity().findViewById(R.id.userid);
        user = userid.getText().toString();
        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemPos=position+1;
                try {
                    applyFilerMonth(itemPos);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Calendar c = Calendar.getInstance();
        int mMonth = c.get(Calendar.MONTH);
        ArrayList<String> categories =  new ArrayList<>();
        categories.add("January");
        categories.add("February");
        categories.add("March");
        categories.add("April");
        categories.add("May");
        categories.add("June");
        categories.add("July");
        categories.add("August");
        categories.add("September");
        categories.add("October");
        categories.add("November");
        categories.add("December");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,categories );
        month.setAdapter(dataAdapter);
        month.setSelection(mMonth);
        expenses = new ArrayList<Expense>();
        incomes = new ArrayList<Income>();
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        expenses= getUsersDataFromDatabase();
        incomes=getIncomeFromDatabase();
        return rootView;
    }

    public void applyFilerMonth(int month) throws ParseException {
        total_expense=0f;
        total_income=0f;
        for (int i=0;i<expenses.size();i++){
            currentDate =  sd.parse(expenses.get(i).getDate());
            cal = Calendar.getInstance();
            cal.setTime(currentDate);
            if((cal.get(Calendar.MONTH)+1)==month){
                total_expense += expenses.get(i).getTotal();
            }
        }
        for (int i=0;i<incomes.size();i++){
            currentDate =  sd.parse(incomes.get(i).getDate());
            cal = Calendar.getInstance();
            cal.setTime(currentDate);
            if((cal.get(Calendar.MONTH)+1)==month){
                total_income += incomes.get(i).getTotal();
            }
        }
        /*DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String totalExpense = String.valueOf(df.format(total_expense));
        String totalIncome = String.valueOf(df.format(total_income));
        String balance = String.valueOf(df.format(total_income - total_expense));
        Date date =  new Date();
        cal = Calendar.getInstance();
        cal.setTime(date);
        cal.get(Calendar.DATE);
        Calendar myCal = Calendar.getInstance();
        myCal.setTime(new Date());
        int daysInMonth = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String limitMonth = df.format((total_income - total_expense)/(daysInMonth-cal.get(Calendar.DATE)));
        limitMonth = limitMonth.replaceAll(",","");
        float limitInt = Float.parseFloat(limitMonth);
        expense.setText("$ "+totalExpense);
        income.setText("$ "+totalIncome);
        bal.setText("$ "+balance);*/

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String totalExpense = String.valueOf(df.format(total_expense));
        String totalIncome = String.valueOf(df.format(total_income));
        float balanceee = total_income - total_expense;
        String balance = String.valueOf(df.format(balanceee));
        Date date =  new Date();
        cal = Calendar.getInstance();
        cal.setTime(date);
        cal.get(Calendar.DATE);
        Calendar myCal = Calendar.getInstance();
        myCal.setTime(new Date());
        int daysInMonth = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String limitMonth = df.format((total_income - total_expense)/(daysInMonth-cal.get(Calendar.DATE)));
        limitMonth = limitMonth.replaceAll(",","");
        float limitInt = Float.parseFloat(limitMonth);
        if(total_income!=0)
            income.setText("$ "+totalIncome);
        else {
            income.setTextAlignment(Gravity.END);
            income.setText("$ 0");
        }
        if(total_expense!=0)
            expense.setText("$ "+totalExpense);
        else {
            expense.setTextAlignment(Gravity.END);
            expense.setText("$ 0");
        }
        if(balanceee!=0)
            bal.setText("$ "+balance);
        else {
            bal.setTextAlignment(Gravity.END);
            bal.setText("$ 0");
        }
        if(limitInt<0){
            limit.setText("Limit Exceeded!");
            limit.setTextColor(getResources().getColor(R.color.primary));
        }else{
            limit.setText("$ "+limitMonth);
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        (getView().findViewById(R.id.btn_expense)).setOnClickListener(this);
        (getView().findViewById(R.id.btn_income)).setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        String title = "";
        if(view.getId() == R.id.btn_expense) {
            modeEx.setText("fromHome");
            fragment = new ExpenseFragment();
            title = getString(R.string.title_expense);
        } else if (view.getId() == R.id.btn_income) {
            modeIn.setText("fromHome");
            fragment = new IncomeFragment();
            title = getString(R.string.title_income);
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    public ArrayList<Income> getIncomeFromDatabase() {

        String selectQuery = "SELECT  * FROM income where userid="+"'"+user+"'";
        Cursor mCursor = db.rawQuery(selectQuery, null);
        if (mCursor.moveToFirst()) {
            do {
                Income expense = new Income();
                expense.setId(mCursor.getString(mCursor.getColumnIndexOrThrow("id")));
                expense.setDescription(mCursor.getString(mCursor.getColumnIndexOrThrow("description")));
                expense.setDate(mCursor.getString(mCursor.getColumnIndexOrThrow("date")));
                expense.setTotal(Float.parseFloat(mCursor.getString(mCursor.getColumnIndexOrThrow("total"))));
                expense.setUserid(mCursor.getString(mCursor.getColumnIndexOrThrow("userid")));
                incomes.add(expense);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return incomes;
    }

    public ArrayList<Expense> getUsersDataFromDatabase() {
        String selectQuery = "SELECT  * FROM expense where userid="+"'"+user+"'";
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
}