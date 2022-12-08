package mainLoop;

import rendering.MasterRenderer;

/**
 * 
 * I absolutely hate the use of static methods for all this when its all instance stuff. This needs to be changed in the future and cannot stay but other stuff is more import. 
 * Easy way out is to create a UniverseVariables and have WorldVariables have access to this information.
 *
 */
public final class MainLoopVariables {
	
	public static LoopManager.Managers getNextManager() {
		return MainLoop.nextManager;
	}

	public static void setNextManager(LoopManager.Managers nextManager) {
		LoopManager.Managers.setNextManager(nextManager);
	}

	public static MasterRenderer getMasterRenderer() {
		return MainLoop.renderer;
	}
	
	
	private MainLoopVariables() {
		throw new IllegalAccessError();
	}

}
