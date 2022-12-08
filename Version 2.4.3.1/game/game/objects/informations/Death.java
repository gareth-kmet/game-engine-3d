package game.objects.informations;

import java.util.Collection;

import game.objects.GameObject;

public sealed interface Death extends Information{

	public non-sealed interface CanDie extends Death{
		public boolean isDead();
		public boolean die();
		public interface DiesOnDespawn extends CanDie{}
		public interface CanBeKilled extends CanDie{
			public default boolean isKilled() {
				return isDead()&&getKiller()!=null;
			}
			public Death.CanKill getKiller();
			public boolean kill(Death.CanKill killer);
		}
	}

	public non-sealed interface CanKill extends Death{}

	public sealed interface Drops extends Death{
		public Collection<? extends GameObject> drops();
		public non-sealed interface DropsOnDespawn extends Drops{};
		public non-sealed interface DropsOnDeath extends Drops, CanDie{};
		public non-sealed interface DropsOnKill extends Drops, CanDie.CanBeKilled{};
	}

}
