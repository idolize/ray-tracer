package domainModel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class RayTracer extends Observable implements Observer {
	public int width, height, fov;
	public int maxDepth; // maximum number of recursive reflection rays
	public float[] pixels;	
	public Vector3D bgColor;
	public ArrayList<Shape> shapes;
	public ShapesManager shapesManager;
	public ArrayList<Light> lights;
	public LightsManager lightsManager;
	public ArrayList<Material> materials;
	public MaterialsManager materialsManager;
	public Ray[] camRays;
	private float k; // used to determine size of view plane from fov

	public RayTracer(int width, int height, int fov, int maxDepth, float bgR, float bgG, float bgB) {
		this.maxDepth = maxDepth;
		this.bgColor = new Vector3D(bgR, bgG, bgB);
		// Create collections
		shapes = new ArrayList<Shape>();
		shapesManager = new ShapesManager(shapes);
		shapesManager.addObserver(this);
		lights = new ArrayList<Light>();
		lightsManager = new LightsManager(lights);
		lightsManager.addObserver(this);
		materials = new ArrayList<Material>();
		materialsManager = new MaterialsManager(materials);
		materialsManager.addObserver(this);
		
		alterDimensions(width, height, fov);
		
		// add the default material
		float[] Kd = {0.8f, 0.8f, 0.8f};
		float[] Ka = {0.2f, 0.2f, 0.2f};
		float[] Ks = {0f, 0f, 0f};
		materialsManager.addMaterial("Default", Kd, Ka, Ks, 10, 0, true);
	}

	public void alterDimensions(int width, int height, int fov) {
		this.width = width;
		this.height = height;
		this.fov = fov;
		pixels = new float[width*height*3]; // 96 bit pixels
		camRays = new Ray[width*height];

		// Pre-create a ray for each pixel from the eye down the negative z-axis
		// and convert pixel on screen into pPrime (point on view plane)
		float aspect = (float)height / width;
		float fovR = (float) (fov * (Math.PI / 180.0)); // convert to radians
		k = 2*(float)Math.tan(fovR/2);
		int r = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Ray ray = new Ray(new Vector3D(0,0,0), (x - width/2)*(k/width), -(y - height/2)*(k/height)*aspect, -1);
				camRays[r++] = ray;
			}
		}
		setChanged();
		notifyObservers("Dimensions");
		rayTraceScene();
	}

	public void setBackground(float r, float g, float b) {
		bgColor.x = r;
		bgColor.y = g;
		bgColor.z = b;
		rayTraceScene();
	}

	public void update(Observable o, Object arg) {
		if ((String)arg != "Collection") rayTraceScene();
	}

	/*
	 **************************** Ray Tracing functions ********************************
	 */

	public void rayTraceScene() {
		int ray_depth = 0;
		int pixelNum = 0;
		Vector3D color;
		for (Ray ray : camRays) {
			pixelNum += 3;
			color = shadeRay(ray, ray_depth);
			pixels[pixelNum-3] = (color.x < 1.0f) ? (int)(color.x*255) : 255;
			pixels[pixelNum-2] = (color.y < 1.0f) ? (int)(color.y*255) : 255;
			pixels[pixelNum-1] = (color.z < 1.0f) ? (int)(color.z*255) : 255;
			//System.out.println("Model: adding colors to pixels array...");
		}
		setChanged();
		notifyObservers("Render");
	}

	private Vector3D shadeRay(Ray ray, int ray_depth) {
		// called initially and also recursively for reflections and shadows
		if (ray_depth < maxDepth) {
			ray_depth++;
			Hit nearest_hit = rayIntersectScene(ray);
			if (nearest_hit != null) return shadeHit(nearest_hit, ray, ray_depth);
		}
		return bgColor.get();
	}

	private Vector3D shadeHit(Hit hit, Ray ray, int ray_depth) {		
		// find what color the hit should be
		Material material = hit.shapeHit.getMaterial();
		Vector3D L = material.Ka.get();
		// create reflection ray
		Vector3D reflP = hit.pos.get();
		Vector3D reflD = Vector3D.sub(ray.d, Vector3D.mult(hit.n, 2*(ray.d.dot(hit.n))));
		reflD.normalize();
		Vector3D e = reflD.get();
		e.limit(0.001f);
		reflP.add(e);
		Ray reflRay = new Ray(reflP, reflD);
		// loop through each light in the scene
		for (int i=0; i<lights.size(); i++) {
			Light light = lights.get(i);
			// find l, the vector from the hit to the light
			Vector3D l = Vector3D.sub(hit.pos, light.pos);
			float distToLight = l.mag();
			l.normalize();
			// create shadow ray
			Vector3D shadowP = hit.pos.get();
			Vector3D shadowD = Vector3D.oppSign(l);
			e = shadowD.get();
			e.limit(0.005f);
			shadowP.add(e);
			Ray shadow = new Ray(shadowP, shadowD);
			// add shading if not in shadow
			Hit shadowHit = rayIntersectScene(shadow);
			if (shadowHit == null || (shadowHit != null && shadowHit.t > distToLight)) {
				// diffuse
				Vector3D Kd = Vector3D.mult(material.Kd, Math.max(0,(hit.n).dot(l)));
				Kd.mult(light.c);
				L.add(Kd);
				// specular
				Vector3D Ks = Vector3D.mult(material.Ks, (float)Math.pow(Math.max(0,(reflD).dot(shadowD)), material.p)); // r¥v
				Ks.mult(light.c);
				L.add(Ks);
			}
		}
		// add reflection if the surface is reflective
		if (material.Krefl > 0) {
			// recursive call to shade_ray() for the reflection ray
			Vector3D rColor = shadeRay(reflRay, ray_depth);
			rColor.mult(material.Krefl);
			L.add(rColor);
		}
		return L;
	}

	private Hit rayIntersectScene(Ray ray) {
		// returns nearest hit object
		Hit hit = null;
		if (!shapes.isEmpty()) hit = shapes.get(0).intersectsRay(ray);
		// brute force all shapes in the scene
		for (int i=1; i<shapes.size(); i++) {
			Shape s = shapes.get(i);
			Hit nextHit = s.intersectsRay(ray);
			// check if the hit is closer than the last hit
			if (hit == null && nextHit != null) hit = nextHit;
			else if (hit != null && nextHit != null) {
				// compare which one is closer
				hit = (nextHit.t < hit.t) ? nextHit : hit;
			}
		}
		return hit;
	}
}