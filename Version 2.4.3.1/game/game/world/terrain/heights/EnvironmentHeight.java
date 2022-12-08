package game.world.terrain.heights;

import game.world.environment.styles.EnvironmentStyles;

public class EnvironmentHeight {
	
	private EnvironmentHeight() {}
	
	
	public static Data generateEnvironmentHeightMap(final Height.Data heightData, EnvironmentStyles biome) {
		float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
		float[][] map = heightData.getMap().clone();
		
		for(int i =0; i<map.length; i++)for(int j=0; j<map[i].length; j++) {
			float newHeight = biome.getEnvironmentManipulatedHeight(map[i][j]);
			if(newHeight<min)min=newHeight;
			if(newHeight>max)max=newHeight;
			map[i][j]=newHeight;
		}
		
		return new Data(map, heightData, max, min);
	}
	
	
	
	public static final class Data extends HeightsDataClass{
		final Height.Data heightData;
		
		private Data(float[][] heights, Height.Data height, float max, float min) {
			super(heights, max, min);
			heightData = height;
		}
	}

}
