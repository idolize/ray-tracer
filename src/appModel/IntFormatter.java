package appModel;

import java.awt.Color;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import view.MainGUI;

@SuppressWarnings("serial")
public class IntFormatter extends AbstractFormatter {
	int min, max;
	boolean NaN = false;
	
	public IntFormatter() {
		this.min = Integer.MIN_VALUE;
		this.max = Integer.MAX_VALUE;
	}
	
	public IntFormatter(int min, int max) {
		// set clipping values
		this.min = min;
		this.max = max;
	}

	public Object stringToValue(String text) throws ParseException {
		int temp;
		JFormattedTextField parent = getFormattedTextField();
		try {
			temp = Integer.parseInt(text);
			NaN = false;
			if (temp <= min) temp = min;
			else if (temp >= max) temp = max;
			parent.setBackground(Color.WHITE);
		}
		catch(NumberFormatException ex) {
			NaN = true;
			parent.setBackground(MainGUI.errorColor);
			invalidEdit();
			throw new ParseException("Not a valid number", hashCode());
		}
		return temp;
	}

	public String valueToString(Object value) throws ParseException {
		if (!(value instanceof Integer)) throw new ParseException("Trying to convert a non-integer to a String", this.hashCode());
		if (NaN) return "";
		return Integer.toString((Integer)value);
	}
}