package domainModel;
public interface Shape {
	Hit intersectsRay(Ray ray); // test for intersection with a ray
	Material getMaterial(); // returns the material of the object
	void setMaterial(Material m); // sets the material of the object
	String getName(); // gets the name of the object
	void setName(String name); // sets the object's name
	String getType(); // returns a String of the class type
}