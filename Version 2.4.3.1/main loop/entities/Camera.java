package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch=20, yaw, roll;
	private float distanceFromPlayer = 0, angleAroundPlayer=0;
	private float mousex=0,mousey=0;
	
	private Player player;
	
	public Camera(Player player) {
		this.player=player;
	}
	
	public void move() {
		calculateAngleAroundPlayer();
		calculatePitch();
		calculateZoom();
		float hDistance = calculateHorizontalDistance();
		float vDistance = calculateVerticalDistance();
		calculateCarmerPosition(hDistance, vDistance);
		this.yaw = 180 - (player.getRotation().y + angleAroundPlayer);
		
		
		
		//pitch+=mousey;
		//yaw+=mousex;
	}
	
	private void calculateCarmerPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.getRotation().y + angleAroundPlayer;
		float offsetX  = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ  = (float) (horizontalDistance * Math.cos(Math.toRadians(theta))); 
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance;// + 6.0f;
		
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.01f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		if(Mouse.isGrabbed()){
			pitch-=Mouse.getDY() *0.1f;
		}
	}
	
	private void calculateAngleAroundPlayer() {
		if(Mouse.isGrabbed()) {
			angleAroundPlayer-=Mouse.getDX()*0.3f;
		}
	}
	
	public void invertPitch() {
		this.pitch*=-1;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	
}
