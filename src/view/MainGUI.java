package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import appModel.CollectionList;

import domainModel.Light;
import domainModel.Material;
import domainModel.RayTracer;
import domainModel.Shape;

public class MainGUI implements ActionListener {
	public static final Color errorColor = new Color(1.0f, 0.65f, 0.65f);
	private RayTracer tracer;
	private Container mainFrame;
	private RayTracerCanvas canvas;
	private CollectionList<Shape> shapesList;
	private CollectionList<Light> lightsList;
	private CollectionList<Material> materialsList;

	public MainGUI() {
		tracer = new RayTracer(300, 300, 60, 15, 0.165f, 1/3.0f, 0.75f);
		createGUI();
		addDefaultScene();
	}

	private void createGUI() {
		JFrame frame = new JFrame("Ray Tracer");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame = frame.getContentPane();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel centerPanel = new JPanel();
		canvas = new RayTracerCanvas(frame, tracer);
		centerPanel.add(canvas);

		JPanel bottomPanel = new JPanel(new GridBagLayout());

		// set up layout
		Insets midPadding = new Insets(10,10,0,10);
		Insets noPadding = new Insets(0,0,0,0);
		GridBagConstraints labelRow = new GridBagConstraints();
		labelRow.weightx = 1.0;
		labelRow.gridy = 0;
		labelRow.insets = noPadding;
		labelRow.anchor = GridBagConstraints.PAGE_END;
		GridBagConstraints listRow = new GridBagConstraints();
		listRow.weightx = 1.0;
		listRow.gridy = 1;
		listRow.insets = midPadding;
		listRow.fill = GridBagConstraints.BOTH;
		GridBagConstraints buttonRow = new GridBagConstraints();
		buttonRow.weightx = 1.0;
		buttonRow.gridy = 2;
		buttonRow.insets = noPadding;
		buttonRow.anchor = GridBagConstraints.PAGE_START;
		Dimension colSize = new Dimension(200,100);

		// shapes collection
		ShapesToolbar shapesToolbar = new ShapesToolbar(frame);
		shapesList = new CollectionList<Shape>(tracer.shapes, shapesToolbar);
		tracer.shapesManager.addObserver(shapesList.getModel());
		JLabel shapesLabel = new JLabel("Shapes");
		shapesLabel.setLabelFor(shapesList);
		labelRow.gridx = listRow.gridx = buttonRow.gridx = 0;
		bottomPanel.add(shapesLabel, labelRow);
		JScrollPane shapes = new JScrollPane(shapesList);
		shapes.setPreferredSize(colSize);
		bottomPanel.add(shapes, listRow);
		bottomPanel.add(shapesToolbar, buttonRow);

		// lights collection
		LightsToolbar lightsToolbar = new LightsToolbar(frame);
		lightsList = new CollectionList<Light>(tracer.lights, lightsToolbar);
		tracer.lightsManager.addObserver(lightsList.getModel());
		JLabel lightsLabel = new JLabel("Lights");
		lightsLabel.setLabelFor(shapesList);
		labelRow.gridx = listRow.gridx = buttonRow.gridx = 1;
		bottomPanel.add(lightsLabel, labelRow);
		JScrollPane lights = new JScrollPane(lightsList);
		lights.setPreferredSize(colSize);
		bottomPanel.add(lights, listRow);
		bottomPanel.add(lightsToolbar, buttonRow);

		// materials collection
		MaterialsToolbar materialsToolbar = new MaterialsToolbar(frame);
		materialsList = new CollectionList<Material>(tracer.materials, materialsToolbar);
		tracer.materialsManager.addObserver(materialsList.getModel());
		JLabel materialsLabel = new JLabel("Materials");
		materialsLabel.setLabelFor(shapesList);
		labelRow.gridx = listRow.gridx = buttonRow.gridx = 2;
		bottomPanel.add(materialsLabel, labelRow);
		JScrollPane materials = new JScrollPane(materialsList);
		materials.setPreferredSize(colSize);
		bottomPanel.add(materials, listRow);
		bottomPanel.add(materialsToolbar, buttonRow);

		mainPanel.add(centerPanel);
		mainPanel.add(new JSeparator());
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(bottomPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(new JSeparator());
		mainPanel.add(createOptionsPane());
		mainFrame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}

	private JComponent createOptionsPane() {
		JPanel panel = new JPanel();

		JButton aboutBtn = new JButton("About");
		aboutBtn.setActionCommand("About");
		aboutBtn.addActionListener(this);
		panel.add(aboutBtn);

		JButton bgColorBtn = new JButton("Change Background Color");
		bgColorBtn.setActionCommand("Color");
		bgColorBtn.addActionListener(this);
		panel.add(bgColorBtn);

		JButton sizeBtn = new JButton("Change Size / Field of View");
		sizeBtn.setActionCommand("Size");
		sizeBtn.addActionListener(this);
		panel.add(sizeBtn);

		JButton saveBtn = new JButton("Export Image");
		saveBtn.setActionCommand("Save");
		saveBtn.addActionListener(this);
		panel.add(saveBtn);

		return panel;
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		/*
		 * About
		 */
		if (command.equals("About")) {
			JOptionPane.showMessageDialog(mainFrame, "This application was created by R. David Idol.", "About", JOptionPane.INFORMATION_MESSAGE);
		}
		/*
		 * Change background color
		 */
		else if (command.equals("Color")) {
			Color newColor = JColorChooser.showDialog(mainFrame, "Select Background Color", new Color(tracer.bgColor.x, tracer.bgColor.y, tracer.bgColor.z));
			if (newColor != null) { // OK was pressed
				float color[] = newColor.getColorComponents(null);
			    tracer.setBackground(color[0], color[1], color[2]);
			}
		}
		/*
		 * Change size / field-of-view
		 */
		else if (command.equals("Size")) {
			new SizeDialog(mainFrame, tracer);
		}
		/*
		 * Save Image
		 */
		else if (command.equals("Save")) {
			JFileChooser chooser = new JFileChooser();
			String extension = "png";
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG image", extension));
			int result = chooser.showSaveDialog(mainFrame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File filepath = chooser.getSelectedFile();
				String name = filepath.getName();
				// Validate that the file extension is correct
				if (!name.toLowerCase().endsWith("." + extension.toLowerCase())) {
					name += "." + extension;
					filepath = new File(filepath.getParent(), name);
				}
				// Check if a file already exists at this location
				if (filepath.exists ()) {
		             int response = JOptionPane.showConfirmDialog (mainFrame, "A file by that name already exists.\nAre you sure you wish to overwrite the existing file?","Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		             if (response == JOptionPane.CANCEL_OPTION) return;
		         }
				// Save the image
				canvas.saveImage(filepath, extension);
				JOptionPane.showMessageDialog(mainFrame, "Image saved to "+filepath.getPath()+".", "Image Saved", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	private void addDefaultScene() {
		// lights
		tracer.lightsManager.addLight("Right", 8.0f, 8.0f, 3.0f, 0.7f, 0.7f, 0.7f);
		tracer.lightsManager.addLight("Left", -4.0f, 2.0f, 0f, 0.3f, 0.3f, 0.3f);
		// ground surface
		tracer.materialsManager.addMaterial("Matte Ground", new float[]{0.8f, 0.8f, 0.8f}, new float[]{0.2f, 0.2f, 0.2f}, new float[]{0, 0, 0}, 1, 0, false);
		// sphere surface
		tracer.materialsManager.addMaterial("Ultra Reflective", new float[]{0.6f, 0.6f, 0.6f}, new float[]{0.2f, 0.2f, 0.2f}, new float[]{0.7f, 0.7f, 0.7f}, 20, 0.7f, false);
		// ground plane
		tracer.shapesManager.addTriangle("Ground 1", new float[]{-100f, -1f, -100f, 100f, -1f, -100f, 100f, -1f, 100f}, tracer.materialsManager.getMaterial(1));
		tracer.shapesManager.addTriangle("Ground 2", new float[]{100f, -1f, 100f, -100f, -1f, 100f, -100f, -1f, -100f}, tracer.materialsManager.getMaterial(1));
		// spheres
		tracer.shapesManager.addSphere("Marble 1", 1.0f, 1f, 0, -5f, tracer.materialsManager.getMaterial(2));
		tracer.shapesManager.addSphere("Marble 2", 1.0f, -2f, 0, -7f, tracer.materialsManager.getMaterial(2));
		tracer.shapesManager.addSphere("Marble 3", 0.3f, 0, -0.7f, -2f, tracer.materialsManager.getMaterial(2));
		tracer.shapesManager.addSphere("Marble 4", 0.4f, 1f, -0.6f, -3f, tracer.materialsManager.getMaterial(2));
		tracer.shapesManager.addSphere("Marble 5", 0.5f, -0.7f, -0.5f, -3f, tracer.materialsManager.getMaterial(2));
	}

	@SuppressWarnings("serial")
	class ShapesToolbar extends CollectionToolbar {
		String[] options = { "Sphere", "Triangle" };
		ShapesToolbar(Component parent) { super(parent); }
		void addBtnClicked() {
			int type = JOptionPane.showOptionDialog(parent, new JLabel("Create what type of shape?", JLabel.CENTER), null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
			if (type != -1) {
				Shape s = ShapeEditDialog.createAddDialog(parent, tracer.shapesManager, tracer.materialsManager, options[type]);
				shapesList.setSelectedValue(s, true);
			}
		}
		void editBtnClicked() {
			int index = shapesList.getSelectedIndex();
			ShapeEditDialog.createEditDialog(parent, tracer.shapesManager, tracer.materialsManager, index);
		}
		void removeBtnClicked() {
			int index = shapesList.getSelectedIndex();
			if (index != -1) {
				tracer.shapesManager.removeShape(index);
				shapesList.clearSelection();
			}
		}
	}
	@SuppressWarnings("serial")
	class LightsToolbar extends CollectionToolbar {
		LightsToolbar(Component parent) { super(parent); }
		void addBtnClicked() {
			Light l = LightEditDialog.createAddDialog(parent, tracer.lightsManager);
			lightsList.setSelectedValue(l, true);
		}
		void editBtnClicked() {
			int index = lightsList.getSelectedIndex();
			LightEditDialog.createEditDialog(parent, tracer.lightsManager, index);
		}
		void removeBtnClicked() {
			int index = lightsList.getSelectedIndex();
			if (index != -1) {
				tracer.lightsManager.removeLight(index);
				lightsList.clearSelection();
			}
		}
	}
	@SuppressWarnings("serial")
	class MaterialsToolbar extends CollectionToolbar {
		MaterialsToolbar(Component parent) { super(parent); }
		void addBtnClicked() {
			Material m = MaterialEditDialog.createAddDialog(parent, tracer.materialsManager);
			materialsList.setSelectedValue(m, true);
		}
		void editBtnClicked() {
			int index = materialsList.getSelectedIndex();
			MaterialEditDialog.createEditDialog(parent, tracer.materialsManager, index);
		}
		void removeBtnClicked() {
			int index = materialsList.getSelectedIndex();
			if (index != -1) {
				Material m = tracer.materialsManager.getMaterial(index);
				// Can't remove the default material
				if (tracer.materialsManager.defaultMaterial == m) {
					JOptionPane.showMessageDialog(parent, "You can not remove the default material", "Can Not Remove", JOptionPane.WARNING_MESSAGE);
					return;
				}
				else {
					// Make sure all objects using this material switch to the default instead
					for (Shape s : tracer.shapes) {
						if (s.getMaterial() == m) s.setMaterial(tracer.materialsManager.defaultMaterial);
					}
					// Remove it
					tracer.materialsManager.removeMaterial(index);
					materialsList.clearSelection();
				}
			}
		}
	}

	public static void main(String[] args) {
		new MainGUI();
	}
}