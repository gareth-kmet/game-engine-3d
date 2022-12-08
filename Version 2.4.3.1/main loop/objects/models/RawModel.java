package objects.models;

import rendering.loaders.Loader;
import rendering.loaders.LoaderVaoIdentity;

public class RawModel {
	
	protected final int vertexCount;
	protected LoaderVaoIdentity vao;
	
	protected boolean isDeleted = false;
	
	public RawModel(LoaderVaoIdentity vao, int vertexCount) {
		this.vao=vao;
		this.vertexCount=vertexCount;
	}
	
	/**
	 * Only should be called if it is know without a doubt that this object has a vao 
	 *
	 */
	public int forceGetVaoID() {
		return vao.getVaoId(null);
	}
	
	public int getVaoID(Loader loader) {
		return vao.getVaoId(loader);
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public void delete() {
		vao.delete();
		isDeleted=true;
	}
	
	public RawModel Copy() {
		return new RawModel(vao,vertexCount);
	}
	
	public boolean isAnimated() {
		return false;
	}
	
	
	
}
