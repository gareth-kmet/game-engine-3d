package game.world.terrain.heights;

import java.util.Random;

import game.world.WorldRandomStyles;

public enum FalloutHeightStyles implements WorldRandomStyles {
	
	TEST(1000,2000/*min+c*/,false){
		
		private final float b = 3, c = 1000;

		@Override
		public float getFallout(float x) {
			
			float height = f2(f2(-(x-c)))-c;
			
			return height;
		}
		
		private final float f2(float x) {
			x = x / c;
			
			final float x_b = (float) Math.pow(x,b), x_1_b = (float) Math.pow(1-x,b);
			
			float height = (x_b)/(x_b+x_1_b)*x;
			
			height = c * height;
			
			return height;
		}
		
	}
	
	
	
	
	;
	
	private final int minDist, maxDist;
	private final boolean isFactor;
	
	private FalloutHeightStyles(int minDist, int maxDist, boolean isFactor) {
		this.minDist=minDist;
		this.maxDist=maxDist;
		this.isFactor=isFactor;
	}
	
	protected abstract float getFallout(float dist);
	
	final float calcFallout(float dist) {
		//return getFallout(dist);
		
		if(dist<=minDist) {
			return 0;
		}
		if(dist>=maxDist) {
			//System.out.println(dist+" "+(maxDist-minDist)+" "+getFallout(maxDist-minDist));
			return getFallout(maxDist-minDist);
			
		}
//		System.out.println(dist+" "+(dist-minDist)+" "+getFallout(dist-minDist));
		return getFallout(dist-minDist);
		
	}
	
	public final void fallout(HeightsDataClass data, int x0, int z0) {
		
		float[][] map = data.getMap();
		for(int x=0; x<map.length; x++) {
			for(int z=0; z<map[x].length; z++) {
				
				int vx = x+x0;
				int vy = z+z0;
				int dist = vx*vx+vy*vy;
				
				float distSqrt = (float) Math.sqrt(dist);
				float fallout = calcFallout(distSqrt);
				
				float height = map[x][z];
				
				if(isFactor) {
					height*=fallout;
				}else {
					height+=fallout;
				}
				if(height<data.min)data.min=height;
				if(height>data.max)data.max=height;
				map[x][z] = height;
			}
		}
	}

	public static FalloutHeightStyles getRandomizedStyle(final Random random, final LandHeightStyles landStyle, final WaterHeightStyles waterStyle) {
		return values()[0];
	}
	
}
