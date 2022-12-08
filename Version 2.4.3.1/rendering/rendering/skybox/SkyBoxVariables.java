package rendering.skybox;

import org.lwjgl.util.vector.Vector3f;

public enum SkyBoxVariables {;
	
	public static int 
		size = 5000;
	
	public enum Fog{;
		public static Vector3f colour = new Vector3f(0.5444f, 0.62f, 0.69f);
		public static float 
			density = 0.0007f,
			gradient = 1.5f;
	}

}
