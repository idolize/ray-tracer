package domainModel;

public class Sphere implements Shape {
	public String name;
	private float radius, radius2; // radius and radius squared
	public Vector3D pos; // center of sphere object: x, y, z
	private Material material;

	public Sphere(String name, float radius, Vector3D pos, Material material) {
		this.name = name;
		this.radius = radius;
		radius2 = radius*radius;
		this.pos = pos;
		this.material = material;
	}

	public Vector3D getNormal(Vector3D hitPos) {
		//PVector n = PVector.sub(hitPos, pos);
		Vector3D n = Vector3D.sub(pos, hitPos);
		//n.div(radius);
		n.normalize();
		return n;
	}
	
	public void setMaterial(Material m) {
		this.material = m;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setRadius(float r) {
		this.radius = r;
		this.radius2 = r*r;
	}
	
	public float getRadius() {
		return radius;
	}

	public Hit intersectsRay(Ray ray) {
		// solve complex quadratic equation to find value of t on ray
		Vector3D dst = Vector3D.sub(ray.b, pos);
		float B = dst.dot(ray.d);
		float C = dst.dot(dst) - radius2;
		float D = B*B - C;
		float t = (float) (-B - Math.sqrt(D));
		if (!(t > 0)) return null; // escape case: no collision or outside fov
		Vector3D hitPos = Vector3D.add(ray.b, Vector3D.mult(ray.d, t));
		Vector3D n = getNormal(hitPos);
		return new Hit(this, t, hitPos, n);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return "Sphere";
	}
	
	public String toString() {
		return name+" ("+getType()+")";
	}
}