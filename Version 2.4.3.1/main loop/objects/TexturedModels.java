package objects;

import objects.models.Models;
import objects.models.StaticModels;
import objects.models.animations.AnimatedModels;
import objects.textures.Textures;

public enum TexturedModels {
	PLAYER(StaticModels.PERSON, Textures.PLAYER$TEXTURE),
	PERSON(AnimatedModels.PERSON, Textures.PERSON),
	ROBOT(AnimatedModels.ROBOT, Textures.BOX),
	TREE,
	PINE,
	GRASS(StaticModels.GRASS$MODEL, Textures.GRASS$TEXTURE),
	FERN,
	APPLE_TREE_APPLES(StaticModels.APPLE$TREE$APPLES, Textures.APPLE$TREE_TEX),
	APPLE_TREE_BARK(StaticModels.APPLE$TREE$BARK, Textures.APPLE$TREE_TEX),
	APPLE_TREE_LEAVES(StaticModels.APPLE$TREE$LEAVES, Textures.APPLE$TREE_TEX),
	BOULDER(StaticModels.BOULDER, Textures.BOULDER),
	BOX(StaticModels.BOX, Textures.BOX),
	MODEL_B(AnimatedModels.MODEL$B, Textures.BOX),
	BULL(AnimatedModels.BULL, Textures.MATERIAL),
	ALPACA(AnimatedModels.ALPACA, Textures.MATERIAL)
	;
	
	
	private final Models model;
	private final Textures texture;
	
	private int uses = 0;
	
	private TexturedModels(Models model, Textures texture) {
		this.model=model;
		this.texture=texture;
	}
	
	private TexturedModels(Models model) {
		this.model = model;
		this.texture=Textures.valueOf(name());
	}
	
	private TexturedModels(Textures texture) {
		this.model = StaticModels.valueOf(name());
		this.texture=texture;
	}
	
	private TexturedModels() {
		this.model = StaticModels.valueOf(name());
		this.texture=Textures.valueOf(name());
	}
	
	public TexturedModels use() {
		model.use();
		texture.use();
		uses++;
		return this;
	}
	
	public void unUse() {
		uses--;
		model.unUse();
		texture.unUse();
	}

	public Models getModel() {
		return model;
	}

	public Textures getTexture() {
		return texture;
	}

	public int getUses() {
		return uses;
	}
	
	

}
