package factionmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.faction.Faction;

/**
 * This event is fired when a faction levels up.
 * 
 * @author BrokenSwing
 *
 */
public class FactionLevelUpEvent extends Event {

	private final Faction	faction;

	public FactionLevelUpEvent(Faction faction) {
		this.faction = faction;
	}

	public Faction getFaction() {
		return this.faction;
	}

}
