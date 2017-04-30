package factionmod.event;

import java.util.UUID;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.config.ConfigLanguage;
import factionmod.faction.Faction;
import factionmod.utils.DimensionalBlockPos;

/**
 * This event is fired when a player is attempting to change the location of the
 * home of a faction. Cancel this event will prevent the changement of the home
 * position.
 * 
 * @author BrokenSwing
 *
 */
@Cancelable
public class SetHomeEvent extends Event {

	private final Faction				faction;
	private final UUID					playerUUID;
	private String						message;
	private final DimensionalBlockPos	position;

	public SetHomeEvent(Faction faction, UUID player, DimensionalBlockPos position) {
		this.faction = faction;
		this.playerUUID = player;
		this.position = position;
		this.message = ConfigLanguage.missingPermission;
	}

	/**
	 * Indicates the new position of the home.
	 * 
	 * @return the position
	 */
	public DimensionalBlockPos getNewPosition() {
		return position;
	}

	/**
	 * Returns the current position of the faction's home. Can be null if the
	 * home isn't set.
	 * 
	 * @return the current position
	 */
	public DimensionalBlockPos getCurrentPosition() {
		return this.faction.getHome();
	}

	/**
	 * Returns the message which will be displayed to the player if the event is
	 * canceled. Default : {@link ConfigLanguage#missingPermission}.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message which will be displayed to the player if the event is
	 * canceled. A default message exists.
	 * 
	 * @param message
	 *            The message to display
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the faction which will get his home changed.
	 * 
	 * @return the faction
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * Returns the UUID of the player who is changing the home.
	 * 
	 * @return the UUID of the player
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
	}

}
