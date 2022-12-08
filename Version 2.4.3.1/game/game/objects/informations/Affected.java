package game.objects.informations;

public sealed interface Affected extends Information{

	public non-sealed interface AffectedByHealth extends Affected, Death.CanDie.CanBeKilled{
		public float getMaxHealth();
		public float getCurrentHealth();
		public default float getCurrentPercentHealth() {
			return getCurrentHealth()/getMaxHealth();
		}
		public float setCurrentHealth(float health, Affectors.HealthAffecting object);
		public default boolean testForDeath(Affectors.HealthAffecting object) {
			if(getCurrentHealth()>0)return false;
			else {
				kill(object);
				return true;
			}
		}
		public default float setCurrentHealthByPercent(float percent, Affectors.HealthAffecting object) {
			float health = percent * getMaxHealth();
			return setCurrentHealth(health, object);
		}
		public default float changeCurrentHealth(float deltaHealth, Affectors.HealthAffecting object) {
			return setCurrentHealth(getCurrentHealth() + deltaHealth, object);
		}
		public default float changeCurrentHealthByPercent(float deltaPercent, Affectors.HealthAffecting object) {
			float health = deltaPercent * getMaxHealth();
			return changeCurrentHealth(health, object);
		}
	}

	public non-sealed interface AffectedByThirst extends Affected{
		public float getMaxThirst();
		public float getCurrentThirst();
		public default float getCurrentPercentThirst() {
			return getCurrentThirst()/getMaxThirst();
		}
		public float setCurrentThirst(float thirst);
		public default float setCurrentThirstByPercent(float percent) {
			float thirst = percent * getMaxThirst();
			return setCurrentThirst(thirst);
		}
		public default float changeCurrentThirst(float deltaThirst) {
			return setCurrentThirst(getCurrentThirst() + deltaThirst);
		}
		public default float changeCurrentThirstByPercent(float deltaPercent) {
			float thirst = deltaPercent * getMaxThirst();
			return changeCurrentThirst(thirst);
		}
	}

	public non-sealed interface AffectedByHunger extends Affected{
		public float getMaxHunger();
		public float getCurrentHunger();
		public default float getCurrentPercentHunger() {
			return getCurrentHunger()/getMaxHunger();
		}
		public float setCurrentHunger(float hunger);
		public default float setCurrentHungerByPercent(float percent) {
			float hunger = percent * getMaxHunger();
			return setCurrentHunger(hunger);
		}
		public default float changeCurrentHunger(float deltaHunger) {
			return setCurrentHunger(getCurrentHunger() + deltaHunger);
		}
		public default float changeCurrentHungerByPercent(float deltaPercent) {
			float hunger = deltaPercent * getMaxHunger();
			return changeCurrentHunger(hunger);
		}
	}

}
