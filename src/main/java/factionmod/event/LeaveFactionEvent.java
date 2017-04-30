package factionmod.event;

import java.util.UUID;

import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.faction.Faction;

/**
 * This event is fired when a player leaves a faction.
 * 
 * @author BrokenSwing
 *
 */
public class LeaveFactionEvent extends Event {

	private final Faction	faction;
	private final UUID		playerUUID;

	public LeaveFactionEvent(Faction faction, UUID playerUUID) {
		this.faction = faction;
		this.playerUUID = playerUUID;
	}

	/**
	 * Returns the faction the player leaves.
	 * 
	 * @return the faction
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * Returns the UUID of the player who leaves the faction.
	 * 
	 * @return the UUID of the player
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
	}

}
