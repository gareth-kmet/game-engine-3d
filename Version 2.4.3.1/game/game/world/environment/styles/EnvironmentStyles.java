package game.world.environment.styles;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import game.objects.GameObject;
import game.world.WorldRandomStyles;
import game.world.WorldVariables;
import game.world.terrain.TerrainGenerator.Vertex;
import game.world.terrain.heights.FalloutHeightStyles;
import game.world.terrain.heights.LandHeightStyles;
import game.world.terrain.heights.WaterHeightStyles;
import rendering.loaders.LoaderTexIdentity;

public enum EnvironmentStyles implements WorldRandomStyles, Environment{
	
	NORMAL(new NormalEnvironment());
	
	;
	
	
	private final Environment environment;
	
	private EnvironmentStyles(Environment e) {
		environment=e;
	}
	
	public static EnvironmentStyles getRandomizedStyle(Random random, LandHeightStyles landStyle, WaterHeightStyles waterStyle, FalloutHeightStyles falloutStyle) {
		return values()[0];
	}
	
	public static void environmentInit() {
		for(EnvironmentStyles style : values()) {
			style.init();
		}
	}
	
	public Environment getEnvironment() {
		return environment;
	}


	@Override
	public LoaderTexIdentity[] getTextures() {
		return environment.getTextures();
	}

	@Override
	public String[] getTextureLocations() {
		return environment.getTextureLocations();
	}

	@Override
	public void init() {
		environment.init();
	}

	@Override
	public void generateTextures(Vertex[][] vertecies) {
		environment.generateTextures(vertecies);
		
	}

	@Override
	public void generateTexture(Vertex[][] vertecies, int x, int y) {
		environment.generateTexture(vertecies, x, y);
		
	}

	@Override
	public float getEnvironmentManipulatedHeight(float height) {
		// TODO Auto-generated method stub
		return environment.getEnvironmentManipulatedHeight(height);
	}

	@Override
	public GameObject dealWithLargeGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars) {
		return environment.dealWithLargeGenerationPoint(pos, random, worldVars);
	}

	@Override
	public GameObject dealWithMediumGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars) {
		return environment.dealWithMediumGenerationPoint(pos, random, worldVars);
	}

	@Override
	public GameObject dealWithSmallGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars) {
		return environment.dealWithSmallGenerationPoint(pos, random, worldVars);
	}
	
	@Override
	public GameObject dealWithOtherGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars) {
		return environment.dealWithOtherGenerationPoint(pos, random, worldVars);
	}

	@Override
	public LoaderTexIdentity[] getNormalTextures() {
		return environment.getNormalTextures();
	}

	@Override
	public int getHasNormalTextures() {
		return environment.getHasNormalTextures();
	}

}
