package rendering.loaders;

import rendering.loaders.Loader.TEX;

public class LoaderTexIdentity {
	
	private String fileLocation;
	private TEX tex;
	private boolean isLoaded = false;
	
	public LoaderTexIdentity(String fileLocation) {
		this.fileLocation=fileLocation;
	}
	
	public LoaderTexIdentity(TEX tex) {
		this.tex=tex;
		isLoaded=true;
	}
	
	public TEX getTEX(Loader loader) {
		requireLoad(loader);
		return tex;
	}
	
	public int getTexId(Loader loader) {
		requireLoad(loader);
		return tex.id;
	}
	
	private void requireLoad(Loader loader) {
		if(!isLoaded) {
			tex = loader.loadGameTexture(fileLocation);
			fileLocation=null;
			isLoaded=true;
		}
	}

	public void delete() {
		if(tex!=null)Loader.addElementToDeletionList(tex);
		tex=null;
		fileLocation=null;
	}
}
