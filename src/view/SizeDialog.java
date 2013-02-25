package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import appModel.IntFormatter;

import domainModel.RayTracer;

@SuppressWarnings("serial")
public class SizeDialog extends JDialog implements ActionListener {
	RayTracer model;
	Component parent;
	final String saveCom = "Change";
	final String cancelCom = "Cancel";
	int widthVal, heightVal, fovVal;
	JFormattedTextField widthField, heightField;
	JSlider fovSlider;

	public SizeDialog(Component parent, RayTracer model) {
		setModal(true);
		setResizable(false);
		setTitle("Change Dimensions");
		this.model = model;
		this.parent = parent;
		Container contentPane = getContentPane();
		
		// Load initial values
		widthVal = model.width;
		heightVal = model.height;
		fovVal = model.fov;

		// Add GUI elements
		Dimension hoizFiller = new Dimension(10, 0);
		contentPane.add(createButtons(), BorderLayout.SOUTH);
		contentPane.add(createFields());
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.WEST);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.EAST);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private JComponent createButtons() {
		final JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton cancelBtn = new JButton(cancelCom);
		cancelBtn.setActionCommand(cancelCom);

		JButton acceptBtn = new JButton(saveCom);
		acceptBtn.setActionCommand(saveCom);
		getRootPane().setDefaultButton(acceptBtn); // make this the default button

		panel.add(cancelBtn);
		panel.add(acceptBtn);
		acceptBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		return panel;
	}

	private JComponent createFields() {
		final JPanel panel = new JPanel(new GridBagLayout());
		
		// Set up layout constraints
		GridBagConstraints labelCol = new GridBagConstraints();
		Insets rightPadding = new Insets(0,0,0,20);
		labelCol.weightx = 0.5;
		labelCol.gridx = 0;
		labelCol.gridy = 0;
		labelCol.insets = rightPadding;
		labelCol.anchor = GridBagConstraints.EAST;
		GridBagConstraints fieldCol = new GridBagConstraints();
		fieldCol.weightx = 1.0;
		fieldCol.gridx = 1;
		fieldCol.gridy = 0;
		fieldCol.fill = GridBagConstraints.HORIZONTAL;

		IntFormatter posIntFormat = new IntFormatter(1, Integer.MAX_VALUE);

		// Dimension fields
		JLabel dimensionsLabel = new JLabel("Dimensions: ");
		JPanel dimensions = new JPanel();
		widthField = new JFormattedTextField(posIntFormat);
		widthField.setColumns(4);
		widthField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		widthField.setValue(widthVal);
		dimensions.add(widthField);
		dimensions.add(new JLabel(" by "));
		heightField = new JFormattedTextField(posIntFormat);
		heightField.setColumns(4);
		heightField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		heightField.setValue(heightVal);
		dimensions.add(heightField);
		dimensionsLabel.setLabelFor(dimensions);
		panel.add(dimensionsLabel, labelCol);
		labelCol.gridy++;
		panel.add(dimensions, fieldCol);
		fieldCol.gridy++;

		// Field-of-view slider
		JLabel fovLabel = new JLabel("Field of view: ");
		fovSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, fovVal);
		fovSlider.setMajorTickSpacing(90);
		fovSlider.setMinorTickSpacing(10);
		fovSlider.setPaintTicks(true);
		fovSlider.setPaintLabels(true);
		fovLabel.setLabelFor(fovSlider);
		panel.add(fovLabel, labelCol);
		panel.add(fovSlider, fieldCol);

		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(saveCom)) {
			// Validate the input
			try {
				widthField.commitEdit();
				heightField.commitEdit();
			}
			catch (ParseException ex) {
				return;
			}
			widthVal = (Integer) widthField.getValue();
			heightVal = (Integer) heightField.getValue();
			fovVal = fovSlider.getValue();
			// Change the model
			model.alterDimensions(widthVal, heightVal, fovVal);
		}
		setVisible(false);
		dispose();
	}
}
