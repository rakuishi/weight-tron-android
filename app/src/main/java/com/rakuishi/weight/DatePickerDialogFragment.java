package com.rakuishi.weight;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.threeten.bp.LocalDateTime;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String DATETIME = "localDateTime";

    public static DatePickerDialogFragment newInstance(LocalDateTime localDateTime) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATETIME, localDateTime);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDateTime localDateTime = (LocalDateTime) getArguments().getSerializable(DATETIME);

        DatePickerDialog.OnDateSetListener callback;
        if (getActivity() instanceof DatePickerDialog.OnDateSetListener) {
            callback = (DatePickerDialog.OnDateSetListener) getActivity();
        } else {
            throw new IllegalStateException("The parent activity must set DatePickerDialog.OnDateSetListener");
        }

        return new DatePickerDialog(
                getContext(),
                callback,
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth()
        );
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {

    }
}
