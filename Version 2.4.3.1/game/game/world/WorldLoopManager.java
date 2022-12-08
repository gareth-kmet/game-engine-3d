package game.world;

import java.util.Random;

public interface WorldLoopManager{
	
	public void create(Random random);
	public default void create() {
		create(new Random());
	}
	void destroy();
	void update();

}
