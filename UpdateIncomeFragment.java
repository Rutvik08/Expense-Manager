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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

public class UpdateIncomeFragment extends Fragment implements View.OnClickListener{
    private Date selectedDate;
    private EditText updateIDate;
    String userID;
    SQLiteDatabase db;
    private EditText uiTotal,uiDescription;

    public UpdateIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_update_income, container, false);
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        Bundle bundle = this.getArguments();
        userID = bundle.getString("userid");
        updateIDate = (EditText) rootView.findViewById(R.id.updateIdate);
        uiTotal = (EditText) rootView.findViewById(R.id.updateItotal);
        uiDescription = (EditText) rootView.findViewById(R.id.updateIdescription);
        updateIDate.setText(Util.formatDateToString(new Date(),"MM/dd/yyyy"));
        selectedDate = new Date();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM income WHERE id=?", new String[] {userID + ""});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                String date = cursor.getString(cursor.getColumnIndex("date"));
                updateIDate.setText(date);
                uiDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
                uiTotal.setText(cursor.getString(cursor.getColumnIndex("total")));
            }
        }finally {
            cursor.close();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateIDate.setOnClickListener(this);
        (getView().findViewById(R.id.updateIcancel)).setOnClickListener(this);
        (getView().findViewById(R.id.updateIsave)).setOnClickListener(this);
        (getView().findViewById(R.id.updateIdelete)).setOnClickListener(this);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateDate() {
        updateIDate.setText(Util.formatDateToString(selectedDate,"MM/dd/yyyy" ));
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        String title = "";
        if(view.getId() == R.id.updateIdate) {
            showDateDialog();
        } else if (view.getId() == R.id.updateIcancel) {
            fragment = new IncomeListFragment();
            title = getString(R.string.title_income);
        } else if (view.getId() == R.id.updateIsave) {
            validate();
        } else if (view.getId() == R.id.updateIdelete) {
            delete();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    public String viewToString(View v) {
        EditText view = (EditText) v;
        return view.getText().toString();
    }

    public void delete(){
        String table = "income";
        String whereClause = "id=?";
        String[] whereArgs = new String[] { String.valueOf(userID) };
        db.delete(table, whereClause, whereArgs);
        Fragment fragment = new IncomeListFragment();
        String title = getString(R.string.title_income);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    public void validate() {
        if (viewToString(uiDescription).length() == 0) {
            uiDescription.requestFocus();
            uiDescription.setError("Please Enter Income Source");
        } else if (viewToString(uiTotal).length() == 0) {
            uiTotal.requestFocus();
            uiTotal.setError("Please Enter Amount");
        } else if (uiDescription.getError() == null && uiTotal.getError() == null ) {
            try {
                updateIncome();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Enter valid Data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIncome() {
        ContentValues contentValues = new ContentValues(1);
       contentValues.put("description", String.valueOf(uiDescription.getText()));
        contentValues.put("total", String.valueOf(uiTotal.getText()));
        contentValues.put("date", String.valueOf(updateIDate.getText()));
        db.update("income", contentValues, "id="+userID, null);
        Fragment fragment = new IncomeListFragment();
        String title = getString(R.string.title_income);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
