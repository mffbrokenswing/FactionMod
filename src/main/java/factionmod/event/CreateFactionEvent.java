package factionmod.event;

import java.util.UUID;

import factionmod.config.ConfigLanguage;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * Event called when a player tries to create a faction. The disponibility of
 * the name is already checked and the player is certified not being in a
 * faction. Set result to {@link Result#DENY} will prevent the creation of the
 * faction.
 * 
 * @author BrokenSwing
 *
 */
@HasResult
public class CreateFactionEvent extends Event {

	private final String	name;
	private final UUID		owner;
	private final String	description;
	private String			message;

	public CreateFactionEvent(String name, UUID owner, String description) {
		this.name = name;
		this.owner = owner;
		this.description = description;
		this.message = ConfigLanguage.missingPermission;
	}

	/**
	 * The name of the faction
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The UUID of the player which is trying to create the faction.
	 * 
	 * @return the UUID of the player
	 */
	public UUID getOwner() {
		return owner;
	}

	/**
	 * The description of the faction.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the message returned by the fonction which created the faction
	 * 
	 * @return
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets the message returned by the fonction which creates the faction. This
	 * message is returned only if the result is {@link Result#DENY}.
	 * 
	 * @param message
	 *            The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
