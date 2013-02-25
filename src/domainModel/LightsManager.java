package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class LightsManager extends Observable {
	private ArrayList<Light> lights;
	
	public LightsManager(ArrayList<Light> lights) {
		this.lights = lights;
	}
	
	public Light addLight(String name, float x, float y, float z, float r, float g, float b) {
		Light l = new Light(name, new Vector3D(x,y,z), new Vector3D(r,g,b));
		lights.add(l);
		setChanged();
		notifyObservers();
		return l;
	}
	
	public void editLight(Light light, String name, float x, float y, float z, float r, float g, float b) {
		light.name = name;
		light.pos.x = x;
		light.pos.y = y;
		light.pos.z = z;
		light.c.x = r;
		light.c.y = g;
		light.c.z = b;
		setChanged();
		notifyObservers();
	}
	
	public Light getLight(int index) {
		return lights.get(index);
	}
	
	public Light removeLight(int index) {
		Light l = lights.get(index);
		lights.remove(index);
		setChanged();
		notifyObservers();
		return l;
	}
}