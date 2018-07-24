package com.example.shohel.geopoll.classes;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shohel.geopoll.ProfileStep1;
import com.example.shohel.geopoll.R;


import java.util.Date;

/**
 * Created by jahid on 12/10/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    int newDate;
    Date date;
    int year;
    int month;
    int day;
    public static int age;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        //final Calendar c = Calendar.getInstance();
        final Calendar c;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            c = Calendar.getInstance();
            date = new Date();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            newDate = c.get(Calendar.YEAR);
            Log.e("month",newDate+" "+year);
        }

        /*Date date = new Date();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        newDate = c.get(Calendar.YEAR);
        Log.e("month",newDate+" "+year);*/
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Log.e("month",newDate+" "+year);
        age = newDate - year;
        Log.e("month",age+" "+newDate+" "+year);
        EditText tv1 = (EditText) getActivity().findViewById(R.id.input_date_of_birth);
        if (age>=16) {

            tv1.setText("" + view.getDayOfMonth() + "/" + (view.getMonth() + 1) + "/" + view.getYear());
        } else {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Sorry!");
            dialogBuilder.setMessage("You are not eligible! \n16+ age is the minimum eligibility to participate!");
            dialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();

                }
            });
            dialogBuilder.setIcon(R.drawable.paypoll_symble);
            dialogBuilder.show();
            tv1.setError("16+ age is the minimum eligibility to participate! ");
        }

    }
}