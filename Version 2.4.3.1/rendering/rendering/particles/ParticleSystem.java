package rendering.particles;

import org.lwjgl.util.vector.Vector3f;

public abstract class ParticleSystem {
	
	private ParticleTexture texture;
	
	
	
	public ParticleSystem(ParticleTexture texture) {
		this.texture = texture;
	}



	public ParticleTexture getTexture() {
		return texture;
	}



	public abstract void generateParticles(Vector3f systemCenter);

}
