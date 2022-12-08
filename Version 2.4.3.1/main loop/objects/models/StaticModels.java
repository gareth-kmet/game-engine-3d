package objects.models;

import game.physics.collisions.data.CollisionData;
import game.physics.collisions.loaders.CollisionDataLoader;
import rendering.loaders.ObjLoader;
import toolbox.StringUtils;

public enum StaticModels implements Models{
	PERSON(CollisionDataLoader.Loaders.Y_ROTATABLE_BOUNDING_BOX),
	TREE,
	PINE(CollisionDataLoader.Loaders.VERTICAL_SEGMENT_FULL_HEIGHT_BOUNDING_BOX,new float[] {1f/8f}),
	GRASS$MODEL,
	FERN,
	APPLE$TREE$APPLES("appleTree/"),
	APPLE$TREE$BARK(CollisionDataLoader.Loaders.VERTICAL_SEGMENT_FULL_HEIGHT_BOUNDING_BOX/* FURTHEST_POINT_SPHERE*/, new float[] {1f/8f}, "appleTree/", 0.1f, 0.25f, 0.5f),
	APPLE$TREE$LEAVES("appleTree/"),
	BOULDER("res/"),
	BOX(),
	
	;
	
	private int uses = 0;
	private RawModel model = null;
	private final String folderLocation;
	
	/**
	 * resolutions go from least to greatest resolution excluding the full resolution which is this object
	 */
	private final float[] resolutionsNums;
	private RawModel[] resolutions;
	private final int resolutions_length;
	private final boolean hasResolution;
	private final CollisionDataLoader.Loaders collisions;
	private CollisionData collisionData;
	private final float[] collisionConditionals;
	
	/**
	 * 
	 * @param folderLocation
	 * @param resolutions should be between 0(exclusive) and 1(inclusive) including the actual resolution as the last item
	 */
	private StaticModels(CollisionDataLoader.Loaders collisions, float[] conditionals, String folderLocation, float... resolutions) {
		this.resolutionsNums=resolutions;
		this.resolutions_length=resolutions.length-1;
		this.folderLocation=folderLocation;
		this.hasResolution=resolutions_length>0;
		this.collisions=collisions;
		this.collisionConditionals=conditionals;
	}
	
	private StaticModels(CollisionDataLoader.Loaders collisions, String folderLocation, float... resolutions) {
		this(collisions, new float[0], folderLocation, resolutions);
	}
	
	private StaticModels(String folderLocation, float ... resolutions) {
		this(CollisionDataLoader.Loaders.BOUNDING_BOX, folderLocation, resolutions);
	}
	
	private StaticModels(CollisionDataLoader.Loaders collisions) {
		this(collisions, new float[0], "res/");
	}
	
	private StaticModels(CollisionDataLoader.Loaders collisions, float[] conditionals) {
		this(collisions, conditionals, "res/");
	}
	
	private StaticModels() {
		this(CollisionDataLoader.Loaders.BOUNDING_BOX, new float[0]);
	}
	
	@Override
	public final RawModel use() {
		if(model == null) {
			System.out.println("Loading Model: "+toLocationString());
			CollisionDataLoader load = collisions.New(collisionConditionals);
			model = ObjLoader.loadOBJ(toLocationString(), load);
			collisionData = load.getLoadedData();
			loadResolutions();
		}
		uses++;
		return model;
	}
	
	@Override
	public final void unUse() {
		uses--;
		if(uses<=0 && model !=null) {
			System.out.println("Unloading Model: "+toLocationString());
			unLoadResolutions();
			model.delete();
			model = null;
		}
	}
	
	public final void loadResolutions() {
		if(!hasResolution)return;
		
		resolutions = new RawModel[resolutions_length];
		for (int i = 0; i < resolutions_length; i++) {
			resolutions[i] = ObjLoader.loadOBJ(toLocationString(resolutionsNums[i]), null);
		}
	}
	
	public final void unLoadResolutions() {
		if(!hasResolution)return;
		for(RawModel m : resolutions) {
			m.delete();
		}
		resolutions = null;
	}
	
	@Override
	public final int getUses() {
		return uses;
	}
	
	@Override
	public final void setUses(int i) {
		this.uses=i;
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
	public final CollisionDataLoader.Loaders getCollisionType(){
		return collisions;
	}
	
	@Override
	public final RawModel getRawModel(float resolution) {
		
		
		if(!hasResolution) {
			return model;
		}
		
		
		RawModel resObj;
		
		if(resolution<0) {
			resObj= resolutions[0];
		}else if(resolution>1) {
			resObj=model;
		}else {
			resolution *= resolutions_length;
			int res = (int)(resolution+0.5f);
			
			if(res >= resolutions_length) {
				resObj = model;
			}else {
				resObj=resolutions[res];
			}
			
		}
		
		return resObj;
		
		
	}
	
	public final String toLocationString() {
		if(hasResolution) {
			return toLocationString(resolutionsNums[resolutions_length]);
		}else {
			return toLocationString(toString());
		}
		
	}
	
	public final String toLocationString(float resolution) {
		String enumName = toString() + "/" + (int)(resolution*100);
		return toLocationString(enumName);
	}
	
	public final String toLocationString(String enumName) {
		return folderLocation + StringUtils.enumToString(enumName);
	}
	
	@Override
	public final boolean hasResolutions() {
		return hasResolution;
	}
	
	@Override
	public final RawModel getNewRawModel() {
		return getRawModel();
	}
	
//	Overridables
	
	
	
	
	
}
