package factionmod.handler;

import java.util.ArrayList;
import java.util.UUID;

import factionmod.FactionMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

/**
 * Handles the permissions of the admins. It also store them.
 *
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerAdmin {

    private static final ArrayList<EntityPlayerMP> ADMINS = new ArrayList<>();

    /**
     * Used to remove admins when they disconnect.
     * 
     * @param event
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(final PlayerLoggedOutEvent event) {
        removeAdmin((EntityPlayerMP) event.player);
    }

    /**
     * Adds an admins.
     * 
     * @param player
     *            The player
     */
    public static void addAdmin(final EntityPlayerMP player) {
        if (!ADMINS.contains(player)) {
            ADMINS.add(player);
            player.refreshDisplayName();
        }
    }

    /**
     * Removes an admin.
     * 
     * @param player
     *            The player
     */
    public static void removeAdmin(final EntityPlayerMP player) {
        ADMINS.remove(player);
        player.refreshDisplayName();
    }

    /**
     * Indicates if a player is admin.
     * 
     * @param player
     *            The player
     * @return true if the player is admin
     */
    public static boolean isAdmin(final EntityPlayerMP player) {
        return ADMINS.contains(player);
    }

    /**
     * Indicates if the player with the specified {@link UUID} is admin.
     * 
     * @param uuid
     *            The UUID of the player
     * @return true if the player if admin
     */
    public static boolean isAdmin(final UUID uuid) {
        for (final EntityPlayer player : ADMINS)
            if (player.getUniqueID().equals(uuid))
                return true;
        return false;
    }

}
