package edu.uab.cvc.huntingwords.screens;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by carlosb on 10/04/18.
 */

public class Utils {

    public static int GetBackgroundColour(Context context) {
        TypedValue a = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        return a.data;
    }
}
