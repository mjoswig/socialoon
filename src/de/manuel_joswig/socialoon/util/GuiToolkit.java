package de.manuel_joswig.socialoon.util;

import android.graphics.Color;
import android.widget.EditText;

/**
 * Useful methods to work with GUI elements
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class GuiToolkit {
	public static void markEditTextAsError(EditText editTextObject, boolean isError) {
		if (isError) {
			editTextObject.setBackgroundColor(Color.rgb(255, 204, 204));
		}
		else {
			editTextObject.setBackgroundColor(Color.rgb(217, 217, 217));
		}
	}
	
	public static boolean isEditTextEmpty(EditText editTextObject) {
		return (editTextObject.getText().toString().trim().length() == 0);
    }
}
