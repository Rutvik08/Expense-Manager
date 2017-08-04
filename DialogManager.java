package com.expensemanager;


import android.app.DatePickerDialog;
import android.content.Context;
import java.util.Calendar;
import java.util.Date;

public class DialogManager {

    private static DialogManager ourInstance = new DialogManager();
    public static DialogManager getInstance() {
        return ourInstance;
    }
    private DialogManager() {
    }
    public void showDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener dateSetListener, Calendar calendar) {
        showDatePicker(context, dateSetListener, calendar,null, null);
    }
    public void showDatePicker(Context context, DatePickerDialog.OnDateSetListener dateSetListener, Calendar calendar, Date minDate, Date maxDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (minDate != null) datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        if (maxDate != null) datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        datePickerDialog.show();
    }
}
