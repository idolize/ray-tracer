package domainModel;

public class Vector3D {
	public float x, y, z;
	protected float[] array;

	public Vector3D() {
		x = y = z = 0;
	}

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D get() {
		return new Vector3D(x, y, z);
	}

	public float mag() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}

	public void add(Vector3D v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	static public Vector3D add(Vector3D v1, Vector3D v2) {
		float newX = v1.x + v2.x;
		float newY = v1.y + v2.y;
		float newZ = v1.z + v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	public void sub(Vector3D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	static public Vector3D sub(Vector3D v1, Vector3D v2) {
		float newX = v1.x - v2.x;
		float newY = v1.y - v2.y;
		float newZ = v1.z - v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	public void mult(float n) {
		x *= n;
		y *= n;
		z *= n;
	}

	static public Vector3D mult(Vector3D v, float n) {
		float newX = v.x * n;
		float newY = v.y * n;
		float newZ = v.z * n;
		return new Vector3D(newX, newY, newZ);
	}

	public void mult(Vector3D v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
	}

	static public Vector3D mult(Vector3D v1, Vector3D v2) {
		float newX = v1.x * v2.x;
		float newY = v1.y * v2.y;
		float newZ = v1.z * v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	public void div(float n) {
		x /= n;
		y /= n;
		z /= n;
	}

	static public Vector3D div(Vector3D v, float n) {
		float newX = v.x / n;
		float newY = v.y / n;
		float newZ = v.z / n;
		return new Vector3D(newX, newY, newZ);
	}

	public void div(Vector3D v) {
		x /= v.x;
		y /= v.y;
		z /= v.z;
	}

	static public Vector3D div(Vector3D v1, Vector3D v2) {
		float newX = v1.x / v2.x;
		float newY = v1.y / v2.y;
		float newZ = v1.z / v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	static public Vector3D oppSign(Vector3D v) {
		return new Vector3D(-v.x, -v.y, -v.z);
	}

	public float dist(Vector3D v) {
		float newX = x - v.x;
		float newY = y - v.y;
		float newZ = z - v.z;
		return (float) Math.sqrt(newX*newX + newY*newY + newZ*newZ);
	}

	static public float dist(Vector3D v1, Vector3D v2) {
		float newX = v1.x - v2.x;
		float newY = v1.y - v2.y;
		float newZ = v1.z - v2.z;
		return (float) Math.sqrt(newX*newX + newY*newY + newZ*newZ);
	}

	public float dot(Vector3D v) {
		return x*v.x + y*v.y + z*v.z;
	}

	static public float dot(Vector3D v1, Vector3D v2) {
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}

	public Vector3D cross(Vector3D v) {
		float newX = y * v.z - v.y * z;
		float newY = z * v.x - v.z * x;
		float newZ = x * v.y - v.x * y;
		return new Vector3D(newX, newY, newZ);
	}

	public void normalize() {
		float m = mag();
		if (m != 0 && m != 1) {
			div(m);
		}
	}

	public void limit(float max) {
		if (mag() > max) {
			normalize();
			mult(max);
		}
	}

	public String toString() {
		return "<"+x+", "+y+", "+z+">";
	}

	public float[] toArray() {
		if (array == null) {
			array = new float[3];
		}
		array[0] = x;
		array[1] = y;
		array[2] = z;
		return array;
	}
}
