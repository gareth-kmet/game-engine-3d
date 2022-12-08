package objects.models;

import game.physics.collisions.data.CollisionData;
import game.physics.collisions.loaders.CollisionDataLoader;
import objects.models.animations.AnimatedModels;
import objects.models.animations.ModelAnimations;

public interface Models {
	
	public RawModel use();
	public void unUse();
	public RawModel getRawModel();
	public default RawModel getRawModel(float res) 		{return getRawModel();}
	public default RawModel getNewRawModel() 			{return getRawModel().Copy();}
	public CollisionData getCollisionData();
	public CollisionDataLoader.Loaders getCollisionType();
	public boolean hasResolutions();
	public int getUses();
	public void setUses(int i);
	
	public static void forceUnUseAll() {
		for(StaticModels t : StaticModels.values()) {
			t.setUses(-1);
			t.unUse();
		}
		for(AnimatedModels t : AnimatedModels.values()) {
			t.setUses(-1);
			t.unUse();
		}
		
		ModelAnimations.forceUnUseAll();
	}


}
