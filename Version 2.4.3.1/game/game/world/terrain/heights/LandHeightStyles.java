package game.world.terrain.heights;

import java.util.Random;

import game.world.WorldRandomStyles;

public enum LandHeightStyles implements WorldRandomStyles{
	
	/**
	 * Equal water and land with shallow slope at 0.
	 */
	HILLY_ISLANDS(1.5f, 0){
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
	HILLY_LAND(1.75f,-0.3f){

		@Override
		public float calcHeight(float x) {
			
			final float d=0.5f, c=0.15f, j=2.7f, b=0.4f, k=1.37f, l=1.03f, a=3.8f, g=0.85f;
			
			
			
			x=x*d+c;
			float height = -(x*(j*(x*x+b)-l))/k * (x+a) * (x-g);
			
			return height;
			
		}
		
	},
	
	TEST(1,-1){

		@Override
		public float calcHeight(float x) {
			final float a = 0.4f;
			
			x+=1;
			x*=0.5f;
			
			final float xa = (float)Math.pow(x, a), x_1a = (float)Math.pow(1-x, a);
			
			float height = xa / (xa + x_1a);
			
			height *= 2;
			height -= 1;
			
			if(Float.isNaN(height)) {
				System.out.println(x*2-1 + " " + xa + " " + x_1a);
			}
			
			return height;
		}
		
	},
	
	TEST2(1,-0.2f){
		
		private final float a = 15f, q = 0.1f, j=0.4f, k=0.1f, b=3f;

		@Override
		protected float calcHeight(float x) {
			
			float height;
			
			if(x>j) {
				height = g3(x);
			}else if(x>k) {
				height = g1(x);
			}else {
				height = g2(x);
			}
			
			return height;
		}
		
		private float g1(float x) {
			return a * f1(x-1f-k) + 0.5f * a + g2(k);
		}
		
		private float g2(float x) {
			return f21(0.5f*x)+q;
		}
		
		private float g3(float x) {
			return f3(x-j)+g1(j);
		}
		
		private float f3(float x) {
			return x;
		}
		
		private float f1(float x) {
			return 0.5f*x*x+x;
		}
		
		private float f2(float x) {
			final float x_b = (float)Math.pow(x,b), x_1_b = (float)Math.pow(x-1,b);
			return (x_b) / (x_b - x_1_b);
		}
		
		private float f21(float x) {
			return 0.2f * f2(5*(x+0.1f))-0.1f;
		}
		
	},
	
	TEST3(1, -1){

		@Override
		protected float calcHeight(float x) {
			// TODO Auto-generated method stub
			return 0.01f;
		}
		
	}
	
	
	;
	
	final float scale;
	private final float xIntercept;
	
	private LandHeightStyles(float scale, float intercept) {
		this.scale = scale;
		this.xIntercept=intercept;
	}
	
	public final float getHeight(float x, WaterHeightStyles waterStyle) {
		return getHeightWithWaterTest(x, waterStyle)*1000;
	}
	
	private float getHeightWithWaterTest(float x, WaterHeightStyles waterStyle) {
		if(x<xIntercept) {
			return waterStyle.calcHeight(x-xIntercept);
		}else {
			return calcHeight(x);
			
		}
	}
	
	protected abstract float calcHeight(float x);
	
	public final float getScale() {
		return scale;
	}

	public static LandHeightStyles getRandomizedStyle(final Random random) {
		return HILLY_LAND;
	}

}
