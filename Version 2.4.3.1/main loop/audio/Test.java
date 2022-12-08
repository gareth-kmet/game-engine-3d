package audio;

import java.io.IOException;

import org.lwjgl.openal.AL10;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		AudioMaster.init();
		AudioMaster.setListenerData(0,0,0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		int buffer = AudioMaster.loadSound("audio/bounce.wav");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		
		Source source2 = new Source();
		source2.setPitch(2);
		
		float xPos = 0;
		source.setPosition(0, 0, xPos);
		
		char c = ' ';
		while(c!='q') {
			xPos += 0.02f;
			source.setPosition(0, 0, xPos);
			System.out.println(xPos);
			Thread.sleep(10);
		}
		
		source.delete();
		AudioMaster.cleanUp();
	}
}
