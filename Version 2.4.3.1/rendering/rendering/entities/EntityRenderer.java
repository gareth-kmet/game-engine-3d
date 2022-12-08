package rendering.entities;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import entities.RenderEntity;
import mainLoop.DisplayManager;
import mainLoop.LoopManager.GivenMainLoopRequiredVariables;
import objects.TexturedModels;
import objects.models.RawModel;
import objects.textures.Textures;
import rendering.OpenGlUtils;
import rendering.ShaderProgram;
import rendering.loaders.Loader;
import rendering.skybox.SkyBoxVariables;
import toolbox.Maths;

public class EntityRenderer {

	private EntityRegularShader regularShader;
	private EntityNormalShader normalShader;
	private final Loader loader;
	
	private final Sorter sorter = new Sorter();

	public EntityRenderer(Loader loader, Matrix4f projectionMatrix) {
		normalShader = new EntityNormalShader();
		normalShader.start();
		normalShader.loadProjectionMatrix(projectionMatrix);
		normalShader.connectTextureUnits();
		normalShader.stop();
		
		regularShader = new EntityRegularShader();
		regularShader.start();
		regularShader.loadProjectionMatrix(projectionMatrix);
		regularShader.connectTextureUnits();
		regularShader.stop();
		
		this.loader=loader;
		sorter.start();
	}
	
	private class Sorter extends Thread{
		private boolean stop=false;
		private int sorts = -1;
		/**
		 * All clones
		 */
		private EnumMap<TexturedModels, HashSet<RenderEntity>> 
			nextEntitiesToLoad = new EnumMap<TexturedModels, HashSet<RenderEntity>>(TexturedModels.class),
			currentEntitiesGettingSorted = null;
		private EnumMap<Textures, HashMap<RawModel, HashSet<RenderEntity>>> 
			lastLoadedReflectionEntities = new EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>(Textures.class),
			lastLoadedRegularEntities = new EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>(Textures.class),
			lastLoadedNormalEntities = new EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>(Textures.class);
		
		@Override
		public void run() {
			while(!stop) {
				sort();
			}
		}
		
		private void sort() {
			sorts++;
			currentEntitiesGettingSorted = nextEntitiesToLoad;
			EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>
				lastLoadedReflectionEntities = new EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>(Textures.class),
				lastLoadedRegularEntities = new EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>(Textures.class),
				lastLoadedNormalEntities = new EnumMap<Textures, HashMap<RawModel,HashSet<RenderEntity>>>(Textures.class);
			
			for(TexturedModels texturedModel : currentEntitiesGettingSorted.keySet()) {
				if(stop)return;
				
				Textures texture = texturedModel.getTexture();
				final boolean testForNormal = texture.hasNormalMap();
				final HashMap<RawModel, HashSet<RenderEntity>> normalTexturedModels, regularTexturedModels, reflectionTexturedModels;
			
				if(testForNormal)normalTexturedModels = lastLoadedNormalEntities.getOrDefault(texture, new HashMap<RawModel, HashSet<RenderEntity>>());
				else normalTexturedModels = null;
				
				regularTexturedModels = lastLoadedRegularEntities.getOrDefault(texture, new HashMap<RawModel, HashSet<RenderEntity>>());
				reflectionTexturedModels = lastLoadedReflectionEntities.getOrDefault(texture, new HashMap<RawModel, HashSet<RenderEntity>>());
				
				for(RenderEntity e : currentEntitiesGettingSorted.get(texturedModel)) {
					if(stop)return;
					
					if(e.precondition(sorts) && !e.isDeleted()) {
						regularAndNormalSort: {
							final RawModel raw = e.getRawModel(true,sorts);
							final HashMap<RawModel, HashSet<RenderEntity>> hash;
							if(testForNormal && e.canUseNormalMap(sorts)) {
								hash = normalTexturedModels;
							}else {
								hash = regularTexturedModels;
							}
							hash.compute(raw, ACCEPT_ENTITY).add(e);
						}
						reflectionSort: {
							final RawModel raw = e.getRawModel(0, sorts);
							reflectionTexturedModels.compute(raw, ACCEPT_ENTITY).add(e);
						}
					}
				}
				
				
				if(				  	!regularTexturedModels		.isEmpty())		lastLoadedRegularEntities	.putIfAbsent(texture, regularTexturedModels		);
				if(				  	!reflectionTexturedModels	.isEmpty())		lastLoadedReflectionEntities.putIfAbsent(texture, reflectionTexturedModels	);
				if(testForNormal &&	!normalTexturedModels		.isEmpty())		lastLoadedNormalEntities 	.putIfAbsent(texture, normalTexturedModels		);

			}
			
			this.lastLoadedNormalEntities=lastLoadedNormalEntities;
			this.lastLoadedReflectionEntities=lastLoadedReflectionEntities; 
			this.lastLoadedRegularEntities=lastLoadedRegularEntities;
		}
		
		private static final BiFunction<RawModel, HashSet<RenderEntity>, HashSet<RenderEntity>> ACCEPT_ENTITY = (k,s) -> s == null ? new HashSet<RenderEntity>() : s;
		
	}
	
	public enum RenderType{REFLECTION,REGULAR};
	
	private int lastFrameSetEntities = -1;

	@SuppressWarnings("unchecked")
	public void render(Vector4f clipPlane, GivenMainLoopRequiredVariables mainLoopVariables, RenderType renderType) {
//		long time0 = System.currentTimeMillis();
		
		final int frameId = DisplayManager.getCurrentFrameID();
		if(frameId!=lastFrameSetEntities) {
			lastFrameSetEntities=frameId;
			EnumMap<TexturedModels, HashSet<RenderEntity>> cloneList = new EnumMap<TexturedModels, HashSet<RenderEntity>>(TexturedModels.class);
			mainLoopVariables.entities().forEach((key, set) -> cloneList.put(key, (HashSet<RenderEntity>) set.clone()));
			sorter.nextEntitiesToLoad = cloneList;
		}
		
//		long time1 = System.currentTimeMillis();
		
		switch(renderType) {
			case REGULAR:
				render(sorter.lastLoadedNormalEntities, clipPlane, mainLoopVariables.lights(), mainLoopVariables.camera(), normalShader);
				render(sorter.lastLoadedRegularEntities, clipPlane, mainLoopVariables.lights(), mainLoopVariables.camera(), regularShader);
				break;
			default:
				render(sorter.lastLoadedReflectionEntities, clipPlane, mainLoopVariables.lights(), mainLoopVariables.camera(), regularShader);
				break;
		}
		
//		long time2 = System.currentTimeMillis();
//		System.out.println("RENDER "+(time1-time0)+" "+(time2-time1)+" "+(time2-time0));
		
	}
	
	public void render(EnumMap<Textures, HashMap<RawModel, HashSet<RenderEntity>>> entities, Vector4f clipPlane, List<Light> lights, Camera camera, EntityShader shader) {
		
		if(entities.isEmpty())return;
//		long totalDrawTime = 0l;
//		long nowTime = System.currentTimeMillis();
		shader.start();
		prepare(clipPlane, lights, camera, shader);
//		System.out.println(camera.getPosition().toString());
		for(Textures texture : entities.keySet()) {
			prepareModelTexture(texture, shader);
			for (RawModel model : entities.get(texture).keySet()) {
				prepareTexturedModel(model, shader);
				HashSet<RenderEntity> batch = entities.get(texture).get(model);
				for (RenderEntity entity : batch) {
//					if(!entity.canRender())continue; already tested in the sorter
					if(!entity.isDeleted() && !entity.isForceNoRender()) {
						prepareInstance(entity, shader);
//						long time1 = System.currentTimeMillis();
						GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
//						long time2 = System.currentTimeMillis();
//						totalDrawTime+=time2-time1;
					}else {
						entity.resetForceRender();
					}
					
					
				}
				unbindTexturedModel(model);
			}
		}
		shader.stop();
//		long endTime = System.currentTimeMillis();
//		System.out.println("Full render: "+(endTime-nowTime)+" Total draw time: "+totalDrawTime);
	}
	
	public void cleanUp(){
		normalShader.cleanUp();
		regularShader.cleanUp();
		sorter.stop=true;
		try {
			sorter.interrupt();
			sorter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareModelTexture(Textures texture, EntityShader shader) {
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if (texture.hasTransparency()) {
			OpenGlUtils.cullBackFaces(false);
		}
		shader.loadFakeLightingVariable(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID(loader));
		if(texture.hasNormalMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getNormalMap().getID(loader));
		}
		shader.loadUseSpecularMap(texture.hasSpecularMap());
		if(texture.hasSpecularMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap().getID(loader));
		}
		shader.loadUseMaterial(texture.useMaterial());
	}

	private void prepareTexturedModel(RawModel model, EntityShader shader) {
		OpenGlUtils.antialias(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		GL30.glBindVertexArray(model.getVaoID(loader));
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		
		boolean animated = model.isAnimated();
		shader.loadUseAnimation(animated);
		if(animated) {
			GL20.glEnableVertexAttribArray(4);
			GL20.glEnableVertexAttribArray(5);
		}

	}

	private void unbindTexturedModel(RawModel model) {
		OpenGlUtils.cullBackFaces(true);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		if(model.isAnimated()) {
			GL20.glDisableVertexAttribArray(4);
			GL20.glDisableVertexAttribArray(5);
		}
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(RenderEntity entity, EntityShader shader) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		/*if(entity.getTexturedModel()==TexturedModels.PLAYER_BOX) {
			transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 0,
					0, 0, entity.getScale());
		}*/
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
		
		if(entity.hasAnimation()) {
			shader.loadAnimationTransforms(entity.getAnimator().getCurrentJointTransforms());
		}
	}

	private void prepare(Vector4f clipPlane, List<Light> lights, Camera camera, EntityShader shader) {
		shader.loadClipPlane(clipPlane);
		shader.loadSky(SkyBoxVariables.Fog.colour, SkyBoxVariables.Fog.density, SkyBoxVariables.Fog.gradient);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
	}
	
	abstract sealed static class EntityShader extends ShaderProgram permits EntityRegularShader, EntityNormalShader{

		public EntityShader(String vertexFile, String fragmentFile) {
			super(vertexFile, fragmentFile);
		}
		
		protected abstract void loadNumberOfRows(int numberOfRows);
		protected abstract void loadFakeLightingVariable(boolean useFake);
		protected abstract void loadShineVariables(float damper,float reflectivity);
		protected abstract void loadUseSpecularMap(boolean useMap);
		protected abstract void loadTransformationMatrix(Matrix4f matrix);
		protected abstract void loadOffset(float x, float y);
		protected abstract void loadClipPlane(Vector4f plane);
		protected abstract void loadSky(Vector3f colour, float density, float gradient);
		protected abstract void loadLights(List<Light> lights, Matrix4f viewMatrix);
		protected abstract void loadViewMatrix(Matrix4f viewMatrix);
		protected abstract void loadUseAnimation(boolean use);
		protected abstract void loadAnimationTransforms(Matrix4f[] transforms);
		protected abstract void loadUseMaterial(boolean use);
		
	}
}
