package factionmod.event;

import java.util.UUID;

import net.minecraftforge.fml.common.eventhandler.Event;
import factionmod.faction.Faction;

/**
 * This event is fired when a faction is disbanded.
 * 
 * @author BrokenSwing
 *
 */
public abstract class FactionDisbandedEvent extends Event {

	private final Faction	faction;
	private final UUID		playerUUID;

	public FactionDisbandedEvent(Faction faction, UUID playerUUID) {
		this.faction = faction;
		this.playerUUID = playerUUID;
	}

	/**
	 * The faction which is disbanded.
	 * 
	 * @return the faction
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * Returns the UUID of the player who disbanded the faction.
	 * 
	 * @return the UUID of the player
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
	}

	/**
	 * This event is fired before the members and the faction get removed.
	 * 
	 * @author BrokenSwing
	 *
	 */
	public static class Pre extends FactionDisbandedEvent {

		public Pre(Faction faction, UUID playerUUID) {
			super(faction, playerUUID);
		}

	}

	/**
	 * This event if fired once the members and the faction was removed.
	 * 
	 * @author BrokenSwing
	 *
	 */
	public static class Post extends FactionDisbandedEvent {

		public Post(Faction faction, UUID playerUUID) {
			super(faction, playerUUID);
		}

	}

}
