package domainModel;
public class Hit {
	public float t; // distance along ray (to make comparisons easier)
	public Vector3D pos; // position of hit
	public Shape shapeHit;
	public Vector3D n; // surface normal
	
	public Hit(Shape shapeHit, float t, Vector3D pos, Vector3D n) {
		this.t = t;
		this.pos = pos;
		this.shapeHit = shapeHit;
		this.n = n;
	}
}