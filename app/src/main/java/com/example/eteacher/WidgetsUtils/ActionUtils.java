package com.example.eteacher.WidgetsUtils;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.eteacher.R;
import com.google.android.material.snackbar.Snackbar;

public class ActionUtils {
    public static void ShowSnackBar(View layout, String message, Context context) {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();

        Snackbar snackbar;
        snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_primary));
        snackbar.show();
    }
}
