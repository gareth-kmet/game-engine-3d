package rendering;

import java.util.Arrays;

public interface RenderConditions {
	
	public static LoopCondition getLoopCondition(RenderConditions[] conditions) {
		return (LoopCondition) conditions[conditions.length-1];
	}
	
	
	
	public enum LoopCondition implements RenderConditions{
		GAME,
		MAIN;
		
		public RenderConditions[] addLoopConditionTo(RenderConditions[] conditions) {
			RenderConditions[] c = Arrays.copyOf(conditions, conditions.length+1);
			c[c.length-1]=this;
			return c;
		}
	}

}
