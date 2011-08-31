package domainModel;

public class Triangle implements Shape {
	public String name;
	public Vector3D[] vertices = new Vector3D[3];
	private Vector3D u, v; // lines that make up plane
	public Vector3D n; // normal to plane
	private int currVertex = 0;
	private Material material;
	private boolean full = false;

	public Triangle(String name, Material material) {
		this.name = name;
		this.material = material;
	}

	public Triangle(String name, Vector3D vertex1, Vector3D vertex2, Vector3D vertex3, Material material) {
		this(name, material);
		vertices[0] = vertex1;
		vertices[1] = vertex2;
		vertices[2] = vertex3;
		full = true;
		calulatePlane();
	}

	private void calulatePlane() {
		u = Vector3D.sub(vertices[1], vertices[0]);
		v = Vector3D.sub(vertices[2], vertices[0]);
		n = u.cross(v);
		n.normalize();
	}

	public void addNextVertex(Vector3D v) {
		if (!full) {
			vertices[currVertex] = v;
			if (currVertex<2) currVertex++;
			else {
				currVertex = 0;
				full = true;
				calulatePlane();
			}
		}
		else System.out.println("WARNING: Attempting to add a vertex to a Triangle which already has three vertices!");
	}

	public Vector3D getNextVertex() {
		// should only be called once array is populated or it will return null
		if (full) {
			Vector3D v = vertices[currVertex];
			currVertex = (currVertex<2) ? currVertex+1 : 0;
			return v;
		}
		System.out.println("WARNING: Attempting to get a vertex from a Triangle with less than three vertices!");
		return null;
	}
	
	public void setMaterial(Material m) {
		this.material = m;
	}
	
	public Material getMaterial() {
		return material;
	}

	public Hit intersectsRay(Ray ray) {
		// intersect with plane of polygon
		Vector3D w0 = Vector3D.sub(ray.b, vertices[0]);
		float a = -n.dot(w0);
		float b = n.dot(ray.d);
		// possible check to see if ray is inside plane here
		if (Math.abs(b) < 0.0000001 && a != 0) return null; // escape case: ray is parallel to plane
		// find intersection point of ray with triangle plane
		float t = a/b;
		if (t < 0) return null; // escape case: ray is traveling away from triangle
		Vector3D hitPos = Vector3D.add(ray.b, Vector3D.mult(ray.d, t));
		// point-in-polygon test done in 3D rather than 2D
		float uu = u.dot(u);
		float uv = u.dot(v);
		float vv = v.dot(v);
		Vector3D w = Vector3D.sub(hitPos, vertices[0]);
		float wu = w.dot(u);
		float wv = w.dot(v);
		float d = uv*uv - uu*vv;
		float s = (uv*wv - vv*wu)/d;
		if (s > 1.000001f || s < 0) return null; // escape case: point of intersection is outside triangle
		float q = (uv*wu - uu*wv)/d;
		if ((s+q) > 1.000001f || q < 0) return null; // escape case: point of intersection is outside triangle
		return new Hit(this, t, hitPos, n.get());
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return "Triangle";
	}
	
	public String toString() {
		return name+" ("+getType()+")";
	}
}