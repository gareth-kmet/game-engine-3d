package game.world.terrain.heights;

public final class Height {
	
	private Height() {}

	
	public static Data generateHeightMap(final Noise.Data noiseData, LandHeightStyles landStyle, WaterHeightStyles waterStyle){
		float[][] heights = new float[noiseData.map.length][noiseData.map[0].length];
		
		float maxNoiseHeight = Float.MAX_VALUE;
		float minNoiseHeight = Float.MIN_VALUE;
		
		for (int x = 0; x < heights.length; x++) {
			for (int z = 0; z < heights[x].length; z++) {
				float height = getHeight(noiseData.map[x][z], landStyle, waterStyle);
				
				if(height > maxNoiseHeight) {
					maxNoiseHeight = height;
				}else if(height < minNoiseHeight) {
					minNoiseHeight = height;
				}
				
				heights[x][z] = height;
			}
		}
		
		return new Data(heights, noiseData, maxNoiseHeight, minNoiseHeight);
	}
	
	private static float getHeight(float x, LandHeightStyles landStyle, WaterHeightStyles waterStyle) {
		
		return landStyle.getHeight(x, waterStyle);
	}
	
//	flat 		= x*x*x; 				
//	hilly		= x*(x*x+0.1f)*(x-1.25f)*(x+1.25f);			
//	mountain	= x*x*x+x*x+x;			
//	islands 	= x*x*x+x*x+x/2f; | y = -h(-x)*1000;
//	oceans 		= -x*x-x*0.25f+0.1f;	
//	flat islands= x*((x-0.8f)*(x-0.8f)+0.1f)*((x+0.4f)*(x-0.4f)+0.3f);
	
	
	
	public final static class Data extends HeightsDataClass{
		
		final Noise.Data noiseData;
		
		private Data(float[][] heights, Noise.Data noise, float max, float min) {
			super(heights, max, min);
			noiseData = noise;
		}
		
	}

}
