package game.world;

import java.util.Random;

import game.world.environment.styles.EnvironmentStyles;
import game.world.terrain.heights.FalloutHeightStyles;
import game.world.terrain.heights.LandHeightStyles;
import game.world.terrain.heights.WaterHeightStyles;
import rendering.RenderConditions;

public interface WorldRandomStyles extends RenderConditions{
	
	public static final byte 
		LAND_HEIGHT_STYLE					= 0b000001,
		WATER_HEIGHT_STYLE					= 0b000010,
		FALLOUT_STYLE						= 0b000100,
		EVIRONMENT_STYLE					= 0b001000,
		BIOME_STYLE							= 0b010000;
	
	public static WorldRandomStyles getStyle(RenderConditions[] styles, byte style) {
		RenderConditions r = null;
		switch(style) {
			case LAND_HEIGHT_STYLE: 
				r=styles[0];
				break;
			case WATER_HEIGHT_STYLE: 
				r=styles[1];
				break;
			case FALLOUT_STYLE: 
				r=styles[2];
				break;	
			case EVIRONMENT_STYLE: 
				r=styles[3];
				break;
			case BIOME_STYLE:
				r=styles[4];
			default: break;
		}
		
		return (WorldRandomStyles)r;
	}
	
	public static RenderConditions[] getRandomStyles(Random random) {
		
		RenderConditions[] styles = new RenderConditions[4];
		
		styles[0] = LandHeightStyles.getRandomizedStyle(random);
		styles[1] = WaterHeightStyles.getRandomizedStyle(random, (LandHeightStyles)styles[0]);
		styles[2] = FalloutHeightStyles.getRandomizedStyle(random, (LandHeightStyles)styles[0], (WaterHeightStyles)styles[1]);
		styles[3] = EnvironmentStyles.getRandomizedStyle(random, (LandHeightStyles)styles[0], (WaterHeightStyles)styles[1], (FalloutHeightStyles)styles[2]);
		
		return styles;
	}
	

}
