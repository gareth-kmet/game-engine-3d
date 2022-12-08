package rendering.particles;

public class ParticleTexture {
	
	private int textureID, numberOfRows;
	
	private boolean additive;
	
	

	public ParticleTexture(int textureID, int numberOfRows, boolean additive) {
		this.additive=additive;
		this.textureID = textureID;
		this.numberOfRows = numberOfRows;
	}
	
	

	protected boolean isAdditive() {
		return additive;
	}



	protected int getTextureID() {
		return textureID;
	}

	protected int getNumberOfRows() {
		return numberOfRows;
	}
	
	

}
