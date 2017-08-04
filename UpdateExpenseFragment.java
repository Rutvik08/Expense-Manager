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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateExpenseFragment extends Fragment implements View.OnClickListener{
    private Date selectedDate;
    private EditText updateDate,uTotal,uDescription;
    private Spinner uCategory;
    String userID;
    SQLiteDatabase db;

    public UpdateExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_expense, container, false);
        db = getContext().openOrCreateDatabase("appdatabase", Context.MODE_PRIVATE, null);
        Bundle bundle = this.getArguments();
        userID = bundle.getString("userid");
        getEmployeeName(userID);
        updateDate = (EditText) rootView.findViewById(R.id.updatedate);
        uDescription = (EditText) rootView.findViewById(R.id.updatedescription);
        uTotal = (EditText) rootView.findViewById(R.id.updatetotal);
        updateDate.setText(Util.formatDateToString(new Date(),"MM/dd/yyyy"));
        selectedDate = new Date();
        uCategory = (Spinner) rootView.findViewById(R.id.updatecategories);
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
        uCategory.setAdapter(dataAdapter);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM expense WHERE id=?", new String[] {userID + ""});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                String date = cursor.getString(cursor.getColumnIndex("date"));
                updateDate.setText(date);
                uDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
                uTotal.setText(cursor.getString(cursor.getColumnIndex("total")));
                uCategory.setSelection(getIndex(uCategory, cursor.getString(cursor.getColumnIndex("type"))));
            }
        }finally {
            cursor.close();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateDate.setOnClickListener(this);
        (getView().findViewById(R.id.updatecancel)).setOnClickListener(this);
        (getView().findViewById(R.id.updatesave)).setOnClickListener(this);
        (getView().findViewById(R.id.updatedelete)).setOnClickListener(this);
    }

    public void getEmployeeName(String id) {

    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
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
        updateDate.setText(Util.formatDateToString(selectedDate,"MM/dd/yyyy" ));
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
        if(view.getId() == R.id.updatedate) {
            showDateDialog();
        } else if (view.getId() == R.id.updatecancel) {
            fragment = new ExpenseListFragment();
            title = getString(R.string.title_expense);
        } else if (view.getId() == R.id.updatesave) {
            validate();
        } else if (view.getId() == R.id.updatedelete) {
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

    public void delete(){
        String table = "expense";
        String whereClause = "id=?";
        String[] whereArgs = new String[] { String.valueOf(userID) };
        db.delete(table, whereClause, whereArgs);
        Fragment fragment = new ExpenseListFragment();
        String title = getString(R.string.title_expense);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    public void validate() {
        if (viewToString(uTotal).length() == 0) {
            uTotal.requestFocus();
            uTotal.setError("Please Enter Total");
        }
        else if (uCategory.getSelectedItemPosition() == 0) {
            TextView i = (TextView) uCategory.getSelectedView();
            i.setError("Select Type");
            uCategory.requestFocus();
        }else if (uDescription.getError() == null && uTotal.getError() == null ) {
            try {
                updateExpense();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Enter valid Data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateExpense() {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("id",userID);
        contentValues.put("type", uCategory.getSelectedItem().toString());
        contentValues.put("description", String.valueOf(uDescription.getText()));
        contentValues.put("total", String.valueOf(uTotal.getText()));
        contentValues.put("date", String.valueOf(updateDate.getText()));
        db.update("expense", contentValues, "id="+userID, null);
        Fragment fragment = new ExpenseListFragment();
        String title = getString(R.string.title_expense);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}