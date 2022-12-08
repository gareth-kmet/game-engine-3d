package objects.models.animations;

import game.physics.collisions.data.CollisionData;
import game.physics.collisions.loaders.CollisionDataLoader;
import game.physics.collisions.loaders.CollisionDataLoader.Loaders;
import objects.models.Models;
import objects.models.RawModel;
import rendering.animation.model.AnimatedModel;
import rendering.animation.model.AnimatedModelLoader;
import toolbox.StringUtils;

public enum AnimatedModels implements Models{
	PERSON("animated"),
	ROBOT("animated/robot"),
	MODEL$A_IDLE("animated/modelA"),
	MODEL$B("animated/modelB"),
	
	ALPACA	(Loaders.Y_ROTATABLE_BOUNDING_BOX, "animated/animals/alpaca",	AnimalAnimations.values()),
	BULL	(Loaders.Y_ROTATABLE_BOUNDING_BOX, "animated/animals/bull", 	AnimalAnimations.values()),
	COW		(Loaders.Y_ROTATABLE_BOUNDING_BOX, "animated/animals/cow",		AnimalAnimations.values()),
	;
	
	private static final String DEFAULT_RES_LOCATION = "res";
	
	private int uses = 0;
	private AnimatedModel model = null;
	private final String folderLocation;
	private final Loaders collisionLoader;
	private CollisionData collisionData;
	private final float[] collisionConditionals;
	private final ModelAnimations[] animations;
	
	private AnimatedModels(Loaders collisionLoader, float[] collisionConditionals, String location, ModelAnimations...modelAnimations) {

		this.collisionConditionals=collisionConditionals;
		this.collisionLoader=collisionLoader;
		this.folderLocation=location;
		this.animations=modelAnimations;
	}
	
	private AnimatedModels(Loaders collisionLoader, String location, ModelAnimations...animations) {
		this(collisionLoader,new float[0],location, animations);
	}
	
	private AnimatedModels(String location, ModelAnimations...modelAnimations) {
		this(Loaders.BOUNDING_BOX, location, modelAnimations);
	}
	private AnimatedModels(ModelAnimations...modelAnimations) {
		this(DEFAULT_RES_LOCATION, modelAnimations);
	}
	
	public final String toLocationString() {
		return folderLocation+'/'+StringUtils.enumToString(this);
	}

	@Override
	public final RawModel use() {
		if(model==null) {
			System.out.println("Loading Animated Model: "+toLocationString());
			CollisionDataLoader load = collisionLoader.New(collisionConditionals);
			model = AnimatedModelLoader.loadEntity(toLocationString(), load);
			collisionData = load.getLoadedData();
			
			for(ModelAnimations anim : animations) {
				anim.use(this);
			}
		}
		uses++;
		return model;
	}

	@Override
	public final void unUse() {
		uses--;
		if(uses<=0 && model!=null) {
			System.out.println("Unloading Animated Model: "+toLocationString());
			model.delete();
			model=null;
			
			for(ModelAnimations anim : animations) {
				anim.unUse(this);
			}
		}
	}

	@Override
	public final RawModel getRawModel() {
		return model;
	}

	@Override
	public final CollisionData getCollisionData() {
		return collisionData;
	}

	@Override
	public final Loaders getCollisionType() {
		return collisionLoader;
	}

	@Override
	public final boolean hasResolutions() {
		return false;
	}

	@Override
	public final int getUses() {
		return uses;
	}

	@Override
	public final void setUses(int i) {
		this.uses=i;
	}

}
