package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

import javax.swing.*;

import appModel.FloatFormatter;

import domainModel.LightsManager;
import domainModel.Light;

@SuppressWarnings("serial")
public class LightEditDialog extends JDialog implements ActionListener {
	Component parent;
	Light light;
	LightsManager model;
	String action;
	final String cancelCom = "Cancel";
	final String addCom = "Add";
	final String editCom = "Edit";
	final Font nameFont = new Font("Default", Font.BOLD, 24);
	String origName;
	float[] origPos = new float[3];
	Color origColor;

	JTextField nameField;
	JFormattedTextField[] posFields;

	public LightEditDialog(Component parent, LightsManager model, int index) {
		setModal(true);
		this.parent = parent;
		this.model = model;
		this.light = (index != -1)? model.getLight(index) : null;
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(0, 20));
		//this.setPreferredSize(new Dimension(260,350));
		this.setResizable(false);

		// determine if we are making a new object or editing an existing one
		if (light==null) {
			action = addCom;
			// set initial values for add
			origName = "Name";
			for (int i=0; i<3; i++) {
				origPos[i] = 0f;
			}
			origColor = new Color(0.7f, 0.7f, 0.7f);
		}
		else {
			action = editCom;
			// load previous values
			origName = light.name;
			origPos[0] = light.pos.x;
			origPos[1] = light.pos.y;
			origPos[2] = light.pos.z;
			origColor = new Color(light.c.x, light.c.y, light.c.z);
		}
		this.setTitle(action+" Light");

		// add the components
		Dimension hoizFiller = new Dimension(10, 0);
		contentPane.add(createButtons(), BorderLayout.SOUTH);
		contentPane.add(createNameField(), BorderLayout.NORTH);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.WEST);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.EAST);

		contentPane.add(createLightFields());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	public static Light createAddDialog(Component parent, LightsManager model) {
		LightEditDialog dialog = new LightEditDialog(parent, model, -1);
		return dialog.light;
	}
	public static Light createEditDialog(Component parent, LightsManager model, int index) {
		LightEditDialog dialog = new LightEditDialog(parent, model, index);
		return dialog.light;
	}

	public void actionPerformed(ActionEvent e) {
		// Changes Accepted
		if (action.equals(e.getActionCommand())){
			// Parse the input values
			String name = nameField.getText();

			float[] pos = new float[3];
			float[] color = origColor.getColorComponents(null);
			for (int i=0; i<3; i++) {
				try {
					posFields[i].commitEdit();
				}
				catch (ParseException ex) {
					return;
				}
				pos[i] = (Float)(posFields[i].getValue());
			}
			// Add new
			if (action.equals(addCom)) light = model.addLight(name, pos[0], pos[1], pos[2], color[0], color[1], color[2]);
			// Edit existing
			else model.editLight(light, name, pos[0], pos[1], pos[2], color[0], color[1], color[2]);
		}
		setVisible(false);
		dispose();
	}

	private Component createNameField() {
		// name field
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		nameField = new JTextField(9);
		nameField.setText(origName);
		nameField.setFont(nameFont);
		nameField.setHorizontalAlignment(JTextField.CENTER);
		panel.add(nameField);
		return panel;
	}

	private JComponent createButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		String actionLabel = ("Edit".equals(action)) ? "Save" : action;
		JButton acceptBtn = new JButton(actionLabel);
		getRootPane().setDefaultButton(acceptBtn); // make this the default button
		acceptBtn.setActionCommand(action);
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand(cancelCom);
		panel.add(cancelBtn);
		panel.add(acceptBtn);
		acceptBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		return panel;
	}

	private JComponent createLightFields() {
		final JPanel panel = new JPanel(new GridBagLayout());
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

		String[] posLabelStrings = { "x: ", "y: ", "z: " };
		JLabel[] posLabels = new JLabel[posLabelStrings.length];

		FloatFormatter floatFormat = new FloatFormatter();

		// Create the other fields, associate label/field pairs, add everything, and lay it out.
		posFields = new JFormattedTextField[3]; // x, y, z
		for (int i = 0; i < 3; i++) {
			posLabels[i] = new JLabel(posLabelStrings[i], JLabel.TRAILING);
			
			posFields[i] = new JFormattedTextField(floatFormat);
			posFields[i].setColumns(4);
			posFields[i].setFocusLostBehavior(JFormattedTextField.COMMIT);
			posFields[i].setValue(origPos[i]);

			posLabels[i].setLabelFor(posFields[i]);
			panel.add(posLabels[i], labelCol);
			labelCol.gridy++;
			panel.add(posFields[i], fieldCol);
			fieldCol.gridy++;
		}
		
		// Color preview section
		JLabel colorLabel = new JLabel("Color: ", JLabel.TRAILING);
		colorLabel.setToolTipText("The color also controls the brightness of the light");
		JPanel colorSection = new JPanel();
		final JPanel preview = new JPanel();
		preview.setPreferredSize(new Dimension(50, 50));
		preview.setBackground(origColor);
		colorSection.add(preview);
		JButton changeBtn = new JButton("Change");
		changeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(panel, "Select Light Color", origColor);
				if (newColor != null) { // OK was pressed
					origColor = newColor;
				    preview.setBackground(origColor);
				}
			}
		});
		colorSection.add(changeBtn);
				
		colorLabel.setLabelFor(colorSection);
		panel.add(colorLabel, labelCol);
		panel.add(colorSection, fieldCol);
		
		return panel;
	}
}
