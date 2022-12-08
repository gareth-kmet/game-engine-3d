package game.objects.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.lwjgl.util.vector.Vector3f;

import game.world.WorldVariables;

public abstract class GeniusGameEntity extends IntelligentGameEntity {
	private final HashSet<Intelligence> loadedIntelligence = new HashSet<Intelligence>(), needLoadedIntelligence = new HashSet<Intelligence>();
	private final HashSet<Intelligence> decidingIntelligence = new HashSet<Intelligence>();
	
	protected byte listsWithIntelligences = NULL_LIST;
	
	public static final byte 
		NULL_LIST		= 0b000,
		NEED_LOAD_LIST 	= 0b001,
		LOADED_LIST		= 0b010,
		DECIDING_LIST	= 0b100,
		ALL_LIST		= 0b111;
	

	protected GeniusGameEntity(Vector3f pos, Vector3f rot, float scale, float physicsMovementRatio) {
		super(pos, rot, scale, physicsMovementRatio);
	}
	
	protected abstract void makeOwnDecision(WorldVariables vars);
	
	@Override
	protected final void decide(WorldVariables vars) {
		makeOwnDecision(vars);
		doIntelligences(vars);
	}
	
	@SuppressWarnings("unchecked")
	private final void doIntelligences(WorldVariables worldVars) {
		for(Intelligence i : (Collection<Intelligence>)needLoadedIntelligence.clone()) {
			i.load(worldVars);
			if(i.loaded) {
				transferToList(i, NEED_LOAD_LIST, LOADED_LIST);
			}
		}
		for(Intelligence i : (Collection<Intelligence>)decidingIntelligence.clone()) {
			if(i.loaded)i.decide(worldVars);
			if(i.completed) {
				removeFromList(i, DECIDING_LIST);
			}
		}
	}
	
	
	
	public static abstract class Intelligence{
		
		public static final Intelligence BLANK_INTELLIGENCE = new Intelligence(null, true) {
			@Override protected void decide(WorldVariables worldVariables) {}
			@Override protected void load(WorldVariables worldVariables) {}
		};
		
		public static class TimedCompletionIntelligence extends Intelligence{

			private float time;
			public TimedCompletionIntelligence(IntelligentGameEntity thisEntity, float time) {
				super(thisEntity, true);
				this.time=time;
			}

			@Override
			protected void decide(WorldVariables worldVariables) {
				time-=worldVariables.currentFrameDeltaTime;
				if(time<=0) {
					completed=true;
					useless=true;
				}
			}
			@Override protected void load(WorldVariables worldVariables) {}
		}
		
		protected boolean completed=false;
		protected boolean loaded;
		protected boolean useless=false;
		protected final IntelligentGameEntity thisEntity;
		protected final GeniusGameEntity thisGenius;
		
		private byte currentLists = NULL_LIST;
		
		protected Intelligence(IntelligentGameEntity thisEntity, boolean alreadyLoaded) {
			this.thisEntity=thisEntity;
			loaded=alreadyLoaded;
			if(thisEntity instanceof GeniusGameEntity) {
				thisGenius=(GeniusGameEntity)thisEntity;
			}else {
				thisGenius=null;
			}
		}
		protected Intelligence(IntelligentGameEntity thisEntity) {
			this(thisEntity, false);
		}
		
		protected abstract void decide(WorldVariables worldVariables);
		protected abstract void load(WorldVariables worldVariables);
		
		public boolean isComplete() {
			return completed;
		}
		
		public boolean isLoaded() {
			return loaded;
		}
		
		public boolean isUseless() {
			return useless;
		}
		
		
		public byte getCurrentLists() {
			return currentLists;
		}
		
		
		
	}
	
	public void transferToList(Intelligence i, int from, int to) {
		removeFromList(i,from);
		addToList(i,to);
	}
	
	public void transferToList(Intelligence i, int to) {
		transferToList(i, i.currentLists, to);
	}
	
	public void addToList(Intelligence i, int list) {
		for(HashSet<Intelligence> l : getList(list)) {
			l.add(i);
		}
		i.currentLists |= list;
		listsWithIntelligences |= list;
	}
	
	public void removeFromList(Intelligence i, int list) {
		for(HashSet<Intelligence> l : getList(list)) {
			l.remove(i);
		}
		i.currentLists ^= list&i.currentLists;
		listsWithIntelligences ^= list&listsWithIntelligences;
	}
	
	private final ArrayList<HashSet<Intelligence>> getList(int list){
		ArrayList<HashSet<Intelligence>> lists = new ArrayList<HashSet<Intelligence>>();
		if(list!=NULL_LIST) {
			if((list&NEED_LOAD_LIST)>0)	lists.add(needLoadedIntelligence);
			if((list&LOADED_LIST)>0)	lists.add(loadedIntelligence);
			if((list&DECIDING_LIST)>0)	lists.add(decidingIntelligence);
		}
		return lists;
	}
}
