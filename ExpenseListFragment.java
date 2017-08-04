package com.expensemanager;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.expensemanager.entities.Expense;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ExpenseListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    Button add;
    private Date fromdate,todate;
    TextView mode;
    android.widget.ListView listView;
    ArrayList<Expense> expenses,expensesFilter;
    SQLiteDatabase db;
    TextView user;
    EditText from, to;

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_list, container, false);
        add = (Button)rootView.findViewById(R.id.addExpense);
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        user = (TextView) getActivity().findViewById(R.id.userid);
        expenses = new ArrayList<Expense>();
        expensesFilter = new ArrayList<Expense>();
        expenses = getUsersDataFromDatabase();
        from = (EditText) rootView.findViewById(R.id.fromDateU);
        from.setOnClickListener(this);
        to = (EditText) rootView.findViewById(R.id.toDateU);
        to.setOnClickListener(this);
        listView = (android.widget.ListView) rootView.findViewById(R.id.listExpense);
        listView.setOnItemClickListener(this);
        add.setOnClickListener(this);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        fromdate = cal.getTime();
        from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy"));
        todate = new Date();
        to.setText(Util.formatDateToString(todate,"MM/dd/yyyy"));
        mode = (TextView)getActivity().findViewById(R.id.expensemode);
        try {
            filterExpenses(fromdate,todate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public ArrayList<Expense> getUsersDataFromDatabase() {
        String userid = user.getText().toString();
        String selectQuery = "SELECT  * FROM expense where userid="+"'"+userid+"'";
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
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        if(!(expensesFilter.size()==0)){
            expensesFilter.clear();
        }
        for(int i=0;i<expenses.size();i++){
            if(between(expenses.get(i).getDate(),from,to)){
                expensesFilter.add(expenses.get(i));
            }
        }
        ListExpenceAdapter adapter = new ListExpenceAdapter(expensesFilter, getContext());
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        String title = "";
        if(view.getId() == R.id.addExpense) {
            fragment = new ExpenseFragment();
            mode.setText("fromList");
            title = "Add Expense";
        }else if(view.getId() == R.id.fromDateU) {
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
        }else if(view.getId() == R.id.toDateU) {
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
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView text = (TextView) view.findViewById(R.id.useridExpense);
        Bundle bundle = new Bundle();
        bundle.putString("userid", text.getText().toString());
        UpdateExpenseFragment fragment = new UpdateExpenseFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Update Expense");
    }
}
