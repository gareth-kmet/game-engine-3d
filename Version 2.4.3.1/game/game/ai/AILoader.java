package game.ai;

import java.util.ArrayList;

public class AILoader extends Thread {
	private final ArrayList<Request> requests = new ArrayList<Request>();
	boolean stop=false;
	
	boolean hasCompleted = false;
	
	@Override
	public synchronized void start() {
		stop=false;
		super.start();
	}
	
	public void destroy() {
		this.stop=true;
		try {
			interrupt();
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(!stop) {
			if(requests.isEmpty()) {
				Thread.yield();
				continue;
			}
			runRequest();
		}
	}
	
	private void runRequest() {
		//System.out.println(requests.toString());
		Request request = requests.get(0);
		requests.remove(0);
		if(request.complete || stop || request.remove) {
			return;
		}
		request.complete=request.run();
		hasCompleted=request.complete;
		
		if(!request.complete) {
			requests.add(request);	
		}else {
			
		}
	}
	
	public static abstract class Request {
		
		private boolean complete = false;
		private boolean remove = false;
		
		public final void addToThread(AILoader thread) {
			thread.requests.add(this);
		}
		
		public final void removeFromThread() {
			remove=true;
		}
		
		protected abstract boolean run();
		
		public boolean isComplete() {
			return complete;
		}

	}

}
