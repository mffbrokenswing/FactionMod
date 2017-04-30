package factionmod.event;

import java.util.UUID;

import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.faction.Faction;

/**
 * This event is fired when a player is attempting to change the description of
 * a faction. You can use it to modify the new description (remove dirty words,
 * cut it, add extras ..).
 * 
 * @author BrokenSwing
 *
 */
public class DescriptionChangedEvent extends Event {

	private final Faction	faction;
	private final UUID		playerUUID;
	private String			newDescription;

	public DescriptionChangedEvent(Faction faction, UUID playerUUID, String newDescription) {
		this.faction = faction;
		this.playerUUID = playerUUID;
		this.newDescription = newDescription;
	}

	/**
	 * Returns the new description of the faction.
	 * 
	 * @return the new description
	 */
	public String getNewDescription() {
		return newDescription;
	}

	/**
	 * Returns the current description of the faction.
	 * 
	 * @return the current description
	 */
	public String getCurrentDescription() {
		return this.faction.getDesc();
	}

	/**
	 * Changes the new description.
	 * 
	 * @param newDescription
	 *            The new description
	 */
	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	/**
	 * Returns the concerned faction.
	 * 
	 * @return the faction
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * Returns the UUID of the player modifying the description of the faction.
	 * 
	 * @return the UUID of the player
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
	}

}
