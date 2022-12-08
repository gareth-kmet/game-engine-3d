package game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import game.world.WorldManager;
import mainLoop.LoopManager;
import rendering.RenderConditions;

public final class GameManager extends LoopManager{
	
	private WorldManager world;
	

	@Override
	public boolean create() {
		
		world = new WorldManager();
		world.create(mainLoopVariables);
		mainLoopVariables.renderConditions = RenderConditions.LoopCondition.GAME.addLoopConditionTo(world.getStyles());
//		TODO - set camera
		
//		FontType font = new FontType(new LoaderTexIdentity("font/candara"), new File(Loader.RES_LOCATION+"/font/candara.fnt"));
//		GUIText text = new GUIText("A sample string of text!", 1, font, new Vector2f(0.0f,0.0f), 1f, true);
//		text.setColour(0.1f, 0.1f, 0.1f);
		
		
		return false;
	}

	private boolean keyDownA = false;
	
	@Override
	public void loopStep() {
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			if(!keyDownA) {
				Mouse.setGrabbed(!Mouse.isGrabbed());
			}
			keyDownA=true;
		}else {
			keyDownA=false;
		}
		
		world.update(frameTimeSeconds);
		
	}

	@Override
	public boolean loopBoolean() {
		return true;
	}

	@Override
	public void cleanUp() {
		world.destroy();
		
	}

	
	

}
