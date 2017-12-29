package factionmod.event;

import factionmod.config.ConfigLang;
import factionmod.faction.Faction;
import factionmod.utils.DimensionalBlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a player is requesting to teleport to the home of a
 * faction. Cancel this event will prevent the teleportation.
 *
 * @author BrokenSwing
 *
 */
@Cancelable
public class HomeTeleportationEvent extends Event {

    private final Faction        faction;
    private final EntityPlayerMP player;
    private String               message;

    public HomeTeleportationEvent(final Faction faction, final EntityPlayerMP player) {
        this.faction = faction;
        this.player = player;
        this.message = ConfigLang.translate("player.self.permission.hasnt");
    }

    /**
     * Returns the faction
     * 
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Returns the position of the home.
     * 
     * @return the position
     */
    public DimensionalBlockPos getHomePosition() {
        return this.faction.getHome();
    }

    /**
     * Returns the player trying to teleport.
     * 
     * @return the player
     */
    public EntityPlayerMP getPlayer() {
        return this.player;
    }

    /**
     * Indicates the message which will be displayed to the player if the event is
     * canceled.
     * 
     * @return the message
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

}
