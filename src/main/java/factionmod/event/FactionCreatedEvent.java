package factionmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.faction.Faction;

/**
 * Event called when a faction is created.
 * 
 * @author BrokenSwing
 *
 */
public class FactionCreatedEvent extends Event {

	private final Faction	faction;

	public FactionCreatedEvent(Faction faction) {
		this.faction = faction;
	}

	/**
	 * Returns the faction created.
	 * 
	 * @return the faction
	 */
	public Faction getFaction() {
		return faction;
	}

}
