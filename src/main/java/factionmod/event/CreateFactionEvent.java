package factionmod.event;

import java.util.UUID;

import factionmod.config.ConfigLang;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event called when a player tries to create a faction. The disponibility of
 * the name is already checked and the player is certified not being in a
 * faction. Cancel this event will prevent the creation of the faction.
 *
 * @author BrokenSwing
 *
 */
@Cancelable
public class CreateFactionEvent extends Event {

    private final String name;
    private final UUID   owner;
    private final String description;
    private String       message;

    public CreateFactionEvent(final String name, final UUID owner, final String description) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.message = ConfigLang.translate("player.self.permission.hasnt");
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
     * message is returned only if the event is canceled.
     * 
     * @param message
     *            The message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

}
