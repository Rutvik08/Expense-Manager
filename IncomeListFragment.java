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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.expensemanager.entities.Income;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class IncomeListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    Button add;
    private Date todate,fromdate;
    TextView mode;
    ListView listView;
    ArrayList<Income> incomes,incomeFilter;
    SQLiteDatabase db;
    TextView user;
    EditText from, to;

    public IncomeListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income_list, container, false);
        add = (Button)rootView.findViewById(R.id.addIncome);
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        user = (TextView) getActivity().findViewById(R.id.userid);
        incomes = new ArrayList<Income>();
        incomeFilter = new ArrayList<Income>();
        incomes = getUsersDataFromDatabase();
        from = (EditText) rootView.findViewById(R.id.fromDateI);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        fromdate = cal.getTime(); //setting one month before
        from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy"));
        from.setOnClickListener(this);
        to = (EditText) rootView.findViewById(R.id.toDateI);
        todate = new Date();
        to.setText(Util.formatDateToString(todate,"MM/dd/yyyy"));
        to.setOnClickListener(this);
        listView = (android.widget.ListView) rootView.findViewById(R.id.listincome);
        listView.setOnItemClickListener(this);
        IncomeListAdapter adapter = new IncomeListAdapter(incomeFilter, getContext());
        listView.setAdapter(adapter);
        add.setOnClickListener(this);
        mode = (TextView)getActivity().findViewById(R.id.incomemode);
        try {
            filterIncomes(fromdate,todate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public ArrayList<Income> getUsersDataFromDatabase() {
        String userid = user.getText().toString();
        String selectQuery = "SELECT  * FROM income where userid="+"'"+userid+"'";
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

    public void filterIncomes(Date from, Date to) throws ParseException {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        if(!(incomeFilter.size()==0)){
            incomeFilter.clear();
        }
        for(int i=0;i<incomes.size();i++){
            if(between(incomes.get(i).getDate(),from,to)){
                incomeFilter.add(incomes.get(i));
            }
        }
        IncomeListAdapter adapter = new IncomeListAdapter(incomeFilter, getContext());
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        String title = "";
        if(view.getId() == R.id.addIncome) {
            fragment = new IncomeFragment();
            mode.setText("fromList");
            title = "Add Income";
        }else if(view.getId() == R.id.fromDateI) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromdate);
            DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(year, month, day);
                    fromdate = calendar.getTime();
                    from.setText(Util.formatDateToString(fromdate,"MM/dd/yyyy" ));
                    try {                filterIncomes(fromdate, todate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, calendar);
        }else if(view.getId() == R.id.toDateI) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(todate);
            DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(year, month, day);
                    todate = calendar.getTime();
                    to.setText(Util.formatDateToString(todate,"MM/dd/yyyy" ));
                    try {
                        filterIncomes(fromdate, todate);

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
        TextView text = (TextView) view.findViewById(R.id.useridIncome);
        Bundle bundle = new Bundle();
        bundle.putString("userid", text.getText().toString());
        UpdateIncomeFragment fragment = new UpdateIncomeFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Update Income");
    }
}
