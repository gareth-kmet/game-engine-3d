package game;

import mainLoop.LoopManager.MainLoopRequiredVariables;

public interface GameLoopManager {
	
	public void create(MainLoopRequiredVariables mainLoopRequiredVariables);
	public void update(float seconds);
	public void destroy();

}
