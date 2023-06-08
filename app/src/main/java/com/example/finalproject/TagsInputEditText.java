package com.example.finalproject;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.Objects;

public class TagsInputEditText extends TextInputEditText {
    TextWatcher textWatcher;
    String lastString;
    String separator = " ";

    public TagsInputEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String thisString = editable.toString();
                if (thisString.length() > 0 && !thisString.equals(lastString)) {
                    format();
                }
            }
        };

        addTextChangedListener(textWatcher);
    }

    private void format() {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String fullString = Objects.requireNonNull(getText()).toString();

        String[] strings = fullString.split(separator);

        for(int i = 0; i < strings.length; i++) {
            String s = strings[i];
            stringBuilder.append(s);

            if (fullString.charAt(fullString.length() - 1) != separator.charAt(0) && i == strings.length - 1) {
                break;
            }

            BitmapDrawable bitmapDrawable = (BitmapDrawable) convertViewToDrawable(createTokenView(s));
            bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

            int startIdx = stringBuilder.length() - (s.length());
            int endIdx = stringBuilder.length();

            ClickableSpan span = new ClickableSpan(startIdx, endIdx);
            stringBuilder.setSpan(span, Math.max(endIdx - 2, startIdx), endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            stringBuilder.setSpan(new ImageSpan(bitmapDrawable), startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (i < strings.length - 1) {
                stringBuilder.append(separator);
            } else if (fullString.charAt(fullString.length() - 1) == separator.charAt(0)) {
                stringBuilder.append(separator);
            }
        }
        lastString = stringBuilder.toString();

        setText(stringBuilder);
        setSelection(stringBuilder.length());
    }

    public View createTokenView(String text) {
        Context context = getContext();

        // Create a LinearLayout to hold the token view
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);

        // Create a Chip view
        Chip chip = new Chip(context);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the close icon click event
                layout.removeView(chip);
            }
        });

        // Set background color and text color for the Chip view
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
        chip.setTextColor(Color.BLACK);

        // Add the Chip view to the LinearLayout
        layout.addView(chip);

        return layout;
    }

    public Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(canvas);
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        Bitmap viewBitmap = cache.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getContext().getResources(), viewBitmap);
    }

    private class ClickableSpan extends android.text.style.ClickableSpan {
        int startIdx;
        int endIdx;

        public ClickableSpan(int startIdx, int endIdx) {
            super();
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void onClick(@NonNull View view) {
            String s = Objects.requireNonNull(getText()).toString();
            String s1 = s.substring(0, startIdx);
            String s2 = s.substring(Math.min(endIdx + 1, s.length() - 1));
            TagsInputEditText.this.setText(MessageFormat.format("{0}{1}", s1, s2));
        }
    }
}
