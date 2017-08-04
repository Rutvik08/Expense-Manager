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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;


public class IncomeFragment extends Fragment  implements View.OnClickListener {
    private Date selectedDate;
    private EditText btnDate;
    private TextView userid, mode;
    String modeEx;
    SQLiteDatabase db;

    private EditText eTotal,eDescription;

    public IncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + "income" + " (" + "id" + " INTEGER PRIMARY KEY," + "type" + " TEXT,"+ "userid" + " TEXT,"
                + "description" + " TEXT," + "total" + " NUMERIC," +"date" + " TEXT" + ");");
        View rootView = inflater.inflate(R.layout.fragment_income, container, false);
        btnDate = (EditText) rootView.findViewById(R.id.btn_date);
        eTotal = (EditText) rootView.findViewById(R.id.et_total);
        eDescription = (EditText) rootView.findViewById(R.id.et_description);
        userid = (TextView) getActivity().findViewById(R.id.userid);
        mode = (TextView) getActivity().findViewById(R.id.incomemode);
        modeEx = mode.getText().toString();
        btnDate.setText(Util.formatDateToString(new Date(),"MM/dd/yyyy"));
        selectedDate = new Date();
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnDate.setOnClickListener(this);
        (getView().findViewById(R.id.btn_cancel)).setOnClickListener(this);
        (getView().findViewById(R.id.btn_save)).setOnClickListener(this);
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
        btnDate.setText(Util.formatDateToString(selectedDate,"MM/dd/yyyy" ));
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
        if(view.getId() == R.id.btn_date) {
            showDateDialog();
        } else if (view.getId() == R.id.btn_cancel) {
            if(modeEx.equals("fromHome")){
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                modeEx="";
            }
            else if(modeEx.equals("fromList")){
                fragment = new IncomeListFragment();
                title = getString(R.string.title_income);
                modeEx="";
            }
        } else if (view.getId() == R.id.btn_save) {
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

    public int getDatabaseLastRecordIncome() {
        int databaseLast=0;
        Cursor mCursor = db.rawQuery("SELECT id FROM income ORDER BY ID DESC LIMIT 1;", null);
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
        contentValues.put("id",getDatabaseLastRecordIncome()+1 );
        contentValues.put("description", String.valueOf(eDescription.getText()));
        contentValues.put("total", String.valueOf(eTotal.getText()));
        contentValues.put("date", String.valueOf(btnDate.getText()));
        contentValues.put("userid",userid.getText().toString());
        db.insert("income", null, contentValues);
        eDescription.setText("");
        eTotal.setText("");
        btnDate.setText(Util.formatDateToString(new Date(),"MM/dd/yyyy"));
        Toast.makeText(getContext(),"Income Saved",Toast.LENGTH_SHORT).show();
        Fragment fragment = new IncomeListFragment();
        String title = getString(R.string.title_income);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    public String viewToString(View v) {
        EditText view = (EditText) v;
        return view.getText().toString();
    }

    public void validate() {
        if (viewToString(eDescription).length() == 0) {
            eDescription.requestFocus();
            eDescription.setError("Please Enter Income Source");
        } else if (viewToString(eTotal).length() == 0) {
            eTotal.requestFocus();
            eTotal.setError("Please Enter Amount");
        } else if (eDescription.getError() == null && eTotal.getError() == null ) {
            try {
                saveExpense();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Enter valid Data!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}



