package game.world.terrain.heights;

import java.util.Random;

import game.world.WorldRandomStyles;

public enum WaterHeightStyles implements WorldRandomStyles {
	

	/**
	 * Equal water and land with shallow slope at 0.
	 */
	HILLY_ISLANDS(){
		@Override
		public float calcHeight(float x) {
			
			float h = x*(x*x+0.1f)*(x-1.25f)*(x+1.25f);
			h*=-1;
			
			return h;
		}
	},
	
	/**
	 * Unequal land and water with a shallow slope at -0.3
	 * Water has a higher slope than land.
	 * 
	 */
	HILLY_LAND(){

		@Override
		public float calcHeight(float x) {
			
			final float d=0.5f, c=0.15f, j=2.7f, b=0.4f, k=1.37f, l=1.03f, a=3.8f, g=0.85f, m=4.3f;
			
			
			
			x=x*d+c;
			float height = -(x*(j*(x*x+b)-l))/k * (x+a) * (x-g);
			height = m*height;
			
			return height;
			
		}
		
	},
	
	TEST(){

		@Override
		public float calcHeight(float x) {
			return 1f/(10f*(x-0.3f)*(x-0.3f))-10f/9f;
		}
		
	},
	
	TEST2(){

		@Override
		protected float calcHeight(float x) {
			
			return -4*x*x;
		}
		
	}
	
	
	;
	
	protected abstract float calcHeight(float x);

	public static WaterHeightStyles getRandomizedStyle(final Random random, final LandHeightStyles landStyle) {
		return HILLY_ISLANDS;
	}

}
