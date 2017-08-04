package com.expensemanager;

import android.preference.PreferenceManager;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Util {

    public static String formatDateToString(Date date, String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static String getCurrentDateFormat() {
        return PreferenceManager.getDefaultSharedPreferences(ExpenseManagerApp.getContext()).
                getString(ExpenseManagerApp.getContext().getString(R.string.date_format_key),
                        ExpenseManagerApp.getContext().getString(R.string.default_date_format));
    }

    public static List<Integer> getListColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        return colors;
    }
}
