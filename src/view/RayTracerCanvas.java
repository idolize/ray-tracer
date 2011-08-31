package view;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import domainModel.RayTracer;

@SuppressWarnings("serial")
public class RayTracerCanvas extends Canvas implements Observer {
	private BufferedImage image;
	private WritableRaster raster;
	private Color bgColor;
	private RayTracer model;
	private Dimension size = new Dimension();
	private Component parent;

	public RayTracerCanvas(Component parent, RayTracer model) {
		this.parent = parent;
		this.model = model;
		setBackground (bgColor);
		setSize(model.width, model.height);
		image = new BufferedImage(model.width, model.height, BufferedImage.TYPE_INT_RGB);
		raster = image.getRaster();
		model.addObserver(this);
	}

	public void paint(Graphics g) {
		raster.setPixels(0, 0, model.width, model.height, model.pixels);
		g.drawImage(image, 0, 0, null);
		g.setPaintMode();
	}
	
	public boolean saveImage(File filepath, String format) {
		try {
			return ImageIO.write(image, format, filepath);
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public void update(Observable o, Object arg) {
		String command = (String)arg;
		if (command.equals("Render")) {
			repaint();
		}
		else if (command.equals("Dimensions")) {
			// resize the image and get a new raster to write to
			image = new BufferedImage(model.width, model.height, BufferedImage.TYPE_INT_RGB);
			raster = image.getRaster();
			
			size.width = model.width;
			size.height = model.height;
			setSize(size);			
			if (parent instanceof JFrame) ((JFrame)parent).pack();
		}
	}
}