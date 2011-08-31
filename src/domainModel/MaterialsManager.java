package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class MaterialsManager extends Observable {
	private ArrayList<Material> materials;
	public Material defaultMaterial;

	public MaterialsManager(ArrayList<Material> materials) {
		this.materials = materials;
	}

	public Material addMaterial(String name, float[] Kd, float[] Ka, float[] Ks, float p, float Krefl, boolean isDefault) {
		Material m = new Material(name, new Vector3D(Kd[0], Kd[1], Kd[2]), new Vector3D(Ka[0], Ka[1], Ka[2]), new Vector3D(Ks[0], Ks[1], Ks[2]), p, Krefl);
		if (isDefault) defaultMaterial = m;
		materials.add(m);
		setChanged();
		notifyObservers("Collection");
		return m;
	}

	public void editMaterial(Material material, String name, float[] Kd, float[] Ka, float[] Ks, float p, float Krefl) {
		material.name = name;
		material.Kd.x = Kd[0];
		material.Kd.y = Kd[1];
		material.Kd.z = Kd[2];
		material.Ka.x = Ka[0];
		material.Ka.y = Ka[1];
		material.Ka.z = Ka[2];
		material.Ks.x = Ks[0];
		material.Ks.y = Ks[1];
		material.Ks.z = Ks[2];
		material.p = p;
		material.Krefl = Krefl;
		setChanged();
		notifyObservers();
	}

	public Material getMaterial(int index) {
		return materials.get(index);
	}
	
	public Object[] getMaterialsAsArray() {
		return materials.toArray();
	}
	
	public Material removeMaterial(int index) {
		Material m = materials.get(index);
		materials.remove(index);
		setChanged();
		notifyObservers();
		return m;
	}
}
