package domainModel;

import java.awt.Color;

public class RGB {
	public int r, g, b;
	
	public RGB() {
		r=g=b=0;
	}
	
	public RGB(int r, int g, int b) {
		// assumes range of 0-255
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public RGB(float r, float g, float b) {
		setFromFloats(r, g, b);
	}
	
	public RGB(Vector3D c) {
		// assumes the input vector contains color information
		setFromFloats(c.x, c.y, c.z);
	}
	
	public void setFromFloats(float r, float g, float b) {
		// assumes range of 0-1;
		this.r = (r < 1.0f) ? (int)(r*255) : 255;
		this.g = (g < 1.0f) ? (int)(g*255) : 255;
		this.b = (b < 1.0f) ? (int)(b*255) : 255;
	}
	
	public RGB get() {
		return new RGB(r, g, b);
	}
	
	public Color toColor() {
		return new Color(r, g, b);
	}
}
