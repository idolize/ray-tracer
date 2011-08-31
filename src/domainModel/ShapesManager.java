package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class ShapesManager extends Observable {
	private ArrayList<Shape> shapes;
	
	public ShapesManager(ArrayList<Shape> shapes) {
		this.shapes = shapes;
	}
	
	public void addShape(Shape s) {
		this.shapes.add(s);
		setChanged();
		notifyObservers();
	}

	public Sphere addSphere(String name, float radius, float x, float y, float z, Material material) {
		Sphere s = new Sphere(name, radius, new Vector3D(x, y, z), material);
		shapes.add(s);
		setChanged();
		notifyObservers();
		return s;
	}
	
	public void editSphere(Sphere sphere, String name, float radius, float x, float y, float z, Material material) {
		sphere.name = name;
		sphere.setRadius(radius);
		sphere.pos.x = x;
		sphere.pos.y = y;
		sphere.pos.z = z;
		sphere.setMaterial(material);
		setChanged();
		notifyObservers();
	}
	
	public Triangle addTriangle(String name, float[] vertices, Material material) {
		Triangle t = new Triangle(name, material);
		int coord = 0;
		for (int i = 0; i<3; i++){
			Vector3D v = new Vector3D(vertices[coord++], vertices[coord++], vertices[coord++]);
			t.addNextVertex(v);
		}
		shapes.add(t);
		setChanged();
		notifyObservers();
		return t;
	}
	
	public void editTriangle(Triangle triangle, String name, float[] vertices, Material material) {
		triangle.name = name;
		triangle.setMaterial(material);
		int coord = 0;
		for (int i = 0; i<3; i++){
			triangle.vertices[i].x = vertices[coord++];
			triangle.vertices[i].y = vertices[coord++];
			triangle.vertices[i].z = vertices[coord++];
		}
		setChanged();
		notifyObservers();
	}
	
	public Shape getShape(int index) {
		return shapes.get(index);
	}
	
	public Shape removeShape(int index) {
		Shape s = shapes.get(index);
		shapes.remove(index);
		setChanged();
		notifyObservers();
		return s;
	}
}
