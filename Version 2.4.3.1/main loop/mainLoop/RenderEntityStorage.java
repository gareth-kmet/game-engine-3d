package mainLoop;

import java.util.EnumMap;
import java.util.HashSet;

import entities.RenderEntity;
import objects.TexturedModels;

public class RenderEntityStorage {
	
	public final EnumMap<TexturedModels, HashSet<RenderEntity>> 
		allEntities = new EnumMap<TexturedModels, HashSet<RenderEntity>>(TexturedModels.class);
	
	public void addEntity(RenderEntity entity) {
		addEntity(entity, allEntities);
	}
	
	public static void addEntity(RenderEntity entity, EnumMap<TexturedModels, HashSet<RenderEntity>> map) {
		TexturedModels entityModel = entity.getTexturedModel();
		HashSet<RenderEntity> batch = map.get(entityModel);
		if(batch!=null) {
			batch.add(entity);
		}else {
			HashSet<RenderEntity> newBatch = new HashSet<RenderEntity>();
			newBatch.add(entity);
			map.put(entityModel, newBatch);
		}
	}
	
	public void removeEntity(RenderEntity entity) {
		removeEntity(entity, allEntities);
		
		
	}
	
	public static void removeEntity(RenderEntity entity, EnumMap<TexturedModels, HashSet<RenderEntity>> map) {
		TexturedModels entityModel = entity.getTexturedModel();
		HashSet<RenderEntity> batch = map.get(entityModel);
		if(batch==null)return;
		batch.remove(entity);
		if(batch.size()==0) {
			map.remove(entityModel);
		}
	}
	
	
}
