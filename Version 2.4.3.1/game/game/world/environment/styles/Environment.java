package game.world.environment.styles;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import game.objects.GameObject;
import game.world.WorldVariables;
import game.world.terrain.TerrainGenerator.Vertex;
import rendering.loaders.Loader;
import rendering.loaders.LoaderTexIdentity;

public interface Environment{
	
	public static final String NORMAL_TEXTURE_IDENTIFIER = "_normal";
	
	public static final int MAX_GL_TEXTURES = 10, MIN_GL_TEXTURE_NUM=10; 
	
	public static enum TEX_FLAGS{
		
		T_TEX_0(0),
		T_TEX_1(1),
		T_TEX_2(2),
		T_TEX_3(3),
		T_TEX_4(4),
		T_TEX_5(5),
		T_TEX_6(6),
		T_TEX_7(7),
		T_TEX_8(8),
		T_TEX_9(9),
		T_TEX_NULL(-1)
		
		;
		
		private static final int 
			ALL_TEXTURE_FULL_VALUE_FLAG	= 0b00_11_11_11_11_11_11_11_11_11_11;
		
		public static final int
			NULL_TEX_FLAG 		= 0b00 << 20,
			ALL_TEX_FLAG  		= 0b11 << 20,
			SINGLE_TEX_FLAG		= 0b01 << 20,
			NOT_ALL_TEX__FLAG	= 0b10 << 20
		;
		
		public static final int
			NULL_VALUE_FLAG	= 0b00,
			QUATER_VALUE_FLAG = 0b01,
			HALF_VALUE_FLAG	= 0b10,
			FULL_VALUE_FLAG	= 0b11
		;
		
		private final int flag;
		public final int ID;
		
		private TEX_FLAGS(int texValue) {
			ID = texValue*2;
			if(texValue == -1) {
				flag = 0;
			}else {
				flag = 0b11 << texValue*2;
			}
		}
		
		public int flag() {
			return flag;
		}
		
		public int flag(int valueFlag) {
			if(valueFlag > FULL_VALUE_FLAG) {
				valueFlag = FULL_VALUE_FLAG;
			}
			return flag & (valueFlag << ID);
		}
		
		public static final int addTexValueFlag(final int FLAG, int numOfTextures) {
			if(FLAG==0 || numOfTextures == 0) {
				return NULL_TEX_FLAG;
			}
			if(numOfTextures == 1) {
				return FLAG | SINGLE_TEX_FLAG;
			}
			if(numOfTextures >= 2 && numOfTextures < 10) {
				return FLAG | NOT_ALL_TEX__FLAG;
			}
			
			boolean allFull = true;
			for(int i = 0; i<10; i++) {
				TEX_FLAGS flag = values()[i];
				int fullValueFlag = FULL_VALUE_FLAG << i*2;
				
				if((FLAG & flag.flag) != fullValueFlag) {
					allFull = false;
					break;
				}
			}
			
			if(allFull) {
				return ALL_TEX_FLAG;
			}else {
				return NOT_ALL_TEX__FLAG;
			}
		}
		
		public static final int addTexValueFlag(final int FLAG) {
			if(FLAG == 0) {
				return NULL_TEX_FLAG;
			}else if((FLAG & ALL_TEXTURE_FULL_VALUE_FLAG) == ALL_TEXTURE_FULL_VALUE_FLAG) {
				return FLAG | ALL_TEX_FLAG;
			}
			
			int num = 0;
			for(TEX_FLAGS flag : values()) {
				if((FLAG & flag.flag) > 0) {
					num++;
					if(num > 1) {
						break;
					}
				}
			}
			
			if(num==0) {
				return FLAG | NULL_TEX_FLAG;
			}else if(num==1) {
				return FLAG | SINGLE_TEX_FLAG;
			}else {
				return FLAG | NOT_ALL_TEX__FLAG;
			}
		}
	}
	
	/**
	 * 
	 * @param texNum - (0-9)
	 * @return GL_TEXTURE[10-19]
	 */
	public static int getGLTextureConstant(int texNum) {
		if(texNum < 0 || texNum > 9) {
			throw new IllegalArgumentException();
		}
		return GL13.GL_TEXTURE10+texNum;
	}
	
	
	
	public abstract LoaderTexIdentity[] getTextures();
	public abstract LoaderTexIdentity[] getNormalTextures();
	public abstract int getHasNormalTextures();
	public abstract String[] getTextureLocations();
	public abstract void generateTextures(Vertex[][] vertecies);
	public abstract void generateTexture(Vertex[][] vertecies, int x, int y);
	public abstract void init();
	public abstract float getEnvironmentManipulatedHeight(float height);
	
	public abstract GameObject dealWithLargeGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars);
	public abstract GameObject dealWithMediumGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars);
	public abstract GameObject dealWithSmallGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars);
	public default  GameObject dealWithOtherGenerationPoint(Vector3f pos, Random random, WorldVariables worldVars) {return null;}
	
	
	
	
	public static LoaderTexIdentity[] loadTextures(final Environment e) {
		final String[] locations = e.getTextureLocations();
		final int hasNormalLocations = e.getHasNormalTextures();
		
		final int length = Math.min(locations.length, MAX_GL_TEXTURES);
		
		final LoaderTexIdentity[] textures = new LoaderTexIdentity[length];
		final LoaderTexIdentity[] normalTextures = new LoaderTexIdentity[length];
		
		for (int i = 0; i < length; i++) {
			textures[i] = new LoaderTexIdentity(locations[i]);
			if((hasNormalLocations&(1<<i))>0) {
				normalTextures[i] = new LoaderTexIdentity(locations[i]+NORMAL_TEXTURE_IDENTIFIER);
			}
		}
		
		return textures;
	}
	
	public default void bindTextures(Loader loader) {
		LoaderTexIdentity[] textures = getTextures();
		for (int i = 0; i < textures.length; i++) {
			GL13.glActiveTexture(getGLTextureConstant(i));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i].getTexId(loader));
		}
	}
}
