package com.example.mega;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogUtils {
    public static void showSearchErrorDialog(Context context, String query) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AdaptiveDialogTheme);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_search_error, null);
        TextView message = dialogView.findViewById(R.id.errorMessage);
        message.setText(String.format("По запросу \"%s\" ничего не найдено", query));

        AlertDialog dialog = builder.setView(dialogView)
                .setPositiveButton("OK", (d, which) -> d.dismiss())
                .setCancelable(true)
                .create();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        dialog.show();
    }
}