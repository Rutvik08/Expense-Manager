package com.expensemanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.expensemanager.entities.Category;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ExpenseFragment extends Fragment  implements View.OnClickListener {
    private Date selectedDate;
    private EditText eEditDate,eTotal,eDescription;
    private Spinner eCategory;
    private TextView userid, mode;
    String modeEx;
    ArrayList<Category> category   = new ArrayList<Category>();
    SQLiteDatabase db;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_expense, container, false);
        userid = (TextView) getActivity().findViewById(R.id.userid);
        mode = (TextView) getActivity().findViewById(R.id.expensemode);
        modeEx = mode.getText().toString();
        eEditDate = (EditText) rootView.findViewById(R.id.ebtn_date);
        eDescription = (EditText) rootView.findViewById(R.id.eet_description);
        eTotal = (EditText) rootView.findViewById(R.id.eet_total);
        eEditDate.setText(Util.formatDateToString(new Date(),"MM/dd/yyyy"));
        selectedDate = new Date();
        eCategory = (Spinner) rootView.findViewById(R.id.sp_categories);
        ArrayList<String> categories =  new ArrayList<>();
        categories.add("Select Type");
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,categories );
        eCategory.setAdapter(dataAdapter);

        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + "expense" + " (" + "id" + " INTEGER PRIMARY KEY," + "type" + " TEXT,"+ "userid" + " TEXT,"
                + "description" + " TEXT," + "total" + " NUMERIC," +"date" + " TEXT" + ");");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eEditDate.setOnClickListener(this);
        (getView().findViewById(R.id.ebtn_cancel)).setOnClickListener(this);
        (getView().findViewById(R.id.ebtn_save)).setOnClickListener(this);
    }

    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                selectedDate = calendar.getTime();
                updateDate();
            }
        }, calendar);
    }

    private void updateDate() {
        eEditDate.setText(Util.formatDateToString(selectedDate,"MM/dd/yyyy" ));
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
        if(view.getId() == R.id.ebtn_date) {
            showDateDialog();
        } else if (view.getId   () == R.id.ebtn_cancel) {
            if(modeEx.equals("fromHome")){
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                modeEx="";
            }
            else if(modeEx.equals("fromList")){
                fragment = new ExpenseListFragment();
                title = getString(R.string.title_expense);
                modeEx="";
            }
        } else if (view.getId() == R.id.ebtn_save) {
            validate();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }

    }

    public int getDatabaseLastRecordExpense() {
        int databaseLast=0;
        Cursor mCursor = db.rawQuery("SELECT id FROM expense ORDER BY ID DESC LIMIT 1;", null);
        if (mCursor.moveToFirst()) {
            databaseLast = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return databaseLast;
    }


    private void saveExpense() {

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("id",getDatabaseLastRecordExpense()+1 );
        contentValues.put("type", eCategory.getSelectedItem().toString());
        contentValues.put("description", String.valueOf(eDescription.getText()));
        contentValues.put("total", String.valueOf(eTotal.getText()));
        contentValues.put("date", String.valueOf(eEditDate.getText()));
        contentValues.put("userid",userid.getText().toString());
        db.insert("expense", null, contentValues);
        eCategory.setSelection(0);
        eDescription.setText("");
        eTotal.setText("");
        eEditDate.setText(Util.formatDateToString(new Date(),"MM/dd/yyyy"));
        Toast.makeText(getContext(),"Expense Saved",Toast.LENGTH_SHORT).show();
        Fragment fragment = new ExpenseListFragment();
        String title = getString(R.string.title_expense);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);

    }


    public void validate() {
        if (viewToString(eTotal).length() == 0) {
            eTotal.requestFocus();
            eTotal.setError("Please Enter Total");
        }
        else if (eCategory.getSelectedItemPosition() == 0) {
            TextView i = (TextView) eCategory.getSelectedView();
            i.setError("Select Type");
            eCategory.requestFocus();
        }else if (eDescription.getError() == null && eTotal.getError() == null ) {
            try {
                saveExpense();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Enter valid Data!", Toast.LENGTH_SHORT).show();
        }
    }


    public String viewToString(View v) {
        EditText view = (EditText) v;
        return view.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }

}
