package rendering.loaders;

import rendering.loaders.Loader.LoaderInfo;
import rendering.loaders.Loader.VAO;

public class LoaderVaoIdentity {
	
	private LoaderInfo info;
	private VAO vao;
	private boolean isLoaded = false;
	
	public LoaderVaoIdentity(LoaderInfo info) {
		this.info=info;
	}
	
	public LoaderVaoIdentity(VAO vao) {
		this.vao=vao;
		info=null;
		isLoaded=true;
	}
	
	public VAO getVAO(Loader loader) {
		requireLoad(loader);
		return vao;
	}
	
	public int getVaoId(Loader loader) {
		requireLoad(loader);
		return vao.id;
	}
	
	private void requireLoad(Loader loader) {
		if(!isLoaded) {
			vao = info.loadToVAO(loader);
			info=null;
			isLoaded=true;
		}
	}

	public void delete() {
		if(vao!=null)Loader.addElementToDeletionList(vao);
		vao=null;
		info=null;
	}
}
