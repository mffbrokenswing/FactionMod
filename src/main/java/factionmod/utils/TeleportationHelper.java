package factionmod.utils;

import java.util.UUID;

import factionmod.handler.EventHandlerTeleportation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

/**
 * Contains methods to teleport entities.
 *
 * @author BrokenSwing
 *
 */
public class TeleportationHelper {

    /**
     * Teleports an entity to a position in the world.
     * 
     * @param entity
     *            The entity to teleport
     * @param pos
     *            Where to teleport the entity
     */
    public static void teleport(final Entity entity, final BlockPos pos) {
        entity.setPositionAndUpdate(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
    }

    /**
     * Teleports a player to a position in the world.
     * 
     * @param uuid
     *            The UUID of the player
     * @param pos
     *            The position point
     */
    public static void teleport(final UUID uuid, final BlockPos pos) {
        final EntityPlayer player = ServerUtils.getPlayer(uuid);
        if (player != null)
            teleport(player, pos);
    }

    /**
     * Teleports the entity after a delay to a position in the world.
     * 
     * @param entity
     *            The entity to teleport
     * @param pos
     *            Where to teleport the entity
     * @param sec
     *            The delay before teleportation
     */
    public static void teleport(final EntityPlayer player, final BlockPos pos, final int sec) {
        if (sec == 0)
            teleport(player, pos);
        else
            EventHandlerTeleportation.teleport(player, pos, sec);
    }

}
