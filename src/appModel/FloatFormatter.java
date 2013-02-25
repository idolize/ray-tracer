package appModel;

import java.awt.Color;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import view.MainGUI;

@SuppressWarnings("serial")
public class FloatFormatter extends AbstractFormatter {
	float min, max;

	public FloatFormatter() {
		this.min = -Float.MAX_VALUE;
		this.max = Float.MAX_VALUE;
	}

	public FloatFormatter(float min, float max) {
		// set clipping values
		this.min = min;
		this.max = max;
	}

	public Float stringToValue(String text) throws ParseException {
		float temp;
		JFormattedTextField parent = getFormattedTextField();
		try {
			temp = Float.parseFloat(text);
			if (temp <= min) temp = min;
			else if (temp >= max) temp = max;
			parent.setBackground(Color.WHITE);
		}
		catch(NumberFormatException ex) {
			temp = Float.NaN;
			parent.setBackground(MainGUI.errorColor);
			invalidEdit();
			throw new ParseException("Not a valid number", hashCode());
		}
		return temp;
	}
	
	public String valueToString(Object value) throws ParseException {
		if (!(value instanceof Float)) throw new ParseException("Trying to convert a non-float to a String", this.hashCode());
		if (Float.isNaN((Float)value)) return "";
		return Float.toString((Float)value);
	}
}