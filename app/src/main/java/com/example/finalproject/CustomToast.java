package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;

public class CustomToast {
    public static void showErrorToast(Context context, String message, String toastText,int duration) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_error, null);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(toastText);
        toast.setView(view);
        toast.show();
    }
}
