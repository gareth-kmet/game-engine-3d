package rendering.particles;

import org.lwjgl.util.vector.Vector3f;

import mainLoop.DisplayManager;

public class SimpleParticleSystem extends ParticleSystem{
	
	private float pps;
	private float speed;
	private float gravityComplient;
	private float lifeLength;
	
	public SimpleParticleSystem(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength) {
		super(texture);
		this.pps = pps;
		this.speed = speed;
		this.gravityComplient = gravityComplient;
		this.lifeLength = lifeLength;
	}
	
	public void generateParticles(Vector3f systemCenter){
		float delta = DisplayManager.getFrameTimeSeconds();
		float particlesToCreate = pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		for(int i=0;i<count;i++){
			emitParticle(systemCenter);
		}
		if(Math.random() < partialParticle){
			emitParticle(systemCenter);
		}
	}
	
	private void emitParticle(Vector3f center){
		float dirX = (float) Math.random() * 2f - 1f;
		float dirZ = (float) Math.random() * 2f - 1f;
		Vector3f velocity = new Vector3f(dirX, 1, dirZ);
		velocity.normalise();
		velocity.scale(speed);
		new Particle(getTexture(), new Vector3f(center), velocity, gravityComplient, lifeLength, 0, 1);
	}
	
	

}
