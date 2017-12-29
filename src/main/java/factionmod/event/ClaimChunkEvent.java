package factionmod.event;

import java.util.UUID;

import factionmod.config.ConfigLang;
import factionmod.faction.Faction;
import factionmod.utils.DimensionalPosition;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a player claims a chunk for a faction. Cancel this
 * event will prevent the claim.
 *
 * @author BrokenSwing
 *
 */
@Cancelable
public class ClaimChunkEvent extends Event {

    private final Faction             faction;
    private final UUID                playerUUID;
    private final DimensionalPosition position;
    private String                    message;

    public ClaimChunkEvent(final Faction faction, final UUID playerUUID, final DimensionalPosition position) {
        this.faction = faction;
        this.playerUUID = playerUUID;
        this.position = position;
        this.message = ConfigLang.translate("player.self.permission.hasnt");
    }

    /**
     * Indicates which message will be displayed to the player if the event is
     * canceled.
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message which will be displayed to the layer if the event is
     * canceled.
     * 
     * @param message
     *            The new message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Returns the concerned faction, the faction whih will own the chunk.
     * 
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Returns the UUID of the player who tries to claim the chunk.
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
