package factionmod.event;

import java.util.UUID;

import factionmod.config.ConfigLang;
import factionmod.faction.Faction;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a player tries to join a faction. Cancel this event
 * will prevent the player to join it.
 *
 * @author BrokenSwing
 *
 */
@Cancelable
public class JoinFactionEvent extends Event {

    private final Faction faction;
    private final UUID    playerUUID;
    private String        message;

    public JoinFactionEvent(final Faction faction, final UUID playerUUID) {
        this.faction = faction;
        this.playerUUID = playerUUID;
        this.message = ConfigLang.translate("player.self.permission.hasnt");
    }

    /**
     * Indicates the message which will be displayed to the player if the event is
     * canceled.
     * 
     * @return the displayed message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message which will be displayed to the player if the event is
     * canceled.
     * 
     * @param message
     *            The new message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Returns the faction the player wants to join.
     * 
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Returns the UUID of the player who wants to join the faction.
     * 
     * @return the UUID of the player
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

}
