package factionmod.event;

import java.util.UUID;

import factionmod.faction.Faction;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event called when a faction is created.
 *
 * @author BrokenSwing
 *
 */
public class FactionCreatedEvent extends Event {

    private final Faction faction;
    private final UUID    playerUUID;

    public FactionCreatedEvent(final Faction faction, final UUID player) {
        this.faction = faction;
        this.playerUUID = player;
    }

    /**
     * Returns the UUID of the player who created the faction.
     * 
     * @return the UUID of the player
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
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
