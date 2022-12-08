package objects.textures;

import rendering.loaders.Loader;
import rendering.loaders.LoaderTexIdentity;
import toolbox.StringUtils;

public enum Textures {
	
	PLAYER$TEXTURE,
	PERSON("animated/"),
	TREE,
	FERN{
		@Override
		public int getNumberOfRows() {
			return 2;
		}
	},
	PINE{
		@Override
		public boolean hasTransparency() {
			return true;
		}
	},
	GRASS$TEXTURE{
		@Override
		public boolean hasTransparency() {
			return true;
		}
		@Override
		public boolean useFakeLighting() {
			return true;
		}
	},
	APPLE$TREE_TEX("appleTree/textures/"){
		@Override
		protected void loadTexture() {
			setNormalMap(APPLE$TREE_NORMAL);
		}
	},
	APPLE$TREE_NORMAL("appleTree/textures/"),
	BOULDER(){
		@Override
		protected void loadTexture() {
			setNormalMap(BOULDER$NORMAL);
		}
	},
	BOULDER$NORMAL(),
	BOX(),
	MATERIAL(true),
	;
	
	private int uses = 0;
	private LoaderTexIdentity tex = null;
	private final String folderLocation;
	private final boolean isMaterial;
	
	private Textures normalMap = null;
	private Textures specularMap = null;
	
	private Textures() {
		this("res/");
	}
	
	private Textures(String folder) {
		folderLocation=folder;
		isMaterial=false;
	}
	
	private Textures(boolean a) {
		isMaterial=a;
		folderLocation = "";
		
	}
	
	public final Textures use() {
		if(tex==null) {
			System.out.println("Loading Texture: "+this.toLocationString());
			tex = new LoaderTexIdentity(this.toLocationString());
			loadTexture();
		}
		if(normalMap!=null) {
			normalMap.use();
		}
		if(specularMap!=null) {
			specularMap.use();
		}
		uses++;
		return this;
		
	}
	
	public final void unUse() {
		uses--;
		if(normalMap!=null) {
			normalMap.unUse();
		}
		if(specularMap!=null) {
			specularMap.unUse();
		}
		if(uses<=0 && tex !=null) {
			System.out.println("Unloading Texture: "+this.toLocationString());
			tex.delete();
			tex = null;
		}
	}
	
	public final String toLocationString() {
		if(isMaterial) {
			return "blank";
		}
		return folderLocation+StringUtils.enumToString(this);
	}
	
	protected final void setSpecularMap(Textures specMap) {
		this.specularMap=specMap;
	}
	
	public final boolean hasSpecularMap() {
		return specularMap!=null;
	}
	
	public final Textures getSpecularMap() {
		return specularMap;
	}

	public final Textures getNormalMap() {
		return normalMap;
	}

	protected final void setNormalMap(Textures normalMap) {
		this.normalMap = normalMap;
	}
	
	public final boolean hasNormalMap() {
		return normalMap!=null;
	}
	
	public final int getID(Loader loader) {
		return tex.getTexId(loader);
	}
	
	
//	Overridables
	
	public int getNumberOfRows() {
		return 1;
	}
	
	public boolean useFakeLighting() {
		return false;
	}

	public boolean hasTransparency() {
		return false;
	}	
	
	public float getShineDamper() {
		return 1;
	}

	public float getReflectivity() {
		return 0;
	}
	
	protected void loadTexture() {}
	
//	Statics
	
	public static void forceUnUseAll() {
		for(Textures t : values()) {
			t.uses=-1;
			t.unUse();
		}
	}

	public boolean useMaterial() {
		return isMaterial;
	}
	
}
