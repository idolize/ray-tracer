package domainModel;
public class Light {
	public String name;
	public Vector3D pos;
	public Vector3D c;

	public Light(String name, Vector3D pos, Vector3D c) {
		this.name = name;
		this.pos = pos;
		this.c = c;
	}
	
	public String toString() {
		return name+" (Light)";
	}
}