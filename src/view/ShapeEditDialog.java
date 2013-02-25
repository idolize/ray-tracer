package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

import javax.swing.*;

import appModel.FloatFormatter;

import domainModel.Material;
import domainModel.MaterialsManager;
import domainModel.Shape;
import domainModel.ShapesManager;
import domainModel.Sphere;
import domainModel.Triangle;

@SuppressWarnings("serial")
public class ShapeEditDialog extends JDialog implements ActionListener {
	Shape shape;
	ShapesManager shapes;
	MaterialsManager materials;
	String action;
	String shapeType;
	final String cancelCom = "Cancel";
	final String addCom = "Add";
	final String editCom = "Edit";
	final String sphereType = "Sphere";
	final String triangleType = "Triangle";
	final Font nameFont = new Font("Default", Font.BOLD, 24);
	String origName;
	float[][] origVertices = new float[3][3];
	float[] origPos = new float[3];
	float origRadius;
	Material origMaterial;

	JTextField nameField;
	JFormattedTextField radiusField, xField, yField, zField;
	JFormattedTextField[][] vertexFields;
	
	GridBagConstraints labelCol, fieldCol;
	
	private ShapeEditDialog(Component parent, ShapesManager shapes, MaterialsManager materials, int index, String shapeType) {
		setModal(true);
		this.shapes = shapes;
		this.materials = materials;
		this.shape = (index != -1)? shapes.getShape(index) : null;
		this.shapeType = shapeType = (shapeType == null)? shape.getType() : shapeType;
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(0, 20));
		//this.setPreferredSize(new Dimension(260,370));
		this.setResizable(false);
		setupLayout();
		
		// determine if we are making a new object or editing an existing one
		if (shape==null) {
			action = addCom;
			// set initial values for add
			origName = "Name";
			origRadius = 1.0f;
			origPos[0] = origPos[1] = 0f;
			origPos[2] = -5.5f;
			for (int v=0; v<3; v++) {
				for (int i=0; i<3; i++) {
					origVertices[v][i] = origPos[i];
				}
			}
			origMaterial = materials.defaultMaterial;
		}
		else {
			action = editCom;
			// load previous values
			origName = shape.getName();
			if (shapeType.equals(sphereType)) {
				Sphere s = (Sphere)shape;
				origRadius = s.getRadius();
				origPos[0] = s.pos.x;
				origPos[1] = s.pos.y;
				origPos[2] = s.pos.z;
			}
			else {
				Triangle t = (Triangle)shape;
				for (int v=0; v<3; v++) {
					origVertices[v][0] = t.vertices[v].x;
					origVertices[v][1] = t.vertices[v].y;
					origVertices[v][2] = t.vertices[v].z;
				}
			}
			origMaterial = shape.getMaterial();
		}
		setTitle(action+" "+shapeType);
		
		// add the components
		Dimension hoizFiller = new Dimension(10, 0);
		contentPane.add(createButtons(), BorderLayout.SOUTH);
		contentPane.add(createNameField(), BorderLayout.NORTH);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.WEST);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.EAST);
		
		JComponent entryFields = (shapeType.equals(sphereType)) ? createSphereFields() : createTriangleFields();
		contentPane.add(entryFields);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	// Adding
	public static Shape createAddDialog(Component parent, ShapesManager shapes, MaterialsManager materials, String shapeType) {
		ShapeEditDialog dialog = new ShapeEditDialog(parent, shapes, materials, -1, shapeType);
		return dialog.shape;
	}
	// Editing
	public static Shape createEditDialog(Component parent, ShapesManager shapes, MaterialsManager materials, int index) {
		ShapeEditDialog dialog = new ShapeEditDialog(parent, shapes, materials, index, null);
		return dialog.shape;
	}
	
	public void setupLayout() {
		labelCol = new GridBagConstraints();
		Insets rightPadding = new Insets(0,0,0,20);
		labelCol.weighty = 1.0;
		labelCol.gridx = 0;
		labelCol.gridy = 0;
		labelCol.insets = rightPadding;
		labelCol.anchor = GridBagConstraints.EAST;
		fieldCol = new GridBagConstraints();
		fieldCol.weighty = 1.0;
		fieldCol.gridx = 1;
		fieldCol.gridy = 0;
		fieldCol.fill = GridBagConstraints.HORIZONTAL;
	}

	public void actionPerformed(ActionEvent e) {
		// Changes Accepted
		if (action.equals(e.getActionCommand())){
			// Parse the input values
			String name = nameField.getText();
			
			if (shapeType.equals(sphereType)) {
				try {
					radiusField.commitEdit();
					xField.commitEdit();
					yField.commitEdit();
					zField.commitEdit();
				}
				catch (ParseException ex) {
					return;
				}
				float radius = (Float)(radiusField.getValue());
				float x = (Float)(xField.getValue());
				float y = (Float)(yField.getValue());
				float z = (Float)(zField.getValue());
				// Add new
				if (action.equals(addCom)) shape = shapes.addSphere(name, radius, x, y, z, origMaterial);
				// Edit existing
				else shapes.editSphere((Sphere)shape, name, radius, x, y, z, origMaterial);
			}
			else if (shapeType.equals(triangleType)) {
				float[] verts = new float[3*3];
				for (int v=0; v<3; v++) {
					for (int i=0; i<3; i++) {
						try {
							vertexFields[v][i].commitEdit();
						}
						catch (ParseException ex) {
							return;
						}
						float pt = (Float)(vertexFields[v][i].getValue());
						verts[(v*3)+i] = pt;
					}
				}
				// Add new
				if (action.equals(addCom)) shape = shapes.addTriangle(name, verts, origMaterial);
				// Edit existing
				else shapes.editTriangle((Triangle)shape, name, verts, origMaterial);
			}
		}
		setVisible(false);
		dispose();
	}
	
	private Component createNameField() {
		// name field
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		nameField = new JTextField(10);
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
	
	private JComponent createTriangleFields() {
		final JPanel panel = new JPanel(new GridBagLayout());

		String[] labelStrings = { "Vertex 1: ", "Vertex 2: ", "Vertex 3: " };
		JLabel[] labels = new JLabel[labelStrings.length];
		JComponent[] fieldPanels = new JComponent[labelStrings.length];

		FloatFormatter floatFormat = new FloatFormatter();

		// Create the other fields, associate label/field pairs, add everything, and lay it out.
		vertexFields = new JFormattedTextField[3][3]; // x, y, z
		for (int i = 0; i < 3; i++) {
			labels[i] = new JLabel(labelStrings[i], JLabel.TRAILING);
			fieldPanels[i] = new JPanel();

			for (int j=0; j<3; j++) {
				boolean isZCoord = (j==2);
				vertexFields[i][j] = new JFormattedTextField(floatFormat);
				vertexFields[i][j].setColumns(4);
				vertexFields[i][j].setFocusLostBehavior(JFormattedTextField.COMMIT);
				vertexFields[i][j].setValue(origVertices[i][j]);
				fieldPanels[i].add(vertexFields[i][j]);
				if (!isZCoord) fieldPanels[i].add(new JLabel(", "));
			}
			labels[i].setLabelFor(fieldPanels[i]);
			panel.add(labels[i], labelCol);
			labelCol.gridy++;
			panel.add(fieldPanels[i], fieldCol);
			fieldCol.gridy++;
		}
		JLabel materialLabel = new JLabel("Material: ", JLabel.TRAILING);
		JComboBox materialsList = createMaterialBox();
		materialsList.setSelectedItem(origMaterial);
		materialLabel.setLabelFor(materialsList);
		panel.add(materialLabel, labelCol);
		labelCol.gridy++;
		panel.add(materialsList, fieldCol);
		fieldCol.gridy++;
		return panel;
	}

	private JComponent createSphereFields() {
		final JPanel panel = new JPanel(new GridBagLayout());

		String[] labelStrings = { "Radius: ", "x: ", "y: ", "z: ", "Material: " };

		JLabel[] labels = new JLabel[labelStrings.length];
		JComponent[] fields = new JComponent[labelStrings.length];

		int fieldNum = 0;
		FloatFormatter floatFormat = new FloatFormatter();

		radiusField = new JFormattedTextField(new FloatFormatter(0, Float.MAX_VALUE)); // can't have a negative radius
		radiusField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		radiusField.setColumns(4);
		radiusField.setValue(origRadius);
		fields[fieldNum++] = radiusField;

		xField = new JFormattedTextField(floatFormat);
		xField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		xField.setColumns(4);
		xField.setValue(origPos[0]);
		fields[fieldNum++] = xField;
		
		yField = new JFormattedTextField(floatFormat);
		yField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		yField.setColumns(4);
		yField.setValue(origPos[1]);
		fields[fieldNum++] = yField;
		
		zField = new JFormattedTextField(floatFormat);
		zField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		zField.setColumns(4);
		zField.setValue(origPos[2]);
		fields[fieldNum++] = zField;
		
		JComboBox materialsList = createMaterialBox();
		materialsList.setSelectedItem(origMaterial);
		fields[fieldNum++] = materialsList;
		
		// Associate label/field pairs, add everything, and lay it out.
		for (int i = 0; i < labelStrings.length; i++) {
			labels[i] = new JLabel(labelStrings[i], JLabel.TRAILING);
			labels[i].setLabelFor(fields[i]);
			panel.add(labels[i], labelCol);
			labelCol.gridy++;
			panel.add(fields[i], fieldCol);
			fieldCol.gridy++;
		}
		return panel;
	}
	
	private JComboBox createMaterialBox() {
		final JComboBox materialsList = new JComboBox(materials.getMaterialsAsArray());
		materialsList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				origMaterial = (Material)materialsList.getSelectedItem();
			}
		});
		return materialsList;
	}
}
