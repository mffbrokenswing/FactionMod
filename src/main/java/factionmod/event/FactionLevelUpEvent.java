package factionmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.faction.Faction;

/**
 * This event is fired when a faction levels up. This is not cancelable. This
 * has no result.
 * 
 * @author BrokenSwing
 *
 */
public class FactionLevelUpEvent extends Event {

	private final Faction	faction;

	public FactionLevelUpEvent(Faction faction) {
		this.faction = faction;
	}

	/**
	 * The faction which levels up.
	 * 
	 * @return The faction
	 */
	public Faction getFaction() {
		return this.faction;
	}

}
