package domainModel;

public class Material {
	public String name;
	public Vector3D Kd, Ka, Ks; // diffuse, ambient, and specular coefficients (red, green, blue)
	public float p; // specular power P (the Phong exponent), which says how shiny the highlight of the surface should be
	public float Krefl; // reflection coefficient (0 = no reflection, 1 = perfect mirror)
	// Usually, 0 <= Kd,Ka,Ks,Krefl <= 1

	public Material(String name, Vector3D Kd, Vector3D Ka, Vector3D Ks, float p, float Krefl) {
		this.name = name;
		this.Kd = Kd;
		this.Ka = Ka;
		this.Ks = Ks;
		this.p = p;
		this.Krefl = Krefl;
	}
	
	public String toString() {
		return name+" (Material)";
	}
}