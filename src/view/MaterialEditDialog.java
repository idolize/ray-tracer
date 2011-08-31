package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import appModel.FloatFormatter;
import domainModel.Material;
import domainModel.MaterialsManager;
import domainModel.RayTracer;

@SuppressWarnings("serial")
public class MaterialEditDialog extends JDialog implements ActionListener {
	Component parent;
	RayTracer preview;
	RayTracerCanvas previewCanvas;
	Material material;
	MaterialsManager model;
	String action;
	final String cancelCom = "Cancel";
	final String addCom = "Add";
	final String editCom = "Edit";
	final Font nameFont = new Font("Default", Font.BOLD, 24);
	String nameVal;
	Color color;
	float[] sliderVals = new float[3]; // Kd,Ks, Krefl
	float pVal;
	float[] KdArray, KaArray, KsArray;

	JTextField nameField;
	JSlider[] sliders;
	JFormattedTextField pField;

	public MaterialEditDialog(Component parent, MaterialsManager model, int index) {
		setModal(true);
		this.parent = parent;
		this.model = model;
		this.material = (index != -1)? model.getMaterial(index) : null;
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(0, 20));
		//this.setPreferredSize(new Dimension(400,600));
		this.setResizable(false);

		// determine if we are making a new object or editing an existing one
		if (material==null) {
			action = addCom;
			// set initial values for add
			nameVal = "Name";
			sliderVals[0] = 0.75f;
			sliderVals[1] = 0.75f;
			pVal = 20;
			sliderVals[2] = 0;
			color = new Color(0.3f, 0.3f, 0.3f);
			
		}
		else {
			action = editCom;
			// load previous values
			nameVal = material.name;
			sliderVals[0] = material.Kd.x; // assumes no color in diffusion
			sliderVals[1] = material.Ks.x; // assumes no color in specular
			pVal = material.p;
			sliderVals[2] = material.Krefl;
			color = new Color(material.Ka.x, material.Ka.y, material.Ka.z);
		}
		KdArray = new float[]{ sliderVals[0], sliderVals[0], sliderVals[0] };
		KaArray = color.getColorComponents(null);
		KsArray = new float[]{ sliderVals[1], sliderVals[1], sliderVals[1] };
		
		this.setTitle(action+" Material");

		// add the components
		Dimension hoizFiller = new Dimension(10, 0);
		contentPane.add(createButtons(), BorderLayout.SOUTH);
		contentPane.add(createNameField(), BorderLayout.NORTH);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.WEST);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.EAST);

		JComponent entryFields = createMaterialFields();
		contentPane.add(entryFields);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public static Material createAddDialog(Component parent, MaterialsManager model) {
		MaterialEditDialog dialog = new MaterialEditDialog(parent, model, -1);
		return dialog.material;
	}
	
	public static Material createEditDialog(Component parent, MaterialsManager model, int index) {
		MaterialEditDialog dialog = new MaterialEditDialog(parent, model, index);
		return dialog.material;
	}

	public void actionPerformed(ActionEvent e) {
		// Changes Accepted
		if (action.equals(e.getActionCommand())){
			// Parse the input values
			if (!updateVals()) return;
			// Add new
			if (action.equals(addCom)) material = model.addMaterial(nameVal, KdArray, KaArray, KsArray, pVal, sliderVals[2], false);
			// Edit existing
			else model.editMaterial(material, nameVal, KdArray, KaArray, KsArray, pVal, sliderVals[2]);
		}
		setVisible(false);
		dispose();
	}

	private Component createNameField() {
		// name field
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		nameField = new JTextField(12);
		nameField.setText(nameVal);
		nameField.setFont(nameFont);
		nameField.setHorizontalAlignment(JTextField.CENTER);
		if (material == model.defaultMaterial) nameField.setEnabled(false);
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
	
	private JComponent createPreview() {
		// RayTracer preview
		JPanel panel = new JPanel();
		preview = new RayTracer(100, 100, 60, 1, 0.2f, 0.2f, 1);
		panel.setBackground(new Color(preview.bgColor.x, preview.bgColor.y, preview.bgColor.z));
		previewCanvas = new RayTracerCanvas(this, preview);
		updatePreview();
		preview.lightsManager.addLight("PREVIEW", 8.0f, 8.0f, 3.0f, 0.7f, 0.7f, 0.7f);
		preview.shapesManager.addSphere("PREVIEW", 1.0f, 0, 0, -2.2f, preview.materialsManager.defaultMaterial);
		panel.add(previewCanvas);
		return panel;
	}

	private JComponent createMaterialFields() {
		final JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints labelCol = new GridBagConstraints();
		Insets rightPadding = new Insets(0,0,0,20);
		labelCol.weighty = 1.0;
		labelCol.gridx = 0;
		labelCol.gridy = 0;
		labelCol.insets = rightPadding;
		labelCol.anchor = GridBagConstraints.EAST;
		GridBagConstraints fieldCol = new GridBagConstraints();
		fieldCol.weighty = 1.0;
		fieldCol.gridx = 1;
		fieldCol.gridy = 0;
		fieldCol.fill = GridBagConstraints.HORIZONTAL;
		
		// Add the preview
		JLabel previewLabel = new JLabel("Preview: ", JLabel.TRAILING);
		panel.add(previewLabel, labelCol);
		labelCol.gridy++;
		panel.add(createPreview(), fieldCol);
		fieldCol.gridy++;

		String[] sliderLabelStrings = { "Diffuse Contribution: ", "Specular Contribution: ", "Reflectiveness: " };
		String[] sliderTooltips = {
				"This controls the matte lighting applied to the surface",
				"This controls the specular (or 'spot') lighting applied to the surface",
				"This controls how much the surface reflects light from the background as well as other objects"
				};
		JLabel[] sliderLabels = new JLabel[sliderLabelStrings.length];

		// Color editor
		JLabel colorLabel = new JLabel("Ambient Color: ", JLabel.TRAILING);
		colorLabel.setToolTipText("The ambient color also controls brightness, so a bright ambient color can never be darker than the color selected." +
				"\nWhen a light is applied on the ambient color it will get brighter in those spots.");
		JPanel colorSection = new JPanel();
		final JPanel colorPreview = new JPanel();
		colorPreview.setPreferredSize(new Dimension(50, 50));
		colorPreview.setBackground(color);
		colorSection.add(colorPreview);
		JButton changeBtn = new JButton("Change");
		changeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(panel, "Select New Color", color);
				if (newColor != null) { // OK was pressed
					color = newColor;
					colorPreview.setBackground(color);
					updateVals();
					updatePreview();
				}
			}
		});
		colorSection.add(changeBtn);
		colorLabel.setLabelFor(colorSection);
		panel.add(colorLabel, labelCol);
		labelCol.gridy++;
		panel.add(colorSection, fieldCol);
		fieldCol.gridy++;

		// Sliders
		ChangeListener sliderListener = new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				JSlider theJSlider = (JSlider) e.getSource();
				if (!theJSlider.getValueIsAdjusting()) {
					updateVals();
					updatePreview();
				}
			}
		};
		sliders = new JSlider[3];
		for (int i=0; i<sliders.length; i++) {
			int initialVal = (int)(sliderVals[i]*100.0f);
			sliders[i] = new JSlider(JSlider.HORIZONTAL, 0, 100, initialVal);
			sliders[i].addChangeListener(sliderListener);
			sliders[i].setMajorTickSpacing(50);
			sliders[i].setMinorTickSpacing(10);
			sliders[i].setPaintTicks(true);
			sliders[i].setPaintLabels(true);

			sliderLabels[i] = new JLabel(sliderLabelStrings[i]);
			sliderLabels[i].setLabelFor(sliders[i]);
			sliderLabels[i].setToolTipText(sliderTooltips[i]);
			panel.add(sliderLabels[i], labelCol);
			labelCol.gridy++;
			panel.add(sliders[i], fieldCol);
			fieldCol.gridy++;
		}

		// Phong
		JLabel pLabel = new JLabel("Phong Coefficient: ", JLabel.TRAILING);
		pLabel.setToolTipText("The Phong Coefficient controls the 'sharpness' of the specular highlight. Smaller values are more matte while larger values are more glossy.");
		pField = new JFormattedTextField(new FloatFormatter(0, Float.MAX_VALUE));
		pField.setColumns(4);
		pField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		pField.setValue(pVal);
		pField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) { if (updateVals()) updatePreview(); }
			public void keyTyped(KeyEvent e) {}
		});
		pLabel.setLabelFor(pField);
		panel.add(pLabel, labelCol);
		panel.add(pField, fieldCol);

		return panel;
	}
	
	private boolean updateVals() {
		nameVal = nameField.getText();
		for (int i = 0; i < 3; i++){
			sliderVals[i] = ((float)sliders[i].getValue())/100.0f;
		}
		KdArray[0] = KdArray[1] = KdArray[2] = sliderVals[0];
		color.getColorComponents(KaArray);
		KsArray[0] = KsArray[1] = KsArray[2] = sliderVals[1];
		try {
			pField.commitEdit();
		}
		catch (ParseException ex) {
			return false;
		}
		pVal = (Float)(pField.getValue());
		return true;
	}
	
	private void updatePreview() {
		preview.materialsManager.editMaterial(preview.materialsManager.defaultMaterial, "Default", KdArray, KaArray, KsArray, pVal, sliderVals[2]);
	}
}
