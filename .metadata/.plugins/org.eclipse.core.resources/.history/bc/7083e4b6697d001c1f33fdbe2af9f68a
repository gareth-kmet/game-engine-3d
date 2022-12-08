package game.objects.informations;

import game.objects.informations.Affected.AffectedByHealth;

public sealed interface Affectors extends Information{
	
	public non-sealed interface HealthAffecting extends Affectors, Death.CanKill{
		public float applyHealthEffect(Affected.AffectedByHealth o);
		public interface PercentHealthAffecting extends HealthAffecting{
			public float getHealthAffectPercent(Affected.AffectedByHealth o);
			@Override
			public default float applyHealthEffect(Affected.AffectedByHealth o) {
				return o.changeCurrentHealthByPercent(getHealthAffectPercent(o), this);
			}
		}
		public interface ValueHealthAffecting extends HealthAffecting{
			public float getHealthAffectValue(Affected.AffectedByHealth o);
			@Override
			public default float applyHealthEffect(Affected.AffectedByHealth o) {
				return o.changeCurrentHealthByPercent(getHealthAffectValue(o), this);
			}
		}
		public enum AbstractHealthAffectors implements HealthAffecting{
			THIRST, HUNGER, FALL_DAMAGE;
			@Override public float applyHealthEffect(AffectedByHealth o) {return 0;}
		}
	}

	public non-sealed interface ThirstAffecting extends Affectors{
		public float applyThirstEffect(Affected.AffectedByThirst o);
		public interface PercentThirstAffecting extends ThirstAffecting{
			public float getThirstAffectPercent(Affected.AffectedByThirst o);
			@Override
			public default float applyThirstEffect(Affected.AffectedByThirst o) {
				return o.changeCurrentThirstByPercent(getThirstAffectPercent(o));
			}
		}
		public interface ValueThirstAffecting extends ThirstAffecting{
			public float getThirstAffectValue(Affected.AffectedByThirst o);
			@Override
			public default float applyThirstEffect(Affected.AffectedByThirst o) {
				return o.changeCurrentThirstByPercent(getThirstAffectValue(o));
			}
		}
	}

	public non-sealed interface HungerAffecting extends Affectors{
		public float applyHungerEffect(Affected.AffectedByHunger o);
		public interface PercentHungerAffecting extends HungerAffecting{
			public float getHungerAffectPercent(Affected.AffectedByHunger o);
			@Override
			public default float applyHungerEffect(Affected.AffectedByHunger o) {
				return o.changeCurrentHungerByPercent(getHungerAffectPercent(o));
			}
		}
		public interface ValueHungerAffecting extends HungerAffecting{
			public float getHungerAffectValue(Affected.AffectedByHunger o);
			@Override
			public default float applyHungerEffect(Affected.AffectedByHunger o) {
				return o.changeCurrentHungerByPercent(getHungerAffectValue(o));
			}
		}
	}

}
