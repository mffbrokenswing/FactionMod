package factionmod.event;

import java.util.UUID;

import factionmod.faction.Faction;
import factionmod.utils.DimensionalPosition;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a player unclaim a chunk.
 *
 * @author BrokenSwing
 *
 */
public class UnclaimChunkEvent extends Event {

    private final Faction             faction;
    private final UUID                playerUUID;
    private final DimensionalPosition position;

    public UnclaimChunkEvent(final Faction faction, final UUID playerUUID, final DimensionalPosition position) {
        this.faction = faction;
        this.playerUUID = playerUUID;
        this.position = position;
    }

    /**
     * Returns the faction which own the chunk.
     * 
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Returns the UUID of the player who unclaims the chunk.
     * 
     * @return the UUID of the player
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Indicates the position of the chunk.
     * 
     * @return the position
     */
    public DimensionalPosition getPosition() {
        return position;
    }

}
