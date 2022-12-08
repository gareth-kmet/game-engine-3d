package mainLoop;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import mainLoop.LoopManager.GivenMainLoopRequiredVariables;
import objects.models.Models;
import objects.textures.Textures;
import rendering.MasterRenderer;
import rendering.guis.GuiTexture;
import rendering.loaders.Loader;

public final class MainLoop {
	
	
	static GuiTexture outlineGUI;
	
	static Loader loader;
	
	static LoopManager.Managers nextManager = LoopManager.Managers.GAME;
	static LoopManager currentManager;
	static Thread loopThread;
	
	static MasterRenderer renderer;
	
	
	private static void init() {
		
		DisplayManager.createDisplay();
		loader = new Loader();
		
		renderer = new MasterRenderer(loader);
		outlineGUI = renderer.createOutlineGui();
//		guis.add(outlineGUI); - TODO place this as a post processing
		
		runLoop();
		
		
	}
	
	private static void runLoop() {
		System.out.println("starting mainloop");
		do{
			updateDisplaySize();
			GivenMainLoopRequiredVariables variables = updateManager();
			if(variables!=null) {
				variables.camera().move();
				renderer.render(variables, new Vector4f(0,-1,0,10000));
			}
			Loader.deleteRequiringElements();
			DisplayManager.updateDisplay();
		}while(!Display.isCloseRequested() && currentManager!=null);
		System.out.println("ended mainLoop");
	}
	
	private static void updateDisplaySize() {
		if(Display.wasResized()) {
			renderer.cleanUp();
			renderer = new MasterRenderer(loader);
//			guis.remove(outlineGUI);
			outlineGUI = renderer.createOutlineGui();
//			guis.add(outlineGUI);
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
	}
	
	private static GivenMainLoopRequiredVariables updateManager() {
		if(currentManager==null||!currentManager.loopBoolean()) {
			System.out.println("removing current manager");
			stopLoopThread();
			currentManager=null;
			if(nextManager!=null) {
				System.out.println("changing current manager");
				currentManager = nextManager.getManagerNewInstant();
				loopThread = new Thread(currentManager);
				loopThread.start();
			}
		}
		if(currentManager==null) {
			return null;
		}else {
			return currentManager.lastCreatedVariables;
		}
	}
	
	private static void stopLoopThread() {
		if(loopThread!=null) {
			try {
				loopThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private static void end() {
		stopLoopThread();
		Models.forceUnUseAll();
		Textures.forceUnUseAll();
		if(currentManager!=null)currentManager.stop=true;
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}
	
	public static void main(String[] args) {
		init();
		end();
	}
	

}
