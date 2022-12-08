package game.world.terrain.heights;

public abstract class HeightsDataClass {
	
	public float min, max;
	public final float[][] map;
	
	HeightsDataClass(float[][] map, float max, float min) {
		this.min=min; this.max=max;
		this.map=map;
	}
	
	public float[][] getMap(){
		return map;
	}
	

}
