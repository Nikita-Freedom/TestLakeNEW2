package com.example.testlake.core.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class Utils {

    public static final String HTTP_PREFIX = "http";

    public static int getAttributeColor(@NonNull Context context, int attributeId)
    {
        TypedArray a = context.obtainStyledAttributes(new TypedValue().data, new int[]{ attributeId });
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }
}
