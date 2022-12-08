package game.world.terrain;

import java.util.HashMap;
import java.util.HashSet;

import mainLoop.LoopManager.MainLoopRequiredVariables;

public class TerrainStorage {
	
	private final HashMap<Integer, HashMap<Integer, Terrain>> terrains = new HashMap<Integer, HashMap<Integer, Terrain>>();
	private boolean careAboutMemory = true;
	
	private HashSet<Terrain> lastRendered = new HashSet<Terrain>();
	
	void renderTerrains(MainLoopRequiredVariables mainLoopRequiredVariables, HashSet<Terrain> terrains) {
		
		for(Terrain t : terrains) {
			if(lastRendered.contains(t)) {
				lastRendered.remove(t);
				continue;
			}
			
			mainLoopRequiredVariables.terrains.add(t);
		}
		
		for(Terrain t : lastRendered) {
			mainLoopRequiredVariables.terrains.remove(t);
			
			if(careAboutMemory) {
				removeTerrain(t.x, t.z, t);
			}
			
		}
		
		lastRendered=terrains;
	}
	
	void removeTerrain(int x, int z, Terrain terrain) {
		
		HashMap<Integer, Terrain> stepIn = terrains.get(x);
		if(stepIn==null)return;
		
		Terrain t = stepIn.get(z);
		if(t==null || t != terrain)return;
		
		t.delete();
		
		stepIn.remove(z, t);
		
		if(stepIn.isEmpty()) {
			terrains.remove(x, stepIn);
		}
		
		
	}
	
	void setTerrain(int x, int z, Terrain terrain) {
		HashMap<Integer, Terrain> stepIn = terrains.get(x);
		if(stepIn==null) {
			stepIn = new HashMap<Integer, Terrain>();
			terrains.put(x, stepIn);
		}
		stepIn.put(z, terrain);
	}
	
	Terrain getTerrain(int x, int z) {
		HashMap<Integer, Terrain> stepIn = terrains.get(x);
		if(stepIn==null)return null;
		return stepIn.get(z);
	}

}
